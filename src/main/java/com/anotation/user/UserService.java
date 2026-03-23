package com.anotation.user;

import com.anotation.common.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Dịch vụ quản lý {@link User}: liệt kê phân trang, tra cứu, hồ sơ “me”, tạo/cập nhật/xóa và đổi mật khẩu.
 */
public interface UserService {

    /**
     * Lấy tất cả user phân trang.
     *
     * @param pageable tham số trang và sort
     * @return {@link PageResponse} các {@link UserResponse}
     */
    PageResponse<UserResponse> getAll(Pageable pageable);

    /**
     * Lấy user theo id.
     *
     * @param id UUID
     * @return {@link UserResponse}
     */
    UserResponse getById(UUID id);

    /**
     * Lấy hồ sơ user theo username (thường lấy từ principal sau khi xác thực JWT/session).
     *
     * @param username tên đăng nhập
     * @return {@link UserResponse}
     */
    UserResponse getCurrentUser(String username);

    /**
     * Đổi mật khẩu: kiểm tra mật khẩu cũ và lưu hash mới.
     *
     * @param username   user thực hiện
     * @param request    {@link PasswordChangeRequest}
     */
    void changePassword(String username, PasswordChangeRequest request);

    /**
     * Tạo user mới (Admin): kiểm tra trùng email/username, hash mật khẩu, gán {@link SystemRole} nếu có.
     *
     * @param request {@link UserCreateRequest}
     * @return user đã tạo dạng DTO
     */
    UserResponse create(UserCreateRequest request);

    /**
     * Cập nhật thông tin user; mật khẩu và system role chỉ đổi khi request cung cấp giá trị hợp lệ.
     *
     * @param id      UUID user
     * @param request dữ liệu cập nhật
     * @return DTO sau lưu
     */
    UserResponse update(UUID id, UserCreateRequest request);

    /**
     * Xóa user theo id.
     *
     * @param id UUID
     */
    void delete(UUID id);
}
