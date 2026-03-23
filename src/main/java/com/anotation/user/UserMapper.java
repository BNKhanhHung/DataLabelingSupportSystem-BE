package com.anotation.user;

import org.springframework.stereotype.Component;

/**
 * Chuyển đổi giữa entity {@link User}, DTO {@link UserResponse} và {@link UserCreateRequest}.
 * <p>
 * Không bao giỽ map {@code passwordHash} ra response. Mật khẩu plain từ request được hash ở {@link UserServiceImpl}.
 */
@Component
public class UserMapper {

    /**
     * Map entity sang DTO an toàn cho client (không lộ hash mật khẩu).
     *
     * @param user entity nguồn
     * @return {@link UserResponse} đã điền
     */
    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setStatus(user.getStatus());
        SystemRole systemRole = user.getSystemRole() != null
                ? user.getSystemRole()
                : SystemRole.USER;
        response.setSystemRole(systemRole.name());
        response.setWarnings(user.getWarnings());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
        // Note: passwordHash is intentionally NOT mapped to response
    }

    /**
     * Tạo entity mới từ request tạo user — chỉ các trường không nhạy cảm; hash mật khẩu do tầng service gán.
     *
     * @param request payload tạo user
     * @return entity {@link User} chưa có {@code passwordHash} hợp lệ cho đến khi service encode
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
     * Cập nhật entity từ request: username, email, status. Không cập nhật mật khẩu tại đây (service xử lý riêng).
     *
     * @param request dữ liệu mới
     * @param user    entity đích
     */
    public void updateEntity(UserCreateRequest request, User user) {
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setStatus(request.getStatus());
        // password update is NOT done here — Admin can set via separate flow if needed
    }
}
