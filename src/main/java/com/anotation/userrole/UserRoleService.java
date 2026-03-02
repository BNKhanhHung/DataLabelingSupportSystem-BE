package com.anotation.userrole;

import com.anotation.common.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserRoleService {
    PageResponse<UserRoleResponse> getAll(Pageable pageable);

    UserRoleResponse getById(UUID id);

    PageResponse<UserRoleResponse> getByUser(UUID userId, Pageable pageable);

    UserRoleResponse assign(UserRoleRequest request);

    void delete(UUID id);
}
