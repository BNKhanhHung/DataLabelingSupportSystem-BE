package com.anotation.notification;

import com.anotation.common.PageResponse;
import com.anotation.exception.NotFoundException;
import com.anotation.project.Project;
import com.anotation.project.ProjectRepository;
import com.anotation.task.Task;
import com.anotation.task.TaskRepository;
import com.anotation.user.SystemRole;
import com.anotation.user.User;
import com.anotation.user.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private static final String TYPE_OVERDUE_TASK = "DEADLINE_OVERDUE_TASK";
    private static final String TYPE_OVERDUE_PROJECT = "DEADLINE_OVERDUE_PROJECT";
    private static final String ENTITY_TASK = "TASK";
    private static final String ENTITY_PROJECT = "PROJECT";
    private static final int OVERDUE_NOTIFICATION_COOLDOWN_HOURS = 24;

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository,
                                   TaskRepository taskRepository,
                                   ProjectRepository projectRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getMyNotifications(Pageable pageable) {
        UUID userId = getCurrentUserId();
        Pageable sortByCreatedDesc = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return PageResponse.from(
                notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, sortByCreatedDesc),
                this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnread() {
        return notificationRepository.countByUserIdAndRead(getCurrentUserId(), false);
    }

    @Override
    public NotificationResponse markAsRead(UUID id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notification not found: " + id));
        if (!n.getUser().getId().equals(getCurrentUserId())) {
            throw new NotFoundException("Notification not found: " + id);
        }
        n.setRead(true);
        return toResponse(notificationRepository.save(n));
    }

    @Override
    public void markAllAsRead() {
        UUID userId = getCurrentUserId();
        notificationRepository.findByUserIdAndReadOrderByCreatedAtDesc(userId, false, PageRequest.of(0, 1000))
                .getContent()
                .forEach(n -> n.setRead(true));
    }

    @Override
    public void create(UUID userId, String type, String title, String message, String relatedEntityType, UUID relatedEntityId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;
        Notification n = new Notification();
        n.setUser(user);
        n.setType(type);
        n.setTitle(title);
        n.setMessage(message);
        n.setRelatedEntityType(relatedEntityType);
        n.setRelatedEntityId(relatedEntityId);
        notificationRepository.save(n);
    }

    @Override
    public void checkAndCreateOverdueNotifications() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime since = now.minusHours(OVERDUE_NOTIFICATION_COOLDOWN_HOURS);

        // Overdue tasks: notify annotator, reviewer, và manager/admin
        List<User> managers = userRepository.findBySystemRoleIn(Set.of(SystemRole.MANAGER, SystemRole.ADMIN));
        List<Task> overdueTasks = taskRepository.findOverdueTasks(now, PageRequest.of(0, 500, Sort.unsorted())).getContent();
        for (Task task : overdueTasks) {
            String projectName = task.getProject() != null ? task.getProject().getName() : "N/A";
            String msg = "Task thuộc project \"" + projectName + "\" đã quá hạn (annotator chưa hoàn thành).";
            UUID taskId = task.getId();

            // Annotator
            UUID annotatorId = task.getAnnotator().getId();
            if (!notificationRepository.existsByUserIdAndTypeAndRelatedEntityIdAndCreatedAtAfter(annotatorId, TYPE_OVERDUE_TASK, taskId, since)) {
                create(annotatorId, TYPE_OVERDUE_TASK, "Task quá hạn", msg, ENTITY_TASK, taskId);
                // Ghi nhận số lần trễ deadline (tăng 1 lần mỗi task mỗi 24h để tránh spam)
                User annotator = task.getAnnotator();
                annotator.setWarnings(annotator.getWarnings() + 1);
                userRepository.save(annotator);
            }
            // Reviewer (nếu khác annotator)
            UUID reviewerId = task.getReviewer().getId();
            if (!reviewerId.equals(annotatorId) && !notificationRepository.existsByUserIdAndTypeAndRelatedEntityIdAndCreatedAtAfter(reviewerId, TYPE_OVERDUE_TASK, taskId, since)) {
                create(reviewerId, TYPE_OVERDUE_TASK, "Task quá hạn", msg, ENTITY_TASK, taskId);
            }
            // Manager và Admin
            for (User manager : managers) {
                UUID userId = manager.getId();
                if (!notificationRepository.existsByUserIdAndTypeAndRelatedEntityIdAndCreatedAtAfter(userId, TYPE_OVERDUE_TASK, taskId, since)) {
                    create(userId, TYPE_OVERDUE_TASK, "Task quá hạn", msg, ENTITY_TASK, taskId);
                }
            }
        }

        // Overdue projects: notify managers and admins
        List<Project> overdueProjects = projectRepository.findByDeadlineIsNotNullAndDeadlineBefore(now);
        for (Project project : overdueProjects) {
            String msg = "Project \"" + project.getName() + "\" đã quá hạn.";
            UUID projectId = project.getId();
            for (User manager : managers) {
                UUID userId = manager.getId();
                if (!notificationRepository.existsByUserIdAndTypeAndRelatedEntityIdAndCreatedAtAfter(userId, TYPE_OVERDUE_PROJECT, projectId, since)) {
                    create(userId, TYPE_OVERDUE_PROJECT, "Project quá hạn", msg, ENTITY_PROJECT, projectId);
                }
            }
        }
    }

    private NotificationResponse toResponse(Notification n) {
        NotificationResponse r = new NotificationResponse();
        r.setId(n.getId());
        r.setType(n.getType());
        r.setTitle(n.getTitle());
        r.setMessage(n.getMessage());
        r.setRelatedEntityType(n.getRelatedEntityType());
        r.setRelatedEntityId(n.getRelatedEntityId());
        r.setRead(n.isRead());
        r.setCreatedAt(n.getCreatedAt());
        return r;
    }

    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("User not authenticated");
        }
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"))
                .getId();
    }
}
