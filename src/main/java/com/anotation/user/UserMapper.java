package com.anotation.user;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setStatus(user.getStatus());
        response.setSystemRole(user.getSystemRole().name());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
        // Note: passwordHash is intentionally NOT mapped to response
    }

    /**
     * Maps request fields to entity — password hashing is handled in Service layer.
     */
    public User toEntity(UserCreateRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        // passwordHash is set in UserServiceImpl (hashed with BCrypt)
        user.setStatus(request.getStatus());
        return user;
    }

    /**
     * Update existing entity — does NOT update password (separate endpoint if
     * needed).
     */
    public void updateEntity(UserCreateRequest request, User user) {
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setStatus(request.getStatus());
        // password update is NOT done here — Admin can set via separate flow if needed
    }
}
