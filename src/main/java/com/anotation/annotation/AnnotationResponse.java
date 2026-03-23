package com.anotation.annotation;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO JSON trả về cho client sau các thao tác đọc/ghi annotation ({@code GET/POST/PATCH} trên {@code /api/annotations}).
 * {@code id}: id annotation; {@code taskItemId}, {@code dataItemId}, {@code contentUrl}: bối cảnh dữ liệu và đường dẫn nội dung (ảnh/URL).
 * {@code annotatorId}, {@code annotatorUsername}: người đã gán nhãn; {@code content}: nội dung nhãn; {@code status}: {@link AnnotationStatus}.
 * {@code createdAt}, {@code updatedAt}: mốc thời gian lưu trong DB; được điền bởi {@link AnnotationMapper#toResponse(Annotation)}.
 * Cấu trúc phẳng giúp frontend hiển thị danh sách và chi tiết mà không cần join thủ công nhiều lớp nested.
 */
public class AnnotationResponse {

    private UUID id;
    private UUID taskItemId;
    private UUID dataItemId;
    private String contentUrl;
    private UUID annotatorId;
    private String annotatorUsername;
    private String content;
    private AnnotationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public AnnotationStatus getStatus() {
        return status;
    }

    public void setStatus(AnnotationStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
