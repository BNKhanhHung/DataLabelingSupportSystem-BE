package com.anotation.user;

import com.anotation.common.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    PageResponse<UserResponse> getAll(Pageable pageable);

    UserResponse getById(UUID id);

    UserResponse getCurrentUser(String username);

    void changePassword(String username, PasswordChangeRequest request);

    UserResponse create(UserCreateRequest request);

    UserResponse update(UUID id, UserCreateRequest request);

    void delete(UUID id);
}
