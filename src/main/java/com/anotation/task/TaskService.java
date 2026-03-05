package com.anotation.task;

import com.anotation.common.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    PageResponse<TaskResponse> getAll(Pageable pageable);

    TaskResponse getById(UUID id);

    /** Items in this task (for annotator labeling: taskItemId, dataItemId, contentUrl, hasAnnotation). */
    List<TaskItemResponse> getTaskItems(UUID taskId);

    PageResponse<TaskResponse> getByProject(UUID projectId, Pageable pageable);

    PageResponse<TaskResponse> getByAnnotator(UUID annotatorId, Pageable pageable);

    PageResponse<TaskResponse> getByReviewer(UUID reviewerId, Pageable pageable);

    PageResponse<TaskResponse> search(String name, TaskStatus status, Pageable pageable);

    TaskResponse create(TaskRequest request);

    TaskResponse updateStatus(UUID id, TaskStatus status);

    void delete(UUID id);
}
