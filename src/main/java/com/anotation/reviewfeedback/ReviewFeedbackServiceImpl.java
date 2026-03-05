package com.anotation.reviewfeedback;

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

    public ReviewFeedbackServiceImpl(ReviewFeedbackRepository reviewFeedbackRepository,
            AnnotationRepository annotationRepository,
            TaskItemRepository taskItemRepository,
            TaskRepository taskRepository,
            DataItemRepository dataItemRepository,
            UserRepository userRepository,
            ReviewMapper reviewMapper) {
        this.reviewFeedbackRepository = reviewFeedbackRepository;
        this.annotationRepository = annotationRepository;
        this.taskItemRepository = taskItemRepository;
        this.taskRepository = taskRepository;
        this.dataItemRepository = dataItemRepository;
        this.userRepository = userRepository;
        this.reviewMapper = reviewMapper;
    }

    // ── Read ─────────────────────────────────────────────────────────────────────

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

    @Override
    @Transactional(readOnly = true)
    public ReviewResponse getById(UUID id) {
        return reviewMapper.toResponse(findOrThrow(id));
    }

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

        // ── Status transitions (chỉ cập nhật Annotation + DataItem, Task do Reviewer
        // quyết) ──

        if (request.getStatus() == ReviewStatus.APPROVED) {
            annotation.setStatus(AnnotationStatus.APPROVED);
            annotationRepository.save(annotation);

            dataItem.setStatus(DataItemStatus.REVIEWED);
            dataItemRepository.save(dataItem);

        } else {
            // REJECTED → Task chuyển sang DENIED
            annotation.setStatus(AnnotationStatus.REJECTED);
            annotationRepository.save(annotation);

            dataItem.setStatus(DataItemStatus.ASSIGNED);
            dataItemRepository.save(dataItem);

            task.setStatus(TaskStatus.DENIED);
            taskRepository.save(task);
        }

        return reviewMapper.toResponse(feedback);
    }
    // ── Delete ───────────────────────────────────────────────────────────────────

    @Override
    public void delete(UUID id) {
        findOrThrow(id);
        reviewFeedbackRepository.deleteById(id);
    }

    // ── Private helpers ─────────────────────────────────────────────────────────

    private ReviewFeedback findOrThrow(UUID id) {
        return reviewFeedbackRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ReviewFeedback not found with id: " + id));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BadRequestException("Unauthorized.");
        }
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new BadRequestException("Invalid token or user not found."));
    }
}
