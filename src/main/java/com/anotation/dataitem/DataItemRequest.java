package com.anotation.dataitem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * DTO JSON cho tạo một data item mới ({@code POST /api/data-items}).
 * {@code datasetId}: bắt buộc, trỏ tới dataset hợp lệ; {@code contentUrl}: URL hoặc đường dẫn nội dung, bắt buộc, tối đa 1000 ký tự.
 * {@code metadata}: tùy chọn, chuỗi mô tả bổ sung (JSON hoặc text) lưu cột {@code metadata}.
 * Service từ chối trùng {@code contentUrl} trong cùng dataset ({@link com.anotation.exception.DuplicateException}); trạng thái mặc định NEW do entity.
 */
public class DataItemRequest {

    @NotNull(message = "Dataset ID is required")
    private UUID datasetId;

    @NotBlank(message = "Content URL is required")
    @Size(max = 1000, message = "Content URL must not exceed 1000 characters")
    private String contentUrl;

    private String metadata;

    public UUID getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(UUID datasetId) {
        this.datasetId = datasetId;
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
}
