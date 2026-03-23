package com.anotation.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Payload cho API đổi mật khẩu của chính user đang đăng nhập (ví dụ {@code PATCH /api/users/me/password}).
 * <p>
 * Yêu cầu mật khẩu cũ để xác minh và mật khẩu mới đủ độ dài; lớp service sẽ so khớp hash và mã hóa BCrypt mật khẩu mới.
 */
public class PasswordChangeRequest {

    /** Mật khẩu hiện tại (dạng plain text trong request, không lưu thẳng vào DB). */
    @NotBlank(message = "Old password is required")
    private String oldPassword;

    /** Mật khẩu mới; tối thiểu 6 ký tự theo ràng buộc validation. */
    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "New password must be at least 6 characters")
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
