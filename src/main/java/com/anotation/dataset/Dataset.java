package com.anotation.dataset;

import com.anotation.project.Project;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Thực thể JPA đại diện một <strong>dataset</strong> (tập dữ liệu) thuộc một dự án.
 * <p>
 * Dataset là đơn vị nhóm các data item để gán nhãn; tên dataset phải là duy nhất trong phạm vi cùng một project
 * (ràng buộc {@code uq_dataset_name_project} trên cặp {@code project_id} + {@code name}).
 * Mỗi bản ghi có {@code id} UUID do Hibernate sinh, thời điểm tạo {@code createdAt} được gán tự động khi persist.
 * Liên kết bắt buộc tới {@link com.anotation.project.Project} qua khóa ngoại {@code project_id}.
 * </p>
 *
 * @see com.anotation.dataset.DatasetRepository
 */
@Entity
@Table(name = "datasets", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "uq_dataset_name_project", columnNames = { "project_id", "name" })
})
public class Dataset {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
