package com.anotation.userrole;

import org.springframework.stereotype.Component;

@Component
public class UserRoleMapper {

    public UserRoleResponse toResponse(UserRole userRole) {
        UserRoleResponse response = new UserRoleResponse();
        response.setId(userRole.getId());

        // Flatten User info
        response.setUserId(userRole.getUser().getId());
        response.setUsername(userRole.getUser().getUsername());

        // Flatten Role info
        response.setRoleId(userRole.getRole().getId());
        response.setRoleName(userRole.getRole().getName());

        // Flatten Project info
        response.setProjectId(userRole.getProject().getId());
        response.setProjectName(userRole.getProject().getName());

        response.setAssignedAt(userRole.getAssignedAt());
        return response;
    }
}
