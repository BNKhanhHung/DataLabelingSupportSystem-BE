package com.anotation.project;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO phản hồi thông tin dự án (project) cho API REST.
 * <p>
 * Dữ liệu được map từ entity {@link Project} kèm trạng thái tổng hợp
 * {@link ProjectStatus} (tính từ các task thuộc dự án), dùng cho danh sách/chi tiết
 * dự án phía client (Manager, v.v.).
 */
public class ProjectResponse {

    private UUID id;
    private String name;
    private String description;
    private ProjectStatus projectStatus;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProjectStatus getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(ProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }
}
