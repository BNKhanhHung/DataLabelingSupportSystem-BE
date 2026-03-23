package com.anotation.role;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import java.util.UUID;

/**
 * Entity JPA vai trò người dùng trong hệ thống (ví dụ Manager, Annotator, Reviewer).
 * <p>
 * Tên vai trò {@link #name} là duy nhất; có thể gán cho user qua bảng liên kết user-role
 * tùy thiết kế schema. Mô tả {@link #description} tùy chọn.
 */
@Entity
@Table(name = "roles", schema = "public")
public class Role {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;

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
}
