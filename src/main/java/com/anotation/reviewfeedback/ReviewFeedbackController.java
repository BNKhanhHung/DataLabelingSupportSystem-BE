package com.anotation.reviewfeedback;

import com.anotation.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/review-feedbacks")
@Tag(name = "ReviewFeedback", description = "Review feedback APIs")
public class ReviewFeedbackController {

    private final ReviewFeedbackService reviewFeedbackService;

    public ReviewFeedbackController(ReviewFeedbackService reviewFeedbackService) {
        this.reviewFeedbackService = reviewFeedbackService;
    }

    @GetMapping
    @Operation(summary = "Get all review feedbacks")
    public ResponseEntity<PageResponse<ReviewResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(reviewFeedbackService.getAll(pageable)); // 200
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get review feedback by ID")
    public ResponseEntity<ReviewResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(reviewFeedbackService.getById(id)); // 200
    }

    @GetMapping("/task/{taskId}")
    @Operation(summary = "Get all review feedbacks in a task")
    public ResponseEntity<PageResponse<ReviewResponse>> getByTask(
            @PathVariable UUID taskId,
            Pageable pageable) {
        return ResponseEntity.ok(reviewFeedbackService.getByTask(taskId, pageable)); // 200
    }

    @GetMapping("/reviewer/{reviewerId}")
    @Operation(summary = "Get all review feedbacks submitted by a reviewer")
    public ResponseEntity<PageResponse<ReviewResponse>> getByReviewer(
            @PathVariable UUID reviewerId,
            Pageable pageable) {
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
