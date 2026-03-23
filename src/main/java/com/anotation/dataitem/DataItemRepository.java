package com.anotation.dataitem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA cho {@link DataItem}; hỗ trợ phân trang theo dataset, theo status, và kiểm tra trùng {@code contentUrl} trong một dataset.
 * Các truy vấn JPQL theo project: lọc status ANNOTATED/REVIEWED, hoặc tồn tại ít nhất một {@link com.anotation.annotation.Annotation} qua task item — phục vụ danh sách đã gán nhãn và export.
 * {@code findByDatasetProjectIdAndStatusIn} dùng tập status linh hoạt; các phương thức riêng ANNOTATED/REVIEWED tránh lỗi tham số IN với collection.
 * Được {@link DataItemServiceImpl} dùng cho CRUD, bulk, thống kê/export cùng {@link com.anotation.annotation.AnnotationRepository}.
 */
@Repository
public interface DataItemRepository extends JpaRepository<DataItem, UUID> {

    // Check duplicate contentUrl within same dataset
    boolean existsByContentUrlAndDatasetId(String contentUrl, UUID datasetId);

    // Get all items in a dataset
    Page<DataItem> findByDatasetId(UUID datasetId, Pageable pageable);

    // Get items by status
    Page<DataItem> findByStatus(DataItemStatus status, Pageable pageable);

    // Get items by dataset and status
    Page<DataItem> findByDatasetIdAndStatus(UUID datasetId, DataItemStatus status, Pageable pageable);

    /** Data items thuộc project đã gắn nhãn (ANNOTATED hoặc REVIEWED). */
    @Query("SELECT d FROM DataItem d WHERE d.dataset.project.id = :projectId AND d.status IN :statuses ORDER BY d.createdAt")
    List<DataItem> findByDatasetProjectIdAndStatusIn(@Param("projectId") UUID projectId, @Param("statuses") Collection<DataItemStatus> statuses);

    /** Data items thuộc project có status ANNOTATED (dùng riêng tránh lỗi IN với collection). */
    @Query("SELECT d FROM DataItem d WHERE d.dataset.project.id = :projectId AND d.status = com.anotation.dataitem.DataItemStatus.ANNOTATED ORDER BY d.createdAt")
    List<DataItem> findByDatasetProjectIdAndStatusAnnotated(@Param("projectId") UUID projectId);

    /** Data items thuộc project có status REVIEWED. */
    @Query("SELECT d FROM DataItem d WHERE d.dataset.project.id = :projectId AND d.status = com.anotation.dataitem.DataItemStatus.REVIEWED ORDER BY d.createdAt")
    List<DataItem> findByDatasetProjectIdAndStatusReviewed(@Param("projectId") UUID projectId);

    /** Data items thuộc project và có ít nhất một annotation (đã gắn nhãn), bất kể status. */
    @Query("""
        SELECT d FROM DataItem d
        WHERE d.dataset.project.id = :projectId
        AND EXISTS (SELECT 1 FROM Annotation a WHERE a.taskItem.dataItem = d AND a.taskItem.task.project.id = :projectId)
        ORDER BY d.createdAt
        """)
    List<DataItem> findByProjectIdAndHasAnnotation(@Param("projectId") UUID projectId);
}
