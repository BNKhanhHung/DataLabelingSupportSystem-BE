package com.anotation.dataitem;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO JSON cho {@code GET /api/data-items/export} — xuất data item của một project đã có gán nhãn, kèm cột nhãn để tải CSV/JSON.
 * {@code id}, {@code datasetId}, {@code datasetName}, {@code contentUrl}, {@code metadata}, {@code status}, {@code createdAt}: mirror thông tin {@link DataItemResponse} cơ bản.
 * {@code label}: nội dung nhãn lấy từ annotation (chuỗi {@code content} trên bản ghi annotation liên kết data item), có thể rỗng nếu chưa map được.
 * Được lắp ghép trong {@link DataItemServiceImpl#getLabeledByProjectForExport} cùng truy vấn {@link com.anotation.annotation.AnnotationRepository#findContentByDataItemIdIn}.
 */
public class DataItemExportResponse {

    private UUID id;
    private UUID datasetId;
    private String datasetName;
    private String contentUrl;
    private String metadata;
    private DataItemStatus status;
    private LocalDateTime createdAt;
    /** Nội dung nhãn (từ annotation) đã gắn cho data item. */
    private String label;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getDatasetId() { return datasetId; }
    public void setDatasetId(UUID datasetId) { this.datasetId = datasetId; }
    public String getDatasetName() { return datasetName; }
    public void setDatasetName(String datasetName) { this.datasetName = datasetName; }
    public String getContentUrl() { return contentUrl; }
    public void setContentUrl(String contentUrl) { this.contentUrl = contentUrl; }
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    public DataItemStatus getStatus() { return status; }
    public void setStatus(DataItemStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}
