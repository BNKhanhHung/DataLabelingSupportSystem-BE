package com.anotation.notification;

import com.anotation.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * REST API thông báo cho người dùng đăng nhập, gốc {@code /api/notifications}.
 * <p>
 * Cho phép xem danh sách phân trang, đếm chưa đọc, đánh dấu đã đọc từng bản ghi hoặc toàn bộ,
 * và kích hoạt kiểm tra quá hạn (tạo thông báo loại {@code DEADLINE_OVERDUE_*} với giới hạn tần suất theo nghiệp vụ ở service).
 * </p>
 */
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification", description = "User notifications APIs")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(summary = "Get my notifications (paginated)")
    public ResponseEntity<PageResponse<NotificationResponse>> getMyNotifications(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(notificationService.getMyNotifications(pageable));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get count of unread notifications")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        return ResponseEntity.ok(Map.of("count", notificationService.countUnread()));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable UUID id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Mark all my notifications as read")
    public ResponseEntity<Void> markAllAsRead() {
        notificationService.markAllAsRead();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/check-overdue")
    @Operation(summary = "Check overdue tasks/projects and create DEADLINE_OVERDUE notifications (at most once per 24h per entity)")
    public ResponseEntity<Void> checkOverdue() {
        notificationService.checkAndCreateOverdueNotifications();
        return ResponseEntity.noContent().build();
    }
}
