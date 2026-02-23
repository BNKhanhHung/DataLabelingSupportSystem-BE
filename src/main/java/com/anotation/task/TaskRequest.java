package com.anotation.task;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public class TaskRequest {

    @NotNull(message = "Project ID is required")
    private UUID projectId;

    @NotNull(message = "Annotator ID is required")
    private UUID annotatorId;

    @NotNull(message = "Reviewer ID is required")
    private UUID reviewerId;

    @NotEmpty(message = "At least one DataItem must be selected")
    private List<UUID> dataItemIds;

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

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

    public List<UUID> getDataItemIds() {
        return dataItemIds;
    }

    public void setDataItemIds(List<UUID> dataItemIds) {
        this.dataItemIds = dataItemIds;
    }
}
