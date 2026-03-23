package com.anotation.dataitem;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO JSON trả về cho hầu hết endpoint data item (danh sách, chi tiết, sau create/upload/bulk/patch).
 * {@code id}: khóa; {@code datasetId}, {@code datasetName}: ngữ cảnh dataset; {@code contentUrl}, {@code metadata}: dữ liệu hiển thị/gán nhãn.
 * {@code status}: {@link DataItemStatus}; {@code createdAt}: thời điểm tạo bản ghi.
 * Được điền bởi {@link DataItemMapper#toResponse(DataItem)}; là cơ sở trước khi bổ sung {@code label} trong {@link DataItemExportResponse}.
 */
public class DataItemResponse {

    private UUID id;
    private UUID datasetId;
    private String datasetName;
    private String contentUrl;
    private String metadata;
    private DataItemStatus status;
    private LocalDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(UUID datasetId) {
        this.datasetId = datasetId;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
