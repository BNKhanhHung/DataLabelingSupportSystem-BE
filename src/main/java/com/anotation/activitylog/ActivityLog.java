package com.anotation.activitylog;

import com.anotation.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Thực thể nhật ký hoạt động — bản ghi bất biến (immutable audit record).
 * <p>
 * Mỗi hành động quan trọng trong hệ thống (tạo task, nộp bài, duyệt, từ chối…)
 * đều được ghi lại ở đây. Không có setter cho {@code id} và {@code createdAt},
 * không có endpoint DELETE/PUT/PATCH → đảm bảo không ai có thể sửa đổi lịch sử.
 * <p>
 * Dùng làm bằng chứng pháp lý nếu xảy ra tranh chấp hoặc gian lận.
 */
@Entity
@Table(name = "activity_logs", schema = "public", indexes = {
        @Index(name = "idx_activity_user", columnList = "user_id"),
        @Index(name = "idx_activity_entity", columnList = "entity_type, entity_id"),
        @Index(name = "idx_activity_created", columnList = "created_at")
})
public class ActivityLog {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    /** Người thực hiện hành động. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Loại hành động (TASK_CREATED, ANNOTATION_SUBMITTED, …). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ActivityAction action;

    /** Loại đối tượng bị tác động (TASK, ANNOTATION, REVIEW, PROJECT). */
    @Column(name = "entity_type", nullable = false, length = 30)
    private String entityType;

    /** ID của đối tượng bị tác động. */
    @Column(name = "entity_id")
    private UUID entityId;

    /** Mô tả chi tiết hành động (ví dụ: "Assigned annotator X, reviewer Y to task Z"). */
    @Column(columnDefinition = "TEXT")
    private String details;

    /** Thời điểm ghi nhật ký — tự động set, không cho phép cập nhật. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (createdAt == null)
            createdAt = LocalDateTime.now();
    }

    // ── Getters (Không có setter cho id và createdAt → bất biến) ─────────────

    public UUID getId() { return id; }
    public User getUser() { return user; }
    public ActivityAction getAction() { return action; }
    public String getEntityType() { return entityType; }
    public UUID getEntityId() { return entityId; }
    public String getDetails() { return details; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ── Setters (chỉ cho phép set khi tạo mới, không cho phép sửa sau khi lưu) ─

    public void setUser(User user) { this.user = user; }
    public void setAction(ActivityAction action) { this.action = action; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public void setEntityId(UUID entityId) { this.entityId = entityId; }
    public void setDetails(String details) { this.details = details; }
}
