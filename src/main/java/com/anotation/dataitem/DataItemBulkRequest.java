package com.anotation.dataitem;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for creating multiple DataItems at once.
 * Manager can upload a batch of image URLs into a dataset in a single API call.
 */
public class DataItemBulkRequest {

    @NotNull(message = "Dataset ID is required")
    private UUID datasetId;

    @NotEmpty(message = "At least one content URL is required")
    private List<String> contentUrls;

    public UUID getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(UUID datasetId) {
        this.datasetId = datasetId;
    }

    public List<String> getContentUrls() {
        return contentUrls;
    }

    public void setContentUrls(List<String> contentUrls) {
        this.contentUrls = contentUrls;
    }
}
