package com.anotation.reviewfeedback;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Body request khi reviewer gửi kết quả duyệt cho một annotation.
 * <p>
 * {@link #annotationId} và {@link #reviewerId} bắt buộc; {@link #status} phải là
 * {@link ReviewStatus#APPROVED} hoặc {@link ReviewStatus#REJECTED}. Khi từ chối,
 * {@link #comment} bắt buộc (kiểm tra thêm ở tầng service).
 */
public class ReviewRequest {

    @NotNull(message = "Annotation ID is required")
    private UUID annotationId;

    @NotNull(message = "Reviewer ID is required")
    private UUID reviewerId;

    @NotNull(message = "Status is required (APPROVED or REJECTED)")
    private ReviewStatus status;

    // Required when status = REJECTED; optional when APPROVED
    private String comment;

    public UUID getAnnotationId() {
        return annotationId;
    }

    public void setAnnotationId(UUID annotationId) {
        this.annotationId = annotationId;
    }

    public UUID getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(UUID reviewerId) {
        this.reviewerId = reviewerId;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
