package com.anotation.dataset;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA repository cho {@link Dataset}.
 * <p>
 * Cung cấp truy vấn phân trang theo project và các phương thức kiểm tra trùng tên dataset trong cùng project
 * (dùng khi tạo mới và cập nhật, loại trừ chính bản ghi đang sửa).
 * </p>
 */
@Repository
public interface DatasetRepository extends JpaRepository<Dataset, UUID> {

    // Check duplicate name within same project
    boolean existsByNameAndProjectId(String name, UUID projectId);

    // Check duplicate name within same project excluding self (for update)
    boolean existsByNameAndProjectIdAndIdNot(String name, UUID projectId, UUID id);

    // Get all datasets in a project
    Page<Dataset> findByProjectId(UUID projectId, Pageable pageable);
}
