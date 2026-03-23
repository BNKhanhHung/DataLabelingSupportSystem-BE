package com.anotation.label;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Body request tạo/sửa label: {@code name} bắt buộc; {@code description} và {@code color} tùy chọn;
 * {@code projectId} bắt buộc khi tạo để gắn nhãn vào project (chi tiết ràng buộc cập nhật project do service quyết định).
 */
public class LabelRequest {

    @NotBlank(message = "Label name is required")
    private String name;

    private String description;

    private String color;

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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }
}
