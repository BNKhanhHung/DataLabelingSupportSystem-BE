package com.anotation.task;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/** Request body cho Manager đổi phân công annotator/reviewer của task. */
public class TaskAssignRequest {

    @NotNull(message = "Annotator ID is required")
    private UUID annotatorId;

    @NotNull(message = "Reviewer ID is required")
    private UUID reviewerId;

    public UUID getAnnotatorId() {
        return annotatorId;
    }

    public void setAnnotatorId(UUID annotatorId) {
        this.annotatorId = annotatorId;
    }

    public UUID getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(UUID reviewerId) {
        this.reviewerId = reviewerId;
    }
}

