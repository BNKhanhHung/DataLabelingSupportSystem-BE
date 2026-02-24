package com.anotation.project;

import java.util.List;
import java.util.UUID;

public interface ProjectService {
    List<ProjectResponse> getAll();

    ProjectResponse getById(UUID id);

    ProjectResponse create(ProjectRequest request);

    ProjectResponse update(UUID id, ProjectRequest request);

    void delete(UUID id);
}
