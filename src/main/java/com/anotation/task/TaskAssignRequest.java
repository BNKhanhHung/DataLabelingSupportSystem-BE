package com.anotation.task;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Body PATCH khi Manager/Admin gán lại annotator và reviewer cho một {@link Task} đã tồn tại.
 * <p>
 * Cả hai id đều bắt buộc; quyền và điều kiện trạng thái task do {@link TaskService} kiểm tra.
 */
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

