package com.anotation.userrole;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO request dạng “legacy” hoặc tích hợp cũ: gán role cho user bằng {@code userId}, {@code roleId} và tùy chọn {@code assignedBy}.
 * <p>
 * Lưu ý: API chính hiện dùng {@link UserRoleRequest} với {@code roleId} kiểu {@link UUID}. Giữ class này nếu vẫn cần cho client/schema cũ.
 */
public class UserRoleCreateRequest {
    @NotNull
    private UUID userId;

    @NotNull
    private Long roleId;

    private UUID assignedBy;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public UUID getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(UUID assignedBy) {
        this.assignedBy = assignedBy;
    }
}
