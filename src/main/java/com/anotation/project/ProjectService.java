package com.anotation.project;

import com.anotation.common.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProjectService {
    PageResponse<ProjectResponse> getAll(Pageable pageable);

    PageResponse<ProjectResponse> searchByName(String name, Pageable pageable);

    ProjectResponse getById(UUID id);

    ProjectResponse create(ProjectRequest request);

    ProjectResponse update(UUID id, ProjectRequest request);

    void delete(UUID id);
}
