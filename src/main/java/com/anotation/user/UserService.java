package com.anotation.user;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserResponse> getAll();

    UserResponse getById(UUID id);

    UserResponse create(UserCreateRequest request);

    UserResponse update(UUID id, UserCreateRequest request);

    void delete(UUID id);
}
