package com.anotation.userrole;

import com.anotation.role.Role;
import com.anotation.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

// ============================================================
// CODE CŨ (team có sẵn) - đã được refactor bên dưới
// ============================================================
//
// @Entity
// @Table(name = "user_roles", schema = "public")
// public class UserRole {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;                          // ← Long, không phải UUID
//
//     @Column(name = "user_id")
//     private UUID userId;                      // ← raw UUID, không phải @ManyToOne
//
//     @Column(name = "role_id")
//     private Long roleId;                      // ← raw Long, không phải @ManyToOne
//
//     @Column(name = "assigned_by")
//     private UUID assignedBy;                  // ← có assigned_by thay vì project_id
//
//     @Column(name = "assigned_at")
//     private Instant assignedAt;
// }
//
// ============================================================
// CODE MỚI (refactored)
// ============================================================

@Entity
@Table(name = "user_roles", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "uq_user_role", columnNames = { "user_id", "role_id" })
})
public class UserRole {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "assigned_at", nullable = false, updatable = false)
    private LocalDateTime assignedAt;

    @PrePersist
    public void onCreate() {
        assignedAt = LocalDateTime.now();
    }

    // Getters & Setters
    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }
}
