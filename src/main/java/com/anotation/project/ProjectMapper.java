package com.anotation.project;

import com.anotation.task.Task;
import com.anotation.task.TaskStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Mapper chuyển {@link Project} sang {@link ProjectResponse} và áp dụng {@link ProjectRequest} lên entity.
 * <p>
 * Khi có danh sách {@link Task}, trạng thái project ({@link ProjectStatus}) được suy ra bởi {@code computeStatus}
 * theo thứ tự ưu tiên: có task quá hạn → OVERDUE; tất cả COMPLETED → COMPLETED; có task đã bắt đầu → IN_PROGRESS;
 * ngược lại NOT_STARTED.
 * </p>
 */
@Component
public class ProjectMapper {

    /**
     * Ánh xạ entity sang DTO; không truyền task nên trạng thái suy ra từ danh sách rỗng (thường là NOT_STARTED).
     */
    public ProjectResponse toResponse(Project project) {
        return toResponse(project, List.of());
    }

    /**
     * Ánh xạ entity sang DTO kèm {@link ProjectStatus} tính từ danh sách task của project.
     */
    public ProjectResponse toResponse(Project project, List<Task> tasks) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setCreatedAt(project.getCreatedAt());
        response.setDeadline(project.getDeadline());
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
        project.setDeadline(request.getDeadline());
        return project;
    }

    /**
     * Ghi đè name, description, deadline từ request lên entity đã tồn tại.
     */
    public void updateEntity(ProjectRequest request, Project project) {
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setDeadline(request.getDeadline());
    }

    // ── Compute ProjectStatus from tasks ────────────────────────────────────────

    /**
     * Suy ra trạng thái tổng thể của project từ tập task hiện có.
     * <p>
     * Thứ tự ưu tiên:
     * </p>
     * <ol>
     *   <li>{@link ProjectStatus#OVERDUE} — có ít nhất một task đã quá {@code dueDate} và chưa ở trạng thái kết thúc (COMPLETED/REVIEWED)</li>
     *   <li>{@link ProjectStatus#COMPLETED} — mọi task đều COMPLETED</li>
     *   <li>{@link ProjectStatus#IN_PROGRESS} — có ít nhất một task khác OPEN</li>
     *   <li>{@link ProjectStatus#NOT_STARTED} — không có task hoặc tất cả vẫn OPEN</li>
     * </ol>
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
