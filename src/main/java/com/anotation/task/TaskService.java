package com.anotation.task;

import com.anotation.common.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TaskService {
    PageResponse<TaskResponse> getAll(Pageable pageable);

    TaskResponse getById(UUID id);

    PageResponse<TaskResponse> getByProject(UUID projectId, Pageable pageable);

    PageResponse<TaskResponse> getByAnnotator(UUID annotatorId, Pageable pageable);

    PageResponse<TaskResponse> search(String name, TaskStatus status, Pageable pageable);

    TaskResponse create(TaskRequest request);

    TaskResponse updateStatus(UUID id, TaskStatus status);

    void delete(UUID id);
}
