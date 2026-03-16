package com.anotation.notification;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OverdueNotificationScheduler {

    private final NotificationService notificationService;

    public OverdueNotificationScheduler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void checkOverdueAndNotify() {
        notificationService.checkAndCreateOverdueNotifications();
    }
}
