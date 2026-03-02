package com.anotation.userrole;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

// ============================================================
// CODE CŨ (team có sẵn) - đã được refactor bên dưới
// ============================================================
//
// public class UserRoleCreateRequest {
//     @NotNull private UUID userId;
//     @NotNull private Long roleId;      // ← Long roleId (sai kiểu)
//     private UUID assignedBy;           // ← assignedBy thay vì projectId
// }
//
// ============================================================
// CODE MỚI (refactored)
// ============================================================

public class UserRoleRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Role ID is required")
    private UUID roleId;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }

}
