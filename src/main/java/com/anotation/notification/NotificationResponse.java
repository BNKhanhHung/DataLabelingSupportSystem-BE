package com.anotation.notification;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO trả về thông báo cho client: loại, tiêu đề, nội dung, tham chiếu thực thể liên quan (nếu có), trạng thái đọc và thời gian tạo.
 */
public class NotificationResponse {

    private UUID id;
    private String type;
    private String title;
    private String message;
    private String relatedEntityType;
    private UUID relatedEntityId;
    private boolean read;
    private LocalDateTime createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getRelatedEntityType() { return relatedEntityType; }
    public void setRelatedEntityType(String relatedEntityType) { this.relatedEntityType = relatedEntityType; }
    public UUID getRelatedEntityId() { return relatedEntityId; }
    public void setRelatedEntityId(UUID relatedEntityId) { this.relatedEntityId = relatedEntityId; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
