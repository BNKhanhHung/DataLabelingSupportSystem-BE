package com.anotation.task;

import java.util.UUID;

/**
 * DTO for one item in a task (data item assigned to annotator).
 * Used by annotator labeling UI: taskItemId for submit annotation, contentUrl to display.
 */
public class TaskItemResponse {

    private UUID taskItemId;
    private UUID dataItemId;
    private String contentUrl;
    private boolean hasAnnotation;

    public TaskItemResponse() {
    }

    public TaskItemResponse(UUID taskItemId, UUID dataItemId, String contentUrl, boolean hasAnnotation) {
        this.taskItemId = taskItemId;
        this.dataItemId = dataItemId;
        this.contentUrl = contentUrl;
        this.hasAnnotation = hasAnnotation;
    }

    public UUID getTaskItemId() {
        return taskItemId;
    }

    public void setTaskItemId(UUID taskItemId) {
        this.taskItemId = taskItemId;
    }

    public UUID getDataItemId() {
        return dataItemId;
    }

    public void setDataItemId(UUID dataItemId) {
        this.dataItemId = dataItemId;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public boolean isHasAnnotation() {
        return hasAnnotation;
    }

    public void setHasAnnotation(boolean hasAnnotation) {
        this.hasAnnotation = hasAnnotation;
    }
}
