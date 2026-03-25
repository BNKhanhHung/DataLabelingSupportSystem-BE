package com.anotation.activitylog;

import com.anotation.common.PageResponse;
import com.anotation.user.User;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service nhật ký hoạt động — cung cấp ghi log nội bộ và truy vấn chỉ đọc.
 * <p>
 * Không có method xóa/sửa vì dữ liệu audit là bất biến (immutable).
 */
public interface ActivityLogService {

    // ── Ghi log (dùng nội bộ bởi các Service khác) ───────────────────────────

    /**
     * Ghi một bản ghi nhật ký hoạt động.
     *
     * @param user       người thực hiện hành động
     * @param action     loại hành động
     * @param entityType loại đối tượng bị tác động (TASK, ANNOTATION, REVIEW, PROJECT)
     * @param entityId   ID của đối tượng bị tác động
     * @param details    mô tả chi tiết hành động
     */
    void log(User user, ActivityAction action, String entityType, UUID entityId, String details);

    // ── Truy vấn chỉ đọc (dùng bởi Controller) ─────────────────────────────

    /** Toàn bộ nhật ký, phân trang. */
    PageResponse<ActivityLogResponse> getAll(Pageable pageable);

    /** Nhật ký của một user cụ thể. */
    PageResponse<ActivityLogResponse> getByUser(UUID userId, Pageable pageable);

    /** Nhật ký của đối tượng cụ thể (ví dụ: tất cả log của Task X). */
    PageResponse<ActivityLogResponse> getByEntity(String entityType, UUID entityId, Pageable pageable);

    /** Tìm kiếm nâng cao: kết hợp userId, action, khoảng thời gian. */
    PageResponse<ActivityLogResponse> search(UUID userId, ActivityAction action,
                                              LocalDateTime from, LocalDateTime to,
                                              Pageable pageable);
}
