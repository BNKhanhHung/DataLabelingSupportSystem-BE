package com.anotation.reviewfeedback;

import com.anotation.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * ReviewFeedback API: reviewer gửi duyệt/từ chối từng annotation (POST body: annotationId, reviewerId, status, comment). Trạng thái task chỉ đổi khi hoàn tất review (TaskService.completeReview).
 * GET /, /{id}, /task/{taskId}, /reviewer/{reviewerId}; POST / (submit review); DELETE /{id}.
 */
@RestController
@RequestMapping("/api/review-feedbacks")
@Tag(name = "ReviewFeedback", description = "Review feedback APIs")
public class ReviewFeedbackController {

    private final ReviewFeedbackService reviewFeedbackService;

    public ReviewFeedbackController(ReviewFeedbackService reviewFeedbackService) {
        this.reviewFeedbackService = reviewFeedbackService;
    }

    @GetMapping
    @Operation(summary = "Get all review feedbacks",
            description = "Sort hợp lệ: id, status, createdAt (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<ReviewResponse>> getAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(reviewFeedbackService.getAll(pageable)); // 200
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get review feedback by ID")
    public ResponseEntity<ReviewResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(reviewFeedbackService.getById(id)); // 200
    }

    @GetMapping("/task/{taskId}")
    @Operation(summary = "Get all review feedbacks in a task",
            description = "Sort hợp lệ: id, createdAt (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<ReviewResponse>> getByTask(
            @PathVariable UUID taskId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(reviewFeedbackService.getByTask(taskId, pageable)); // 200
    }

    @GetMapping("/reviewer/{reviewerId}")
    @Operation(summary = "Get all review feedbacks submitted by a reviewer",
            description = "Sort hợp lệ: id, createdAt (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<ReviewResponse>> getByReviewer(
            @PathVariable UUID reviewerId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(reviewFeedbackService.getByReviewer(reviewerId, pageable)); // 200
    }

    @PostMapping
    @Operation(summary = "Submit a review for an annotation")
    public ResponseEntity<ReviewResponse> review(@Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewFeedbackService.review(request)); // 201
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a review feedback")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        reviewFeedbackService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
