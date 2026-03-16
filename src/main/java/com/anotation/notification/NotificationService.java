package com.anotation.notification;

import com.anotation.common.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface NotificationService {

    PageResponse<NotificationResponse> getMyNotifications(Pageable pageable);

    long countUnread();

    NotificationResponse markAsRead(UUID id);

    void markAllAsRead();

    void create(UUID userId, String type, String title, String message, String relatedEntityType, UUID relatedEntityId);

    /** Check overdue tasks/projects and create DEADLINE_OVERDUE_* notifications (at most once per 24h per entity per user). */
    void checkAndCreateOverdueNotifications();
}
