package com.anotation.dataset;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO đầu ra trả về cho client sau khi đọc hoặc ghi dataset.
 * <p>
 * Chứa định danh, tên, mô tả, thời điểm tạo và thông tin project đã làm phẳng ({@code projectId}, {@code projectName})
 * để client hiển thị mà không cần nested object project đầy đủ.
 * </p>
 */
public class DatasetResponse {

    private UUID id;
    private String name;
    private String description;
    private UUID projectId;
    private String projectName;
    private LocalDateTime createdAt;

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

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
