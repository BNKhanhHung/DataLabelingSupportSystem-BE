package com.anotation.activitylog;

import com.anotation.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * REST API nhật ký hoạt động — CHỈ ĐỌC (READ-ONLY).
 * <p>
 * Không có POST/PUT/PATCH/DELETE công khai → không ai có thể sửa đổi lịch sử.
 * Manager/Admin xem được toàn bộ. Annotator/Reviewer xem được lịch sử bản thân
 * qua endpoint {@code /my-history}.
 * <p>
 * Đường dẫn: {@code /api/activity-logs}.
 */
@RestController
@RequestMapping("/api/activity-logs")
@Tag(name = "Activity Log", description = "Nhật ký hoạt động (chỉ đọc - bằng chứng bất khả xâm phạm)")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    /**
     * Xem toàn bộ nhật ký (Manager/Admin only).
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Get all activity logs (Manager/Admin only)",
            description = "Xem toàn bộ nhật ký hoạt động của hệ thống. Sort: createdAt (default DESC), action, entityType.")
    public ResponseEntity<PageResponse<ActivityLogResponse>> getAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(activityLogService.getAll(pageable));
    }

    /**
     * Xem nhật ký của một user cụ thể (Manager/Admin only).
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Get activity logs by user ID (Manager/Admin only)",
            description = "Xem toàn bộ lịch sử làm việc của một nhân viên cụ thể.")
    public ResponseEntity<PageResponse<ActivityLogResponse>> getByUser(
            @PathVariable UUID userId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(activityLogService.getByUser(userId, pageable));
    }

    /**
     * Xem nhật ký theo đối tượng (ví dụ: tất cả log liên quan đến Task X).
     */
    @GetMapping("/entity")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Get activity logs by entity (Manager/Admin only)",
            description = "Xem lịch sử của một đối tượng cụ thể. entityType: TASK, ANNOTATION, REVIEW, PROJECT.")
    public ResponseEntity<PageResponse<ActivityLogResponse>> getByEntity(
            @RequestParam String entityType,
            @RequestParam UUID entityId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(activityLogService.getByEntity(entityType, entityId, pageable));
    }

    /**
     * Tìm kiếm nâng cao nhật ký (Manager/Admin only).
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @Operation(summary = "Search activity logs (Manager/Admin only)",
            description = "Tìm kiếm nhật ký theo userId (tuỳ chọn), action (tuỳ chọn), khoảng thời gian from/to (tuỳ chọn).")
    public ResponseEntity<PageResponse<ActivityLogResponse>> search(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) ActivityAction action,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(activityLogService.search(userId, action, from, to, pageable));
    }

    /**
     * Xem lịch sử bản thân (Annotator/Reviewer tự xem).
     * userId truyền vào phải chính là user đang đăng nhập.
     */
    @GetMapping("/my-history/{userId}")
    @Operation(summary = "Get my own activity history",
            description = "Annotator/Reviewer xem lịch sử làm việc của chính mình. Truyền đúng userId của bản thân.")
    public ResponseEntity<PageResponse<ActivityLogResponse>> getMyHistory(
            @PathVariable UUID userId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(activityLogService.getByUser(userId, pageable));
    }
}
