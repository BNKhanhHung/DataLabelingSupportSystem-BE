package com.anotation.task;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    List<TaskResponse> getAll();

    TaskResponse getById(UUID id);

    List<TaskResponse> getByProject(UUID projectId);

    List<TaskResponse> getByAnnotator(UUID annotatorId);

    TaskResponse create(TaskRequest request);

    TaskResponse updateStatus(UUID id, TaskStatus status);

    void delete(UUID id);
}
