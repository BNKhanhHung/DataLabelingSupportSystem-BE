package com.anotation.task;

import com.anotation.annotation.AnnotationRepository;
import com.anotation.common.PageResponse;
import com.anotation.reviewfeedback.ReviewFeedbackRepository;
import com.anotation.exception.BadRequestException;
import com.anotation.exception.NotFoundException;
import com.anotation.dataitem.DataItem;
import com.anotation.dataitem.DataItemRepository;
import com.anotation.dataitem.DataItemStatus;
import com.anotation.notification.NotificationService;
import com.anotation.project.Project;
import com.anotation.project.ProjectRepository;
import com.anotation.user.User;
import com.anotation.user.UserRepository;
import com.anotation.user.SystemRole;
import com.anotation.userrole.UserRoleRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    /** Giới hạn tối đa số Task đang hoạt động (OPEN/IN_PROGRESS/SUBMITTED) mỗi người. */
    private static final int MAX_ACTIVE_TASKS = 3;

    private final TaskRepository taskRepository;
    private final TaskItemRepository taskItemRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final DataItemRepository dataItemRepository;
    private final TaskMapper taskMapper;
    private final AnnotationRepository annotationRepository;
    private final ReviewFeedbackRepository reviewFeedbackRepository;
    private final NotificationService notificationService;

    public TaskServiceImpl(TaskRepository taskRepository,
            TaskItemRepository taskItemRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            DataItemRepository dataItemRepository,
            TaskMapper taskMapper,
            AnnotationRepository annotationRepository,
            ReviewFeedbackRepository reviewFeedbackRepository,
            NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.taskItemRepository = taskItemRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.dataItemRepository = dataItemRepository;
        this.taskMapper = taskMapper;
        this.annotationRepository = annotationRepository;
        this.reviewFeedbackRepository = reviewFeedbackRepository;
        this.notificationService = notificationService;
    }

    // ── Read operations ──────────────────────────────────────────────────────────

    private static Pageable safePageable(Pageable pageable) {
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id"));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> getAll(Pageable pageable) {
        try {
            return PageResponse.from(taskRepository.findAll(pageable), this::toResponse);
        } catch (PropertyReferenceException e) {
            return PageResponse.from(taskRepository.findAll(safePageable(pageable)), this::toResponse);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getById(UUID id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskItemResponse> getTaskItems(UUID taskId) {
        Task task = findOrThrow(taskId);
        User currentUser = getCurrentUser();
        boolean isAnnotator = task.getAnnotator().getId().equals(currentUser.getId());
        boolean isManagerOrAdmin = currentUser.getSystemRole() == SystemRole.MANAGER
                || currentUser.getSystemRole() == SystemRole.ADMIN;
        if (!isAnnotator && !isManagerOrAdmin) {
            throw new BadRequestException("Only the assigned annotator or Manager/Admin can view task items.");
        }
        List<TaskItem> items = taskItemRepository.findByTaskId(task.getId());
        List<TaskItemResponse> result = new ArrayList<>();
        for (TaskItem ti : items) {
            DataItem di = ti.getDataItem();
            boolean hasAnnotation = annotationRepository.existsByTaskItemId(ti.getId());
            result.add(new TaskItemResponse(
                    ti.getId(),
                    di.getId(),
                    di.getContentUrl() != null ? di.getContentUrl() : "",
                    hasAnnotation));
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> getByProject(UUID projectId, Pageable pageable) {
        try {
            return PageResponse.from(taskRepository.findByProjectId(projectId, pageable),
                    this::toResponse);
        } catch (PropertyReferenceException e) {
            return PageResponse.from(taskRepository.findByProjectId(projectId, safePageable(pageable)),
                    this::toResponse);
        }
    }

    /** Trạng thái đã xong: không hiện trong danh sách "task được giao" của user. */
    private static final List<TaskStatus> EXCLUDED_STATUSES_FOR_ASSIGNED =
            List.of(TaskStatus.COMPLETED, TaskStatus.REVIEWED);

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> getByAnnotator(UUID annotatorId, Pageable pageable) {
        try {
            return PageResponse.from(
                    taskRepository.findByAnnotatorIdAndStatusNotIn(annotatorId, EXCLUDED_STATUSES_FOR_ASSIGNED, pageable),
                    this::toResponse);
        } catch (PropertyReferenceException e) {
            return PageResponse.from(
                    taskRepository.findByAnnotatorIdAndStatusNotIn(annotatorId, EXCLUDED_STATUSES_FOR_ASSIGNED, safePageable(pageable)),
                    this::toResponse);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> getAssignedInProgressByAnnotator(UUID annotatorId, Pageable pageable) {
        try {
            return PageResponse.from(
                    taskRepository.findByAnnotatorIdAndStatus(annotatorId, TaskStatus.IN_PROGRESS, pageable),
                    this::toResponse);
        } catch (PropertyReferenceException e) {
            return PageResponse.from(
                    taskRepository.findByAnnotatorIdAndStatus(annotatorId, TaskStatus.IN_PROGRESS, safePageable(pageable)),
                    this::toResponse);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> getByReviewer(UUID reviewerId, Pageable pageable) {
        List<TaskStatus> reviewQueueStatuses = List.of(TaskStatus.SUBMITTED, TaskStatus.OVERDUE);
        try {
            return PageResponse.from(
                    taskRepository.findByReviewerIdAndStatusIn(reviewerId, reviewQueueStatuses, pageable),
                    this::toResponse);
        } catch (PropertyReferenceException e) {
            return PageResponse.from(
                    taskRepository.findByReviewerIdAndStatusIn(reviewerId, reviewQueueStatuses, safePageable(pageable)),
                    this::toResponse);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> getAssignedInProgressByReviewer(UUID reviewerId, Pageable pageable) {
        try {
            return PageResponse.from(
                    taskRepository.findByReviewerIdAndStatus(reviewerId, TaskStatus.IN_PROGRESS, pageable),
                    this::toResponse);
        } catch (PropertyReferenceException e) {
            return PageResponse.from(
                    taskRepository.findByReviewerIdAndStatus(reviewerId, TaskStatus.IN_PROGRESS, safePageable(pageable)),
                    this::toResponse);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> search(String name, TaskStatus status, Pageable pageable) {
        try {
            return PageResponse.from(taskRepository.search(name, status, pageable), this::toResponse);
        } catch (PropertyReferenceException e) {
            return PageResponse.from(taskRepository.search(name, status, safePageable(pageable)),
                    this::toResponse);
        }
    }

    // ── Create ───────────────────────────────────────────────────────────────────

    @Override
    public TaskResponse create(TaskRequest request) {
        User currentUser = getCurrentUser();
        boolean isAdmin = currentUser.getSystemRole() == SystemRole.ADMIN;
        boolean isSystemManager = currentUser.getSystemRole() == SystemRole.MANAGER;
        boolean hasManagerRole = userRoleRepository.existsByUserIdAndRoleNameIgnoreCase(
                currentUser.getId(), "Manager");
        if (!isAdmin && !isSystemManager && !hasManagerRole) {
            throw new BadRequestException("Only Manager can create tasks.");
        }

        UUID projectId = request.getProjectId();
        UUID annotatorId = request.getAnnotatorId();
        UUID reviewerId = request.getReviewerId();

        // 1. Project must exist
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found: " + projectId));

        // 2. Annotator must exist
        User annotator = userRepository.findById(annotatorId)
                .orElseThrow(() -> new NotFoundException("Annotator not found: " + annotatorId));

        // 3. Reviewer must exist
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new NotFoundException("Reviewer not found: " + reviewerId));

        // 4. Annotator must have role "Annotator"
        if (!userRoleRepository.existsByUserIdAndRoleNameIgnoreCase(
                annotatorId, "Annotator")) {
            throw new BadRequestException(
                    "User " + annotatorId + " does not have role 'Annotator'.");
        }

        // 5. Reviewer must have role "Reviewer"
        if (!userRoleRepository.existsByUserIdAndRoleNameIgnoreCase(
                reviewerId, "Reviewer")) {
            throw new BadRequestException(
                    "User " + reviewerId + " does not have role 'Reviewer'.");
        }

        // 5.1 WIP Limit: Annotator không được ôm quá MAX_ACTIVE_TASKS task đang hoạt động
        long annotatorActiveCount = taskRepository.countActiveTasksByAnnotatorId(annotatorId);
        if (annotatorActiveCount >= MAX_ACTIVE_TASKS) {
            throw new BadRequestException(
                    "Annotator " + annotator.getUsername() + " đã đạt giới hạn "
                    + MAX_ACTIVE_TASKS + " task đang hoạt động. Vui lòng chờ hoàn thành task cũ.");
        }

        // 5.2 WIP Limit: Reviewer không được ôm quá MAX_ACTIVE_TASKS task đang hoạt động
        long reviewerActiveCount = taskRepository.countActiveTasksByReviewerId(reviewerId);
        if (reviewerActiveCount >= MAX_ACTIVE_TASKS) {
            throw new BadRequestException(
                    "Reviewer " + reviewer.getUsername() + " đã đạt giới hạn "
                    + MAX_ACTIVE_TASKS + " task đang hoạt động. Vui lòng chờ hoàn thành task cũ.");
        }

        // 6 & 7. Validate each DataItem
        List<DataItem> dataItems = new ArrayList<>();
        for (UUID dataItemId : request.getDataItemIds()) {
            DataItem item = dataItemRepository.findById(dataItemId)
                    .orElseThrow(() -> new NotFoundException("DataItem not found: " + dataItemId));

            // Must belong to same project (via Dataset → Project)
            UUID itemProjectId = item.getDataset().getProject().getId();
            if (!itemProjectId.equals(projectId)) {
                throw new BadRequestException(
                        "DataItem " + dataItemId + " does not belong to project " + projectId);
            }

            // Must be status NEW
            if (item.getStatus() != DataItemStatus.NEW) {
                throw new BadRequestException(
                        "DataItem " + dataItemId + " is not NEW (current: " + item.getStatus() + ")");
            }

            // Must not be in another active task
            if (taskItemRepository.existsActiveTaskForDataItem(dataItemId)) {
                throw new BadRequestException(
                        "DataItem " + dataItemId + " is already assigned to an active task.");
            }

            dataItems.add(item);
        }

        // Create Task
        Task task = new Task();
        task.setProject(project);
        task.setAnnotator(annotator);
        task.setReviewer(reviewer);

        // Set deadline: nếu có gửi lên thì dùng, không thì mặc định 24h sau khi tạo
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        if (request.getDueDate() != null) {
            if (request.getDueDate().isBefore(now)) {
                throw new BadRequestException("Due date must be in the future.");
            }
            task.setDueDate(request.getDueDate());
        } else {
            task.setDueDate(now.plusHours(24));
        }

        taskRepository.save(task);

        // Create TaskItems + update DataItem status → ASSIGNED
        List<UUID> dataItemIds = new ArrayList<>();
        for (DataItem item : dataItems) {
            TaskItem taskItem = new TaskItem();
            taskItem.setTask(task);
            taskItem.setDataItem(item);
            taskItemRepository.save(taskItem);

            // 8. Set DataItem status → ASSIGNED
            item.setStatus(DataItemStatus.ASSIGNED);
            dataItemRepository.save(item);

            dataItemIds.add(item.getId());
        }

        // 9. Task đã phân công xong → tự cập nhật status IN_PROGRESS
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskRepository.save(task);

        // Thông báo chỉ cho Annotator khi task được phân công. Reviewer chỉ nhận thông báo khi annotator submit (SUBMITTED).
        String projectName = task.getProject().getName();
        notificationService.create(annotatorId, "TASK_ASSIGNED", "Task mới được phân công",
                "Bạn có task mới trong dự án \"" + projectName + "\".", "TASK", task.getId());

        return taskMapper.toResponse(task, dataItemIds);
    }

    // ── Update status ────────────────────────────────────────────────────────────

    @Override
    public TaskResponse updateStatus(UUID id, TaskStatus status) {
        Task task = findOrThrow(id);
        task.setStatus(status);
        taskRepository.save(task);
        String msg = "Manager đã cập nhật trạng thái task thành \"" + status + "\".";
        notificationService.create(task.getAnnotator().getId(), "TASK_STATUS_UPDATED", "Trạng thái task đã thay đổi", msg, "TASK", id);
        // Reviewer chỉ nhận thông báo khi trạng thái chuyển sang SUBMITTED (annotator đã gán nhãn xong và nộp)
        if (status == TaskStatus.SUBMITTED && !task.getReviewer().getId().equals(task.getAnnotator().getId())) {
            notificationService.create(task.getReviewer().getId(), "TASK_STATUS_UPDATED", "Task đã được nộp để review",
                    "Task trong dự án \"" + task.getProject().getName() + "\" đã được nộp. Vui lòng review.", "TASK", id);
        }
        return toResponse(task);
    }

    @Override
    public TaskResponse updateDueDate(UUID id, java.time.LocalDateTime dueDate) {
        Task task = findOrThrow(id);
        task.setDueDate(dueDate);
        taskRepository.save(task);
        return toResponse(task);
    }

    // ── Annotator: nộp task cho Reviewer ─────────────────────────────────────────

    @Override
    public TaskResponse submitForReview(UUID taskId) {
        Task task = findOrThrow(taskId);
        User currentUser = getCurrentUser();

        // 1. Chỉ Annotator của task mới được nộp
        if (!task.getAnnotator().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Only the assigned annotator can submit this task for review.");
        }

        // 2. Task phải đang IN_PROGRESS hoặc DENIED hoặc OVERDUE (nộp lại sau khi sửa / nộp trễ)
        if (task.getStatus() != TaskStatus.IN_PROGRESS
                && task.getStatus() != TaskStatus.DENIED
                && task.getStatus() != TaskStatus.OVERDUE) {
            throw new BadRequestException(
                    "Task must be IN_PROGRESS or DENIED to submit for review. Current status: " + task.getStatus());
        }

        // 3. Tất cả TaskItems phải đã có annotation
        long missingCount = taskItemRepository.countItemsWithoutAnnotation(taskId);
        if (missingCount > 0) {
            throw new BadRequestException(
                    "Cannot submit: " + missingCount + " item(s) have not been annotated yet.");
        }

        // Chuyển trạng thái → SUBMITTED
        task.setStatus(TaskStatus.SUBMITTED);
        taskRepository.save(task);

        notificationService.create(task.getReviewer().getId(), "TASK_STATUS_UPDATED", "Task đã được nộp để review",
                "Annotator đã nộp task trong dự án \"" + task.getProject().getName() + "\". Vui lòng review.", "TASK", taskId);

        return toResponse(task);
    }

    // ── Reviewer: hoàn tất review và gửi kết quả ────────────────────────────────

    @Override
    public TaskResponse completeReview(UUID taskId) {
        Task task = findOrThrow(taskId);
        User currentUser = getCurrentUser();

        // 1. Chỉ Reviewer của task mới được hoàn tất review
        if (!task.getReviewer().getId().equals(currentUser.getId())) {
            boolean isManagerOrAdmin = currentUser.getSystemRole() == SystemRole.MANAGER
                    || currentUser.getSystemRole() == SystemRole.ADMIN;
            if (!isManagerOrAdmin) {
                throw new BadRequestException("Only the assigned reviewer or Manager/Admin can complete review.");
            }
        }

        // 2. Task phải đang SUBMITTED hoặc OVERDUE (quá hạn nhưng vẫn cho reviewer hoàn tất)
        if (task.getStatus() != TaskStatus.SUBMITTED && task.getStatus() != TaskStatus.OVERDUE) {
            throw new BadRequestException(
                    "Task must be SUBMITTED or OVERDUE to complete review. Current status: " + task.getStatus());
        }

        // 3. Tất cả annotations phải đã được review (không còn SUBMITTED)
        long pendingCount = annotationRepository.countSubmittedByTaskId(taskId);
        if (pendingCount > 0) {
            throw new BadRequestException(
                    "Cannot complete review: " + pendingCount + " annotation(s) have not been reviewed yet.");
        }

        // 4. Kiểm tra có annotation bị REJECTED không
        long rejectedCount = annotationRepository.countRejectedByTaskId(taskId);
        if (rejectedCount > 0) {
            // Có annotation bị từ chối → task DENIED (Annotator sửa lại)
            task.setStatus(TaskStatus.DENIED);
            taskRepository.save(task);
            notificationService.create(task.getAnnotator().getId(), "TASK_STATUS_UPDATED", "Task cần chỉnh sửa",
                    "Reviewer đã từ chối một số nhãn. Vui lòng chỉnh sửa và nộp lại.", "TASK", taskId);
            return toResponse(task);
        }

        // 5. Tất cả APPROVED → gửi thông báo cho Annotator và cho Manager(s)
        task.setStatus(TaskStatus.REVIEWED);
        taskRepository.save(task);
        String projectName = task.getProject().getName();
        notificationService.create(task.getAnnotator().getId(), "TASK_STATUS_UPDATED", "Task đã được review xong",
                "Task trong dự án \"" + projectName + "\" đã được reviewer duyệt.", "TASK", taskId);
        for (User manager : userRepository.findBySystemRoleIn(Set.of(SystemRole.MANAGER, SystemRole.ADMIN))) {
            notificationService.create(manager.getId(), "TASK_STATUS_UPDATED", "Task đã được review xong",
                    "Reviewer đã hoàn tất review task trong dự án \"" + projectName + "\". Bạn có thể xem và xác nhận.", "TASK", taskId);
        }
        return toResponse(task);
    }

    // ── Overdue tasks ────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> getOverdueTasks(Pageable pageable) {
        try {
            return PageResponse.from(
                    taskRepository.findOverdueTasks(java.time.LocalDateTime.now(), pageable),
                    this::toResponse);
        } catch (PropertyReferenceException e) {
            return PageResponse.from(
                    taskRepository.findOverdueTasks(java.time.LocalDateTime.now(), safePageable(pageable)),
                    this::toResponse);
        }
    }

    @Override
    public void markOverdueTasks() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        List<Task> tasks = taskRepository.findTasksToMarkOverdue(now, PageRequest.of(0, 500, Sort.unsorted()));
        if (tasks.isEmpty()) return;
        for (Task t : tasks) {
            TaskStatus previousStatus = t.getStatus();
            t.setStatus(TaskStatus.OVERDUE);

            // Xác định người chịu lỗi trễ hạn theo trạng thái trước khi bị chuyển OVERDUE:
            // - SUBMITTED  -> reviewer trễ review
            // - còn lại    -> annotator trễ gán nhãn/nộp
            User responsibleUser = (previousStatus == TaskStatus.SUBMITTED) ? t.getReviewer() : t.getAnnotator();
            responsibleUser.setWarnings(responsibleUser.getWarnings() + 1);
            userRepository.save(responsibleUser);

            String projectName = t.getProject() != null ? t.getProject().getName() : "N/A";
            String roleLabel = (previousStatus == TaskStatus.SUBMITTED) ? "reviewer" : "annotator";
            String msg = "Task thuộc project \"" + projectName + "\" đã quá hạn (" + roleLabel + " chưa hoàn thành đúng hạn).";
            notificationService.create(responsibleUser.getId(), "DEADLINE_OVERDUE_TASK", "Task quá hạn", msg, "TASK", t.getId());
        }
        taskRepository.saveAll(tasks);
    }

    // ── KPI ──────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public KpiResponse getAnnotatorKpi(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        KpiResponse kpi = new KpiResponse();
        kpi.setUserId(user.getId());
        kpi.setUsername(user.getUsername());

        // Task metrics
        kpi.setTotalTasks(taskRepository.countByAnnotatorId(userId));
        kpi.setCompletedTasks(
                taskRepository.countByAnnotatorIdAndStatus(userId, TaskStatus.COMPLETED)
                        + taskRepository.countByAnnotatorIdAndStatus(userId, TaskStatus.REVIEWED));
        kpi.setOverdueTasks(
                taskRepository.countOverdueByAnnotatorId(userId, java.time.LocalDateTime.now()));

        // Annotation metrics
        kpi.setTotalAnnotations(annotationRepository.countByAnnotatorUserId(userId));
        kpi.setApprovedCount(annotationRepository.countApprovedByAnnotatorUserId(userId));
        kpi.setRejectedCount(annotationRepository.countRejectedByAnnotatorUserId(userId));

        // Approval rate
        long reviewed = kpi.getApprovedCount() + kpi.getRejectedCount();
        kpi.setApprovalRate(reviewed > 0
                ? Math.round((double) kpi.getApprovedCount() / reviewed * 10000.0) / 100.0
                : 0.0);

        return kpi;
    }

    // ── Refuse Task ─────────────────────────────────────────────────────────────

    @Override
    public TaskResponse refuseTask(UUID taskId, String reason) {
        Task task = findOrThrow(taskId);
        User currentUser = getCurrentUser();
        UUID currentUserId = currentUser.getId();

        // 1. Chỉ Annotator hoặc Reviewer được giao mới có quyền từ chối
        boolean isAssignedAnnotator = task.getAnnotator().getId().equals(currentUserId);
        boolean isAssignedReviewer = task.getReviewer().getId().equals(currentUserId);
        if (!isAssignedAnnotator && !isAssignedReviewer) {
            throw new BadRequestException(
                    "Only the assigned Annotator or Reviewer can refuse this task.");
        }

        // 2. Chỉ được từ chối task ở trạng thái chưa bắt đầu / đang làm / quá hạn
        if (task.getStatus() != TaskStatus.OPEN
                && task.getStatus() != TaskStatus.IN_PROGRESS
                && task.getStatus() != TaskStatus.OVERDUE) {
            throw new BadRequestException(
                    "Only tasks in OPEN, IN_PROGRESS or OVERDUE status can be refused. Current: " + task.getStatus());
        }

        // 3. Trả Task về trạng thái OPEN (vô chủ, chờ Manager giao lại)
        task.setStatus(TaskStatus.OPEN);
        taskRepository.save(task);

        // 4. Bắn Notification cho tất cả Manager biết Task bị trả lại
        String roleName = isAssignedAnnotator ? "Annotator" : "Reviewer";
        String projectName = task.getProject().getName();
        String notifMessage = roleName + " " + currentUser.getUsername()
                + " đã từ chối task trong dự án \"" + projectName
                + "\". Lý do: " + reason;

        for (User manager : userRepository.findBySystemRoleIn(
                java.util.Set.of(com.anotation.user.SystemRole.MANAGER, com.anotation.user.SystemRole.ADMIN))) {
            notificationService.create(manager.getId(), "TASK_REFUSED",
                    "Task bị từ chối", notifMessage, "TASK", taskId);
        }

        return toResponse(task);
    }

    @Override
    @Transactional
    public TaskResponse assign(UUID taskId, UUID annotatorId, UUID reviewerId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found: " + taskId));

        TaskStatus st = task.getStatus();
        if (st == TaskStatus.SUBMITTED || st == TaskStatus.REVIEWED || st == TaskStatus.COMPLETED) {
            throw new BadRequestException("Cannot re-assign task in status: " + st);
        }

        User annotator = userRepository.findById(annotatorId)
                .orElseThrow(() -> new NotFoundException("Annotator not found: " + annotatorId));
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new NotFoundException("Reviewer not found: " + reviewerId));

        if (!userRoleRepository.existsByUserIdAndRoleNameIgnoreCase(annotatorId, "Annotator")) {
            throw new BadRequestException("User " + annotatorId + " does not have role 'Annotator'.");
        }
        if (!userRoleRepository.existsByUserIdAndRoleNameIgnoreCase(reviewerId, "Reviewer")) {
            throw new BadRequestException("User " + reviewerId + " does not have role 'Reviewer'.");
        }

        // WIP limit tương tự lúc tạo task
        long annotatorActiveCount = taskRepository.countActiveTasksByAnnotatorId(annotatorId);
        if (annotatorActiveCount >= MAX_ACTIVE_TASKS) {
            throw new BadRequestException("Annotator has reached max active tasks: " + MAX_ACTIVE_TASKS);
        }
        long reviewerActiveCount = taskRepository.countActiveTasksByReviewerId(reviewerId);
        if (reviewerActiveCount >= MAX_ACTIVE_TASKS) {
            throw new BadRequestException("Reviewer has reached max active tasks: " + MAX_ACTIVE_TASKS);
        }

        task.setAnnotator(annotator);
        task.setReviewer(reviewer);
        Task saved = taskRepository.save(task);

        // Notify annotator về việc được phân công (consistent với create)
        notificationService.create(annotatorId, "TASK_ASSIGNED", "Task được phân công",
                "Bạn được phân công task cho project: " + (saved.getProject() != null ? saved.getProject().getName() : ""),
                "TASK", saved.getId());

        return toResponse(saved);
    }

    // ── Delete ───────────────────────────────────────────────────────────────────

    @Override
    public void delete(UUID id) {
        Task task = findOrThrow(id);
        UUID taskId = task.getId();
        // Xóa theo thứ tự phụ thuộc: review_feedbacks → annotations → task_items → task
        reviewFeedbackRepository.findByTaskId(taskId, Pageable.unpaged()).getContent()
                .forEach(reviewFeedbackRepository::delete);
        annotationRepository.findByTaskId(taskId, Pageable.unpaged()).getContent()
                .forEach(annotationRepository::delete);
        taskItemRepository.findByTaskId(taskId).forEach(taskItemRepository::delete);
        taskRepository.deleteById(id);
    }

    // ── Private helpers ─────────────────────────────────────────────────────────

    private Task findOrThrow(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found with id: " + id));
    }

    private TaskResponse toResponse(Task task) {
        List<UUID> dataItemIds = taskItemRepository.findByTaskId(task.getId())
                .stream()
                .map(ti -> ti.getDataItem().getId())
                .toList();
        return taskMapper.toResponse(task, dataItemIds);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BadRequestException("Unauthorized.");
        }
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new BadRequestException("Invalid token or user not found."));
    }
}
