package com.anotation.dataitem;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

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
