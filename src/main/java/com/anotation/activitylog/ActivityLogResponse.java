package com.anotation.activitylog;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO trả về cho API nhật ký hoạt động.
 * <p>
 * Chỉ chứa thông tin cần thiết, không lộ password hay token.
 */
public class ActivityLogResponse {

    private UUID id;
    private UUID userId;
    private String username;
    private String userRole;
    private ActivityAction action;
    private String entityType;
    private UUID entityId;
    private String details;
    private LocalDateTime createdAt;

    public ActivityLogResponse() {}

    public ActivityLogResponse(UUID id, UUID userId, String username, String userRole,
                               ActivityAction action, String entityType, UUID entityId,
                               String details, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.userRole = userRole;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
        this.createdAt = createdAt;
    }

    // ── Getters ──────────────────────────────────────────────────────────────────

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getUserRole() { return userRole; }
    public ActivityAction getAction() { return action; }
    public String getEntityType() { return entityType; }
    public UUID getEntityId() { return entityId; }
    public String getDetails() { return details; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ── Setters ──────────────────────────────────────────────────────────────────

    public void setId(UUID id) { this.id = id; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
    public void setAction(ActivityAction action) { this.action = action; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public void setEntityId(UUID entityId) { this.entityId = entityId; }
    public void setDetails(String details) { this.details = details; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
