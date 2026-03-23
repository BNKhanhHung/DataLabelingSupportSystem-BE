package com.anotation.dataset;

import com.anotation.common.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Hợp đồng dịch vụ nghiệp vụ cho quản lý {@link Dataset}.
 * <p>
 * Các thao tác đọc có thể dùng phân trang; tạo/cập nhật kiểm tra tồn tại project và trùng tên trong project;
 * lỗi được báo qua {@link com.anotation.exception.NotFoundException} và {@link com.anotation.exception.DuplicateException}.
 * </p>
 */
public interface DatasetService {
    PageResponse<DatasetResponse> getAll(Pageable pageable);

    DatasetResponse getById(UUID id);

    PageResponse<DatasetResponse> getByProject(UUID projectId, Pageable pageable);

    DatasetResponse create(DatasetRequest request);

    DatasetResponse update(UUID id, DatasetRequest request);

    void delete(UUID id);
}
