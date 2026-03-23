package com.anotation.label;

import com.anotation.project.Project;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Thực thể JPA đại diện một <strong>nhãn (label)</strong> dùng trong gán nhãn dữ liệu, thuộc một project.
 * <p>
 * Tên nhãn là duy nhất trong phạm vi project ({@code uq_label_project_name}). Có thể kèm mô tả dạng TEXT và
 * {@code color} phục vụ hiển thị giao diện. {@code createdAt} và {@code updatedAt} được cập nhật qua lifecycle
 * {@code @PrePersist} / {@code @PreUpdate}.
 * </p>
 */
@Entity
@Table(name = "labels", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "uq_label_project_name", columnNames = { "project_id", "name" })
})
public class Label {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Color code for UI display (e.g. #FF5733)
    private String color;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
