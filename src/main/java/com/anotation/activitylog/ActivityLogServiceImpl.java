package com.anotation.activitylog;

import com.anotation.common.PageResponse;
import com.anotation.user.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Triển khai {@link ActivityLogService}: ghi nhật ký bất biến và truy vấn chỉ đọc.
 * <p>
 * Method {@link #log} được gọi bởi các tầng Service khác (TaskServiceImpl, AnnotationServiceImpl,
 * ReviewFeedbackServiceImpl) để tự động ghi lại mọi hành động quan trọng.
 * <p>
 * Tuân thủ pattern sortable-safe giống {@code TaskServiceImpl}: nếu client gửi sort field
 * không hợp lệ thì fallback về sort theo {@code createdAt DESC}.
 */
@Service
@Transactional
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogServiceImpl(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    // ── Ghi log ──────────────────────────────────────────────────────────────────

    @Override
    public void log(User user, ActivityAction action, String entityType, UUID entityId, String details) {
        ActivityLog log = new ActivityLog();
        log.setUser(user);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDetails(details);
        activityLogRepository.save(log);
    }

    // ── Truy vấn chỉ đọc ────────────────────────────────────────────────────────

    private static Pageable safePageable(Pageable pageable) {
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    private ActivityLogResponse toResponse(ActivityLog log) {
        return new ActivityLogResponse(
                log.getId(),
                log.getUser().getId(),
                log.getUser().getUsername(),
                log.getUser().getSystemRole() != null ? log.getUser().getSystemRole().name() : "USER",
                log.getAction(),
                log.getEntityType(),
                log.getEntityId(),
                log.getDetails(),
                log.getCreatedAt());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ActivityLogResponse> getAll(Pageable pageable) {
        try {
            return PageResponse.from(activityLogRepository.findAll(pageable), this::toResponse);
        } catch (PropertyReferenceException e) {
            return PageResponse.from(activityLogRepository.findAll(safePageable(pageable)), this::toResponse);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ActivityLogResponse> getByUser(UUID userId, Pageable pageable) {
        try {
            return PageResponse.from(activityLogRepository.findByUserId(userId, pageable), this::toResponse);
        } catch (PropertyReferenceException e) {
            return PageResponse.from(activityLogRepository.findByUserId(userId, safePageable(pageable)),
                    this::toResponse);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ActivityLogResponse> getByEntity(String entityType, UUID entityId, Pageable pageable) {
        try {
            return PageResponse.from(
                    activityLogRepository.findByEntityTypeAndEntityId(entityType, entityId, pageable),
                    this::toResponse);
        } catch (PropertyReferenceException e) {
            return PageResponse.from(
                    activityLogRepository.findByEntityTypeAndEntityId(entityType, entityId, safePageable(pageable)),
                    this::toResponse);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ActivityLogResponse> search(UUID userId, ActivityAction action,
                                                     LocalDateTime from, LocalDateTime to,
                                                     Pageable pageable) {
        try {
            return PageResponse.from(
                    activityLogRepository.search(userId, action, from, to, pageable),
                    this::toResponse);
        } catch (PropertyReferenceException e) {
            return PageResponse.from(
                    activityLogRepository.search(userId, action, from, to, safePageable(pageable)),
                    this::toResponse);
        }
    }
}
