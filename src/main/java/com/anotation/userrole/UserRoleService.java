package com.anotation.userrole;

import java.util.List;
import java.util.UUID;

public interface UserRoleService {
    List<UserRoleResponse> getAll();

    UserRoleResponse getById(UUID id);

    List<UserRoleResponse> getByProject(UUID projectId);

    List<UserRoleResponse> getByUser(UUID userId);

    UserRoleResponse assign(UserRoleRequest request);

    void delete(UUID id);
}
