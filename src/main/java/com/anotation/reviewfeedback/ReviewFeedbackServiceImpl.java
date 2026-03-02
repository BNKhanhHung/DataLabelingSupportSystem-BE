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
import org.springframework.data.domain.Pageable;
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
        return PageResponse.from(reviewFeedbackRepository.findAll(pageable), reviewMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponse getById(UUID id) {
        return reviewMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getByTask(UUID taskId, Pageable pageable) {
        return PageResponse.from(reviewFeedbackRepository.findByTaskId(taskId, pageable),
                reviewMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReviewResponse> getByReviewer(UUID reviewerId, Pageable pageable) {
        return PageResponse.from(reviewFeedbackRepository.findByReviewerId(reviewerId, pageable),
                reviewMapper::toResponse);
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

        // ── Status transitions ────────────────────────────────────────────────────

        if (request.getStatus() == ReviewStatus.APPROVED) {
            // Annotation.status → APPROVED
            annotation.setStatus(AnnotationStatus.APPROVED);
            annotationRepository.save(annotation);

            // DataItem.status → REVIEWED
            dataItem.setStatus(DataItemStatus.REVIEWED);
            dataItemRepository.save(dataItem);

            // Task.status → COMPLETED if ALL DataItems are REVIEWED
            long nonReviewed = taskItemRepository.countNonReviewedItemsInTask(task.getId());
            if (nonReviewed == 0) {
                task.setStatus(TaskStatus.COMPLETED);
                taskRepository.save(task);
            }

        } else {
            // REJECTED
            // Annotation.status → REJECTED
            annotation.setStatus(AnnotationStatus.REJECTED);
            annotationRepository.save(annotation);

            // DataItem.status → ASSIGNED (reset for re-annotation)
            dataItem.setStatus(DataItemStatus.ASSIGNED);
            dataItemRepository.save(dataItem);
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
