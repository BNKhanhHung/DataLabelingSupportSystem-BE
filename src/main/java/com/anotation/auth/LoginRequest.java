package com.anotation.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO JSON cho thân yêu cầu đăng nhập ({@code POST /api/auth/login}).
 * {@code usernameOrEmail}: tên đăng nhập hoặc email đã đăng ký; bắt buộc, không rỗng ({@code @NotBlank}).
 * {@code password}: mật khẩu thuần; bắt buộc; so khớp với hash trong DB qua {@link AuthService#login}.
 * Getter {@code getUsernameOrEmail} được service dùng để tra cứu lần lượt theo username rồi email.
 */
public class LoginRequest {

    @NotBlank(message = "Username or email is required")
    private String usernameOrEmail;

    @NotBlank(message = "Password is required")
    private String password;

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
