package com.anotation.project;

import com.anotation.task.Task;
import com.anotation.task.TaskStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ProjectMapper {

    /**
     * Convert Entity → Response DTO (without task-based status).
     */
    public ProjectResponse toResponse(Project project) {
        return toResponse(project, List.of());
    }

    /**
     * Convert Entity → Response DTO with computed ProjectStatus from tasks.
     */
    public ProjectResponse toResponse(Project project, List<Task> tasks) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setCreatedAt(project.getCreatedAt());
        response.setProjectStatus(computeStatus(tasks));
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

    // ── Compute ProjectStatus from tasks ────────────────────────────────────────

    /**
     * Determines the project status based on the current state of its tasks.
     *
     * Priority order:
     * 1. OVERDUE — at least 1 task past dueDate and not COMPLETED/REVIEWED
     * 2. COMPLETED — all tasks exist and all are COMPLETED
     * 3. IN_PROGRESS — at least 1 task has moved beyond OPEN
     * 4. NOT_STARTED — no tasks, or all tasks still OPEN
     */
    private ProjectStatus computeStatus(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return ProjectStatus.NOT_STARTED;
        }

        LocalDateTime now = LocalDateTime.now();
        boolean hasOverdue = false;
        boolean allCompleted = true;
        boolean anyStarted = false;

        for (Task t : tasks) {
            // Check overdue: dueDate passed AND task not finished
            if (t.getDueDate() != null
                    && now.isAfter(t.getDueDate())
                    && t.getStatus() != TaskStatus.COMPLETED
                    && t.getStatus() != TaskStatus.REVIEWED) {
                hasOverdue = true;
            }

            // Check if all completed
            if (t.getStatus() != TaskStatus.COMPLETED) {
                allCompleted = false;
            }

            // Check if any work has started (beyond OPEN)
            if (t.getStatus() != TaskStatus.OPEN) {
                anyStarted = true;
            }
        }

        if (hasOverdue)
            return ProjectStatus.OVERDUE;
        if (allCompleted)
            return ProjectStatus.COMPLETED;
        if (anyStarted)
            return ProjectStatus.IN_PROGRESS;
        return ProjectStatus.NOT_STARTED;
    }
}
