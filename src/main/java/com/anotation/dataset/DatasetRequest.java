package com.anotation.dataset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * DTO đầu vào cho thao tác tạo/cập nhật dataset (HTTP body JSON).
 * <p>
 * Ràng buộc Bean Validation: {@code name} bắt buộc, tối đa 150 ký tự; {@code description} tối đa 500 ký tự;
 * {@code projectId} bắt buộc để gắn dataset vào đúng project khi tạo (logic cập nhật project có thể bị giới hạn ở service).
 * </p>
 */
public class DatasetRequest {

    @NotBlank(message = "Dataset name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Project ID is required")
    private UUID projectId;

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
}
