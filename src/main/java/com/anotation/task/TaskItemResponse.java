package com.anotation.task;

import java.util.UUID;

/**
 * DTO một dòng task item cho UI annotator: định danh task item/data item, URL nội dung hiển thị,
 * và cờ {@code hasAnnotation} cho biết đã có bản ghi annotation hay chưa (tránh nộp trùng).
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
