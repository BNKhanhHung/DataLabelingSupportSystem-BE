package com.anotation.dataitem;

import org.springframework.stereotype.Component;

@Component
public class DataItemMapper {

    public DataItemResponse toResponse(DataItem item) {
        DataItemResponse response = new DataItemResponse();
        response.setId(item.getId());
        response.setContentUrl(item.getContentUrl());
        response.setMetadata(item.getMetadata());
        response.setStatus(item.getStatus());
        response.setCreatedAt(item.getCreatedAt());
        // Flatten dataset info
        response.setDatasetId(item.getDataset().getId());
        response.setDatasetName(item.getDataset().getName());
        return response;
    }
}
