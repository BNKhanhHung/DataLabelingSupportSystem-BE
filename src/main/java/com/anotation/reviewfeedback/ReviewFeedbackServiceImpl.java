package com.anotation.reviewfeedback;

import com.anotation.activitylog.ActivityAction;
import com.anotation.activitylog.ActivityLogService;

import com.anotation.common.PageResponse;
import com.anotation.exception.BadRequestException;
import com.anotation.exception.DuplicateException;
import com.anotation.exception.NotFoundException;
import com.anotation.annotation.Annotation;
import com.anotation.annotation.AnnotationRepository;
import com.anotation.annotation.AnnotationStatus;
import com.anotation.dataitem.DataItem;
import com.anotation.dataitem.DataItemRepository;
import com.anotation.dataitem.DataItemStatus;
import com.anotation.task.Task;
import com.anotation.task.TaskItemRepository;
import com.anotation.task.TaskRepository;
import com.anotation.task.TaskStatus;
import com.anotation.user.User;
import com.anotation.user.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Triển khai {@link ReviewFeedbackService}: đọc phản hồi review, gửi review với đầy đủ
 * kiểm tra bảo mật/nghiệp vụ (task SUBMITTED, user hiện tại là reviewer của task, annotation
 * SUBMITTED, không trùng, comment khi REJECTED), và cập nhật {@link Annotation},
 * {@link DataItem} tương ứng.
 */
@Service
@Transactional
public class ReviewFeedbackServiceImpl implements ReviewFeedbackService {

    private final ReviewFeedbackRepository reviewFeedbackRepository;
    private final AnnotationRepository annotationRepository;
    private final TaskItemRepository taskItemRepository;
    private final TaskRepository taskRepository;
    private final DataItemRepository dataItemRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;
    private final ActivityLogService activityLogService;

    /**
     * @param reviewFeedbackRepository kho lưu {@link ReviewFeedback}
     * @param annotationRepository     cập nhật trạng thái annotation
     * @param taskItemRepository       kho {@link com.anotation.task.TaskItem} (inject theo kiến trúc; có thể phục vụ mở rộng luồng review)
     * @param taskRepository           truy cập task khi validate
     * @param dataItemRepository       cập nhật trạng thái data item
     * @param userRepository           resolve user từ SecurityContext
     * @param reviewMapper             entity → {@link ReviewResponse}
     */
    public ReviewFeedbackServiceImpl(ReviewFeedbackRepository reviewFeedbackRepository,
            AnnotationRepository annotationRepository,
            TaskItemRepository taskItemRepository,
            TaskRepository taskRepository,
            DataItemRepository dataItemRepository,
            UserRepository userRepository,
            ReviewMapper reviewMapper,
            ActivityLogService activityLogService) {
        this.reviewFeedbackRepository = reviewFeedbackRepository;
        this.annotationRepository = annotationRepository;
        this.taskItemRepository = taskItemRepository;
        this.taskRepository = taskRepository;
        this.dataItemRepository = dataItemRepository;
        this.userRepository = userRepository;
        this.reviewMapper = reviewMapper;
        this.activityLogService = activityLogService;
    }

