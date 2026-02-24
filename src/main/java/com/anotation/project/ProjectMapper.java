package com.anotation.project;

import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    /**
     * Convert Entity → Response DTO
     */
    public ProjectResponse toResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setCreatedAt(project.getCreatedAt());
        return response;
    }

    /**
     * Apply Request DTO → Entity (create)
     */
    public Project toEntity(ProjectRequest request) {
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        return project;
    }

    /**
     * Apply Request DTO → existing Entity (update)
     */
    public void updateEntity(ProjectRequest request, Project project) {
        project.setName(request.getName());
        project.setDescription(request.getDescription());
    }
}
