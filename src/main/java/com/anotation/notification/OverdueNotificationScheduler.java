package com.anotation.notification;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Thành phần lên lịch Spring: gọi định kỳ {@link NotificationService#checkAndCreateOverdueNotifications()}
 * để đồng bộ thông báo quá hạn mà không cần client gọi thủ công {@code POST /check-overdue}.
 * <p>
 * Lịch chạy được khai báo trên {@link org.springframework.scheduling.annotation.Scheduled} của phương thức
 * {@link #checkOverdueAndNotify()} (hiện kích hoạt mỗi 30 giây). Có thể nới lỏng cron để giảm tải hệ thống.
 * </p>
 */
@Component
public class OverdueNotificationScheduler {

    private final NotificationService notificationService;

    public OverdueNotificationScheduler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Mỗi 15 phút
    @Scheduled(cron = "*/30 * * * * *")
    public void checkOverdueAndNotify() {
        notificationService.checkAndCreateOverdueNotifications();
    }
}
    