package com.anotation.activitylog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Repository truy vấn nhật ký hoạt động — chỉ đọc (read-only queries).
 * <p>
 * Không cung cấp delete/update custom vì dữ liệu audit là bất biến.
 */
public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {

    /** Lịch sử của một user cụ thể. */
    Page<ActivityLog> findByUserId(UUID userId, Pageable pageable);

    /** Lịch sử theo loại đối tượng và ID (ví dụ: tất cả log của Task X). */
    Page<ActivityLog> findByEntityTypeAndEntityId(String entityType, UUID entityId, Pageable pageable);

    /** Lọc theo hành động (ví dụ: xem tất cả TASK_REFUSED). */
    Page<ActivityLog> findByAction(ActivityAction action, Pageable pageable);

    /** Tìm kiếm nâng cao: kết hợp userId (tuỳ chọn), action (tuỳ chọn), khoảng thời gian (tuỳ chọn). */
    @Query("SELECT a FROM ActivityLog a WHERE "
            + "(:userId IS NULL OR a.user.id = :userId) AND "
            + "(:action IS NULL OR a.action = :action) AND "
            + "(:from IS NULL OR a.createdAt >= :from) AND "
            + "(:to IS NULL OR a.createdAt <= :to) "
            + "ORDER BY a.createdAt DESC")
    Page<ActivityLog> search(
            @Param("userId") UUID userId,
            @Param("action") ActivityAction action,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable);
}
