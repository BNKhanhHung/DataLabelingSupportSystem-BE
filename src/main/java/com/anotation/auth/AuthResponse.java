package com.anotation.auth;

import java.util.UUID;

/**
 * DTO JSON trả về sau đăng nhập thành công ({@code POST /api/auth/login}).
 * {@code token}: chuỗi JWT ký bởi {@link JwtUtil}; {@code type}: luôn {@code "Bearer"} cho header {@code Authorization}.
 * {@code userId}, {@code username}, {@code email}: định danh và hiển thị người dùng; {@code systemRole}: vai hệ thống (vd. USER, ADMIN, MANAGER) để frontend phân quyền UI.
 * Client gửi lại token dạng {@code Authorization: Bearer <token>} để {@link JwtAuthenticationFilter} thiết lập ngữ cảnh bảo mật.
 */
public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private UUID userId;
    private String username;
    private String email;
    private String systemRole;

    public AuthResponse(String token, UUID userId, String username, String email, String systemRole) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.systemRole = systemRole;
    }

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getSystemRole() {
        return systemRole;
    }

    public void setSystemRole(String systemRole) {
        this.systemRole = systemRole;
    }
}
