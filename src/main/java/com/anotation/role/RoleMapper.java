package com.anotation.role;

import org.springframework.stereotype.Component;

/**
 * Chuyển đổi giữa {@link Role}, {@link RoleRequest} và {@link RoleResponse}.
 */
@Component
public class RoleMapper {

    /**
     * Entity → DTO phản hồi API.
     *
     * @param role entity
     * @return {@link RoleResponse}
     */
    public RoleResponse toResponse(Role role) {
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setName(role.getName());
        response.setDescription(role.getDescription());
        return response;
    }

    public Role toEntity(RoleRequest request) {
        Role role = new Role();
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        return role;
    }

    /**
     * Áp dữ liệu từ request lên entity hiện có (cập nhật).
     *
     * @param request nguồn
     * @param role    entity cần mutate
     */
    public void updateEntity(RoleRequest request, Role role) {
        role.setName(request.getName());
        role.setDescription(request.getDescription());
    }
}
