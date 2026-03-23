package com.anotation.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Truy vấn JPA cho {@link Notification}: phân trang theo user và thứ tự {@code createdAt} giảm dần,
 * lọc theo trạng thái đã đọc, đếm chưa đọc, và kiểm tra tồn tại thông báo gần đây (cooldown tránh spam cùng loại/entity).
 */
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Page<Notification> findByUserIdAndReadOrderByCreatedAtDesc(UUID userId, boolean read, Pageable pageable);

    long countByUserIdAndRead(UUID userId, boolean read);

    boolean existsByUserIdAndTypeAndRelatedEntityIdAndCreatedAtAfter(
            UUID userId, String type, UUID relatedEntityId, LocalDateTime createdAtAfter);
}
