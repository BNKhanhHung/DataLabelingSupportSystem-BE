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
