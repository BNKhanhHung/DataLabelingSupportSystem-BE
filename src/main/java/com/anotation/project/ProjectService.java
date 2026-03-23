package com.anotation.project;

import com.anotation.common.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Hợp đồng nghiệp vụ quản lý dự án: phân trang, tìm kiếm, CRUD.
 * <p>
 * Triển khai chuẩn {@link ProjectServiceImpl} xử lý trùng tên dự án, mặc định deadline
 * khi tạo, và tính {@link ProjectStatus} từ task liên quan.
 */
public interface ProjectService {

    /**
     * Lấy danh sách dự án có phân trang và sắp xếp theo {@link Pageable}.
     * <p>
     * Nếu tham số {@code sort} trỏ tới thuộc tính không tồn tại trên entity, tầng service
     * sẽ fallback sort theo {@code id} để tránh lỗi 500.
     *
     * @param pageable thông tin trang, kích thước trang và sort (ví dụ {@code sort=id,asc})
     * @return {@link PageResponse} chứa các {@link ProjectResponse}
     */
    PageResponse<ProjectResponse> getAll(Pageable pageable);

    /**
     * Tìm dự án theo tên (chuỗi con, không phân biệt hoa thường).
     * <p>
     * Nếu {@code name} rỗng hoặc null, hành vi tương đương {@link #getAll(Pageable)}.
     *
     * @param name     chuỗi tìm kiếm (có thể null hoặc blank)
     * @param pageable phân trang và sort
     * @return trang kết quả {@link ProjectResponse}
     */
    PageResponse<ProjectResponse> searchByName(String name, Pageable pageable);

    /**
     * Lấy một dự án theo định danh.
     *
     * @param id UUID dự án
     * @return {@link ProjectResponse} đã map kèm trạng thái tổng hợp
     * @throws com.anotation.exception.NotFoundException nếu không tồn tại dự án với {@code id}
     */
    ProjectResponse getById(UUID id);

    /**
     * Tạo dự án mới.
     * <p>
     * Nếu request không có deadline, tầng triển khai có thể gán mặc định (ví dụ +7 ngày).
     *
     * @param request dữ liệu tạo dự án
     * @return bản ghi dự án sau khi lưu
     * @throws com.anotation.exception.DuplicateException nếu tên dự án đã tồn tại
     */
    ProjectResponse create(ProjectRequest request);

    /**
     * Cập nhật dự án theo id.
     *
     * @param id      UUID dự án cần sửa
     * @param request dữ liệu cập nhật
     * @return dự án sau khi cập nhật
     * @throws com.anotation.exception.NotFoundException nếu không tìm thấy dự án
     * @throws com.anotation.exception.DuplicateException nếu tên mới trùng dự án khác
     */
    ProjectResponse update(UUID id, ProjectRequest request);

    /**
     * Xóa dự án theo id (cần tồn tại trước khi xóa).
     *
     * @param id UUID dự án
     * @throws com.anotation.exception.NotFoundException nếu không tìm thấy
     */
    void delete(UUID id);
}
