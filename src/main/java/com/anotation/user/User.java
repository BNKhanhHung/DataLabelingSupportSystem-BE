package com.anotation.user;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import java.time.Instant;
import java.util.UUID;

/**
 * Entity JPA ánh xạ bảng {@code users} (schema {@code public}): tài khoản đăng nhập hệ thống.
 * <p>
 * Lưu thông tin định danh, email, hash mật khẩu, trạng thái tài khoản, {@link SystemRole}, mốc thời gian
 * và số lần cảnh báo ({@code warnings}) dùng cho nghiệp vụ quá hạn task. Callback {@code @PrePersist}/{@code @PreUpdate}
 * gán mặc định role và cập nhật timestamp.
 */
@Entity
@Table(name = "users", schema = "public")
public class User {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String status;

    @Enumerated(EnumType.STRING)
    @Column(name = "system_role")
    private SystemRole systemRole = SystemRole.USER;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "warnings", nullable = false)
    private int warnings = 0;

    /**
     * Trước khi insert: đảm bảo {@link #systemRole} không null (mặc định USER) và gán {@code createdAt}/{@code updatedAt}.
     */
    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        if (systemRole == null) {
            systemRole = SystemRole.USER;
        }
        createdAt = now;
        updatedAt = now;
    }

    /**
     * Trước khi update: cập nhật {@code updatedAt}.
     */
    @PreUpdate
    public void onUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SystemRole getSystemRole() {
        return systemRole;
    }

    public void setSystemRole(SystemRole systemRole) {
        this.systemRole = systemRole;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public int getWarnings() {
        return warnings;
    }

    public void setWarnings(int warnings) {
        this.warnings = warnings;
    }
}
