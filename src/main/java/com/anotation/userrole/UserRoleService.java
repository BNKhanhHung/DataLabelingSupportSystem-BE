package com.anotation.userrole;

import com.anotation.common.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Dịch vụ gán và quản lý {@link UserRole}: đọc phân trang, tra cứu, gán mới (không trùng cặp user+role) và xóa.
 */
public interface UserRoleService {

    /**
     * Danh sách tất cả assignment phân trang.
     */
    PageResponse<UserRoleResponse> getAll(Pageable pageable);

    /**
     * Chi tiết theo id assignment.
     */
    UserRoleResponse getById(UUID id);

    /**
     * Mọi role đã gán cho {@code userId}, phân trang.
     */
    PageResponse<UserRoleResponse> getByUser(UUID userId, Pageable pageable);

    /**
     * Tạo bản ghi gán role; ném lỗi nếu user/role không tồn tại hoặc đã gán trùng.
     */
    UserRoleResponse assign(UserRoleRequest request);

    /**
     * Xóa assignment theo id.
     */
    void delete(UUID id);
}
