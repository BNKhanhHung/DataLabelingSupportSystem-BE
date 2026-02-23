package com.anotation.annotation;

import com.anotation.exception.BadRequestException;
import com.anotation.exception.DuplicateException;
import com.anotation.exception.NotFoundException;
import com.anotation.dataitem.DataItem;
import com.anotation.dataitem.DataItemRepository;
import com.anotation.dataitem.DataItemStatus;
import com.anotation.task.Task;
import com.anotation.task.TaskItem;
import com.anotation.task.TaskItemRepository;
import com.anotation.task.TaskRepository;
import com.anotation.task.TaskStatus;
import com.anotation.user.User;
import com.anotation.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AnnotationServiceImpl implements AnnotationService {

    private final AnnotationRepository annotationRepository;
    private final TaskItemRepository taskItemRepository;
    private final TaskRepository taskRepository;
    private final DataItemRepository dataItemRepository;
    private final UserRepository userRepository;
    private final AnnotationMapper annotationMapper;

    public AnnotationServiceImpl(AnnotationRepository annotationRepository,
            TaskItemRepository taskItemRepository,
            TaskRepository taskRepository,
            DataItemRepository dataItemRepository,
            UserRepository userRepository,
            AnnotationMapper annotationMapper) {
        this.annotationRepository = annotationRepository;
        this.taskItemRepository = taskItemRepository;
        this.taskRepository = taskRepository;
        this.dataItemRepository = dataItemRepository;
        this.userRepository = userRepository;
        this.annotationMapper = annotationMapper;
    }

    // ── Read ─────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<AnnotationResponse> getAll() {
        return annotationRepository.findAll().stream()
                .map(annotationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AnnotationResponse getById(UUID id) {
        return annotationMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnotationResponse> getByTask(UUID taskId) {
        return annotationRepository.findByTaskId(taskId).stream()
                .map(annotationMapper::toResponse)
                .toList();
    }

    // ── Submit (Create) ──────────────────────────────────────────────────────────

    @Override
    public AnnotationResponse submit(AnnotationRequest request) {
        // 1. TaskItem must exist
        TaskItem taskItem = taskItemRepository.findById(request.getTaskItemId())
                .orElseThrow(() -> new NotFoundException(
                        "TaskItem not found: " + request.getTaskItemId()));

        Task task = taskItem.getTask();
        DataItem dataItem = taskItem.getDataItem();

        // 2. Only Task.annotator can submit
        UUID taskAnnotatorId = task.getAnnotator().getId();
        if (!taskAnnotatorId.equals(request.getAnnotatorId())) {
            throw new BadRequestException(
                    "User " + request.getAnnotatorId() + " is not the annotator of this task.");
        }

        // 3. Annotator user must exist
        User annotator = userRepository.findById(request.getAnnotatorId())
                .orElseThrow(() -> new NotFoundException(
                        "Annotator not found: " + request.getAnnotatorId()));

        // 4. DataItem must be ASSIGNED
        if (dataItem.getStatus() != DataItemStatus.ASSIGNED) {
            throw new BadRequestException(
                    "DataItem is not ASSIGNED (current: " + dataItem.getStatus() + ")");
        }

        // 5. Prevent duplicate annotation for same TaskItem
        if (annotationRepository.existsByTaskItemId(taskItem.getId())) {
            throw new DuplicateException(
                    "An annotation already exists for TaskItem: " + taskItem.getId());
        }

        // Create Annotation
        Annotation annotation = new Annotation();
        annotation.setTaskItem(taskItem);
        annotation.setAnnotator(annotator);
        annotation.setContent(request.getContent());
        // status defaults to SUBMITTED via @PrePersist
        annotationRepository.save(annotation);

        // 6. DataItem status → ANNOTATED
        dataItem.setStatus(DataItemStatus.ANNOTATED);
        dataItemRepository.save(dataItem);

        // 7. Task status → IN_PROGRESS (on first annotation submission)
        if (task.getStatus() == TaskStatus.OPEN) {
            task.setStatus(TaskStatus.IN_PROGRESS);
            taskRepository.save(task);
        }

        return annotationMapper.toResponse(annotation);
    }

    // ── Update content ───────────────────────────────────────────────────────────

    @Override
    public AnnotationResponse updateContent(UUID id, String content) {
        Annotation annotation = findOrThrow(id);

        // Only allow update if REJECTED (needs rework)
        if (annotation.getStatus() != AnnotationStatus.REJECTED) {
            throw new BadRequestException(
                    "Only REJECTED annotations can be updated. Current status: " + annotation.getStatus());
        }

        annotation.setContent(content);
        annotation.setStatus(AnnotationStatus.SUBMITTED); // re-submit after fix
        return annotationMapper.toResponse(annotationRepository.save(annotation));
    }

    // ── Delete ───────────────────────────────────────────────────────────────────

    @Override
    public void delete(UUID id) {
        findOrThrow(id);
        annotationRepository.deleteById(id);
    }

    // ── Private helpers ─────────────────────────────────────────────────────────

    private Annotation findOrThrow(UUID id) {
        return annotationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Annotation not found with id: " + id));
    }
}
