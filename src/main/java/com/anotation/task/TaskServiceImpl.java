package com.anotation.task;

import com.anotation.common.PageResponse;
import com.anotation.exception.BadRequestException;
import com.anotation.exception.NotFoundException;
import com.anotation.dataitem.DataItem;
import com.anotation.dataitem.DataItemRepository;
import com.anotation.dataitem.DataItemStatus;
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
import java.util.UUID;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskItemRepository taskItemRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final DataItemRepository dataItemRepository;
    private final TaskMapper taskMapper;

    public TaskServiceImpl(TaskRepository taskRepository,
            TaskItemRepository taskItemRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository,
            UserRoleRepository userRoleRepository,
            DataItemRepository dataItemRepository,
            TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskItemRepository = taskItemRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.dataItemRepository = dataItemRepository;
        this.taskMapper = taskMapper;
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
    public PageResponse<TaskResponse> getByProject(UUID projectId, Pageable pageable) {
        try {
            return PageResponse.from(taskRepository.findByProjectId(projectId, pageable),
                    this::toResponse);
        } catch (PropertyReferenceException e) {
            return PageResponse.from(taskRepository.findByProjectId(projectId, safePageable(pageable)),
                    this::toResponse);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> getByAnnotator(UUID annotatorId, Pageable pageable) {
        try {
            return PageResponse.from(taskRepository.findByAnnotatorId(annotatorId, pageable),
                    this::toResponse);
        } catch (PropertyReferenceException e) {
            return PageResponse.from(taskRepository.findByAnnotatorId(annotatorId, safePageable(pageable)),
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
        if (!isAdmin && !userRoleRepository.existsByUserIdAndRoleNameIgnoreCase(
                currentUser.getId(), "Manager")) {
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

        return taskMapper.toResponse(task, dataItemIds);
    }

    // ── Update status ────────────────────────────────────────────────────────────

    @Override
    public TaskResponse updateStatus(UUID id, TaskStatus status) {
        Task task = findOrThrow(id);
        task.setStatus(status);
        taskRepository.save(task);
        return toResponse(task);
    }

    // ── Delete ───────────────────────────────────────────────────────────────────

    @Override
    public void delete(UUID id) {
        findOrThrow(id);
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