    // ── Read ─────────────────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getAll(Pageable pageable) {
        try {
            return PageResponse.from(reviewFeedbackRepository.findAll(pageable), reviewMapper::toResponse);
        } catch (PropertyReferenceException e) {
            Pageable safe = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id"));
            return PageResponse.from(reviewFeedbackRepository.findAll(safe), reviewMapper::toResponse);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ReviewResponse getById(UUID id) {
        return reviewMapper.toResponse(findOrThrow(id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getByTask(UUID taskId, Pageable pageable) {
        try {
            return PageResponse.from(reviewFeedbackRepository.findByTaskId(taskId, pageable),
                    reviewMapper::toResponse);
        } catch (PropertyReferenceException e) {
            Pageable safe = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id"));
            return PageResponse.from(reviewFeedbackRepository.findByTaskId(taskId, safe),
                    reviewMapper::toResponse);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getByReviewer(UUID reviewerId, Pageable pageable) {
        try {
            return PageResponse.from(reviewFeedbackRepository.findByReviewerId(reviewerId, pageable),
                    reviewMapper::toResponse);
        } catch (PropertyReferenceException e) {
            Pageable safe = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id"));
            return PageResponse.from(reviewFeedbackRepository.findByReviewerId(reviewerId, safe),
                    reviewMapper::toResponse);
        }
    }

    // ── Review (Create) ──────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * <p>
     * Luồng: validate task {@link TaskStatus#SUBMITTED}, user đăng nhập khớp {@code reviewerId}
     * và khớp reviewer gán trên task; annotation phải {@link AnnotationStatus#SUBMITTED};
     * không cho review hai lần; nếu {@link ReviewStatus#REJECTED} thì bắt buộc comment.
     * Sau đó lưu {@link ReviewFeedback} và cập nhật annotation/data item (APPROVED → data item
     * REVIEWED; REJECTED → data item ASSIGNED để annotator làm lại). Trạng thái task tổng thể
     * chỉ đổi khi reviewer gọi hoàn tất review ở tầng task.
     */
    @Override
    public ReviewResponse review(ReviewRequest request) {
        // 1. Annotation must exist
        Annotation annotation = annotationRepository.findById(request.getAnnotationId())
                .orElseThrow(() -> new NotFoundException(
                        "Annotation not found: " + request.getAnnotationId()));

        // Navigate up to Task for validations
        Task task = annotation.getTaskItem().getTask();
        DataItem dataItem = annotation.getTaskItem().getDataItem();

        // 1.5 Task must be SUBMITTED (Annotator đã nộp, Reviewer mới được review)
        if (task.getStatus() != TaskStatus.SUBMITTED) {
            throw new BadRequestException(
                    "Task must be SUBMITTED to review annotations. Current status: " + task.getStatus());
        }

        // 2. Authenticated user must match request reviewer
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(request.getReviewerId())) {
            throw new BadRequestException("Authenticated user does not match reviewer.");
        }

        // 3. Only Task.reviewer can review
        UUID taskReviewerId = task.getReviewer().getId();
        if (!taskReviewerId.equals(currentUser.getId())) {
            throw new BadRequestException(
                    "User " + currentUser.getId() + " is not the reviewer of this task.");
        }

        // 4. Reviewer user must exist
        User reviewer = currentUser;

        // 5. Annotation must be SUBMITTED (not already reviewed)
        if (annotation.getStatus() != AnnotationStatus.SUBMITTED) {
            throw new BadRequestException(
                    "Annotation is not SUBMITTED (current: " + annotation.getStatus() + ")");
        }

        // 6. Cannot review twice (unique annotation_id)
        if (reviewFeedbackRepository.existsByAnnotationId(annotation.getId())) {
            throw new DuplicateException(
                    "A review already exists for annotation: " + annotation.getId());
        }

        // 7. Comment required when REJECTED
        if (request.getStatus() == ReviewStatus.REJECTED
                && (request.getComment() == null || request.getComment().isBlank())) {
            throw new BadRequestException("Comment is required when rejecting an annotation.");
        }

        // Create ReviewFeedback
        ReviewFeedback feedback = new ReviewFeedback();
        feedback.setAnnotation(annotation);
        feedback.setReviewer(reviewer);
        feedback.setStatus(request.getStatus());
        feedback.setComment(request.getComment());
        reviewFeedbackRepository.save(feedback);

        // Ghi nhật ký hoạt động
        activityLogService.log(currentUser, ActivityAction.REVIEW_SUBMITTED, "REVIEW", feedback.getId(),
                "Reviewer " + currentUser.getUsername() + " review annotation "
                + annotation.getId() + " → " + request.getStatus());

        // ── Status transitions (chỉ cập nhật Annotation + DataItem, Task do Reviewer
        // quyết) ──

        if (request.getStatus() == ReviewStatus.APPROVED) {
            annotation.setStatus(AnnotationStatus.APPROVED);
            annotationRepository.save(annotation);

            dataItem.setStatus(DataItemStatus.REVIEWED);
            dataItemRepository.save(dataItem);

        } else {
            // REJECTED → chỉ cập nhật annotation và dataItem. Trạng thái task chỉ đổi khi reviewer hoàn tất review (complete-review).
            annotation.setStatus(AnnotationStatus.REJECTED);
            annotationRepository.save(annotation);

            dataItem.setStatus(DataItemStatus.ASSIGNED);
            dataItemRepository.save(dataItem);
        }

        return reviewMapper.toResponse(feedback);
    }

    // ── Delete ───────────────────────────────────────────────────────────────────

    @Override
    public void delete(UUID id) {
        ReviewFeedback feedback = findOrThrow(id);
        User currentUser = getCurrentUser();

        // Ghi nhật ký TRƯỚC khi xóa
        activityLogService.log(currentUser, ActivityAction.REVIEW_DELETED, "REVIEW", id,
                "Xóa review feedback cho annotation: " + feedback.getAnnotation().getId());

        reviewFeedbackRepository.deleteById(id);
    }

    // ── Private helpers ─────────────────────────────────────────────────────────

    /**
     * Tìm {@link ReviewFeedback} theo id hoặc ném {@link NotFoundException}.
     *
     * @param id UUID
     * @return entity
     */
    private ReviewFeedback findOrThrow(UUID id) {
        return reviewFeedbackRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ReviewFeedback not found with id: " + id));
    }

    /**
     * Lấy {@link User} tương ứng principal hiện tại trong {@link SecurityContextHolder}.
     *
     * @return user đã xác thực
     * @throws BadRequestException nếu chưa đăng nhập hoặc không tìm thấy user theo username
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BadRequestException("Unauthorized.");
        }
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new BadRequestException("Invalid token or user not found."));
    }
}
