package com.anotation.userrole;

import org.springframework.stereotype.Component;

/**
 * Ánh xạ entity {@link UserRole} sang {@link UserRoleResponse}: “dẹt” thông tin user (id, username) và role (id, tên).
 */
@Component
public class UserRoleMapper {

    /**
     * @param userRole entity có quan hệ {@code user} và {@code role} đã load (hoặc proxy hợp lệ)
     * @return DTO cho API
     */
    public UserRoleResponse toResponse(UserRole userRole) {
        UserRoleResponse response = new UserRoleResponse();
        response.setId(userRole.getId());

        // Flatten User info
        response.setUserId(userRole.getUser().getId());
        response.setUsername(userRole.getUser().getUsername());

        // Flatten Role info
        response.setRoleId(userRole.getRole().getId());
        response.setRoleName(userRole.getRole().getName());

        response.setAssignedAt(userRole.getAssignedAt());
        return response;
    }
}
