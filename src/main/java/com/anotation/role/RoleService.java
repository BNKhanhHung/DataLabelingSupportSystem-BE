package com.anotation.role;

import com.anotation.common.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RoleService {
    PageResponse<RoleResponse> getAll(Pageable pageable);

    RoleResponse getById(UUID id);

    RoleResponse create(RoleRequest request);

    RoleResponse update(UUID id, RoleRequest request);

    void delete(UUID id);
}
