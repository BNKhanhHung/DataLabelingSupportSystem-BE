package com.anotation.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TaskResponse {

    private UUID id;
    private UUID projectId;
    private String projectName;
    private UUID annotatorId;
    private String annotatorUsername;
    private UUID reviewerId;
    private String reviewerUsername;
    private TaskStatus status;
    private List<UUID> dataItemIds;
    private LocalDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public UUID getAnnotatorId() {
        return annotatorId;
    }

    public void setAnnotatorId(UUID annotatorId) {
        this.annotatorId = annotatorId;
    }

    public String getAnnotatorUsername() {
        return annotatorUsername;
    }

    public void setAnnotatorUsername(String annotatorUsername) {
        this.annotatorUsername = annotatorUsername;
    }

    public UUID getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(UUID reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getReviewerUsername() {
        return reviewerUsername;
    }

    public void setReviewerUsername(String reviewerUsername) {
        this.reviewerUsername = reviewerUsername;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public List<UUID> getDataItemIds() {
        return dataItemIds;
    }

    public void setDataItemIds(List<UUID> dataItemIds) {
        this.dataItemIds = dataItemIds;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
