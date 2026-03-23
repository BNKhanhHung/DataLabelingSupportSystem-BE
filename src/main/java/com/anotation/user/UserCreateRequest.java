package com.anotation.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO request khi Admin (hoặc API được phép) tạo hoặc cập nhật tài khoản {@link User}.
 * <p>
 * Mật khẩu gửi dạng plain text trên kênh bảo mật (HTTPS); service sẽ hash BCrypt trước khi lưu.
 * {@link #systemRole} tùy chọn — nếu bỏ trống, entity gán mặc định {@link SystemRole#USER} khi persist.
 */
public class UserCreateRequest {

    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @NotBlank
    private String status;

    /** Tên enum {@link SystemRole} dạng chuỗi (vd. USER, MANAGER); null/blank → mặc định ở tầng service/entity. */
    private String systemRole;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSystemRole() {
        return systemRole;
    }

    public void setSystemRole(String systemRole) {
        this.systemRole = systemRole;
    }
}
