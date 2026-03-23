package com.anotation.dataitem;

import com.anotation.dataset.Dataset;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Thực thể JPA cho một phần tử dữ liệu (ảnh, file, URL) thuộc một {@link com.anotation.dataset.Dataset} trong dự án gán nhãn.
 * {@code contentUrl}: đường dẫn hoặc URL nội dung (tối đa 1000 ký tự); {@code metadata}: JSON/text tùy chọn mô tả thêm.
 * {@link DataItemStatus}: vòng đời từ NEW → ASSIGNED → ANNOTATED → REVIEWED; mặc định NEW khi {@code @PrePersist}.
 * Bảng {@code public.data_items}; quan hệ Many-to-One tới dataset; không có {@code updatedAt} trong schema hiện tại.
 * Đồng bộ với API {@code /api/data-items} và luồng task/annotation khi annotator nộp hoặc reviewer duyệt.
 */
@Entity
@Table(name = "data_items", schema = "public")
public class DataItem {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dataset_id", nullable = false)
    private Dataset dataset;

    @Column(name = "content_url", nullable = false, length = 1000)
    private String contentUrl;

    @Column(name = "metadata", columnDefinition = "TEXT", nullable = true)
    private String metadata;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private DataItemStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (status == null)
            status = DataItemStatus.NEW;
        createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public DataItemStatus getStatus() {
        return status;
    }

    public void setStatus(DataItemStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
