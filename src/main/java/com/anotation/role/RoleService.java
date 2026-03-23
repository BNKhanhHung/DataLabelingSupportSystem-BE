package com.anotation.role;

import com.anotation.common.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Nghiệp vụ quản lý {@link Role}: phân trang, CRUD, chống trùng tên.
 */
public interface RoleService {

    /**
     * @param pageable phân trang/sort
     * @return trang {@link RoleResponse}
     */
    PageResponse<RoleResponse> getAll(Pageable pageable);

    /**
     * @param id UUID role
     * @return DTO
     * @throws com.anotation.exception.NotFoundException nếu không tồn tại
     */
    RoleResponse getById(UUID id);

    /**
     * @param request dữ liệu tạo
     * @return role sau khi lưu
     * @throws com.anotation.exception.DuplicateException nếu trùng tên
     */
    RoleResponse create(RoleRequest request);

    /**
     * @param id      UUID
     * @param request dữ liệu cập nhật
     * @return DTO sau lưu
     * @throws com.anotation.exception.NotFoundException nếu không tồn tại
     * @throws com.anotation.exception.DuplicateException nếu tên trùng role khác
     */
    RoleResponse update(UUID id, RoleRequest request);

    /**
     * @param id UUID
     * @throws com.anotation.exception.NotFoundException nếu không tồn tại
     */
    void delete(UUID id);
}
