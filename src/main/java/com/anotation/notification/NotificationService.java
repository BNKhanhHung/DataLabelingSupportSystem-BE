package com.anotation.notification;

import com.anotation.common.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Dịch vụ thông báo: truy vấn theo user hiện tại (Security), đánh dấu đọc, tạo bản ghi programmatically,
 * và quét task/project quá hạn để tạo thông báo nhắc Manager/Admin (có cooldown theo user/loại/entity).
 */
public interface NotificationService {

    PageResponse<NotificationResponse> getMyNotifications(Pageable pageable);

    long countUnread();

    NotificationResponse markAsRead(UUID id);

    void markAllAsRead();

    void create(UUID userId, String type, String title, String message, String relatedEntityType, UUID relatedEntityId);

    /**
     * Kiểm tra task/project quá hạn và tạo thông báo {@code DEADLINE_OVERDUE_TASK} / {@code DEADLINE_OVERDUE_PROJECT}
     * tối đa một lần trong cửa sổ thời gian (ví dụ 24 giờ) cho mỗi cặp user–entity, tránh trùng lặp.
     */
    void checkAndCreateOverdueNotifications();
}
