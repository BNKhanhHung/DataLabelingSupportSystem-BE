package com.anotation.role;

import java.util.List;
import java.util.UUID;

public interface RoleService {
    List<RoleResponse> getAll();

    RoleResponse getById(UUID id);

    RoleResponse create(RoleRequest request);

    RoleResponse update(UUID id, RoleRequest request);

    void delete(UUID id);
}
