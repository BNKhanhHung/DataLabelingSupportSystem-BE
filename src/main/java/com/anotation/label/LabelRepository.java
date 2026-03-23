package com.anotation.label;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA cho {@link Label}: phân trang theo {@code projectId} và kiểm tra trùng tên trong project
 * (có biến thể loại trừ id khi cập nhật).
 */
@Repository
public interface LabelRepository extends JpaRepository<Label, UUID> {

    Page<Label> findByProjectId(UUID projectId, Pageable pageable);

    boolean existsByNameAndProjectId(String name, UUID projectId);

    boolean existsByNameAndProjectIdAndIdNot(String name, UUID projectId, UUID id);
}
