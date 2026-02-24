package com.anotation.dataset;

import org.springframework.stereotype.Component;

@Component
public class DatasetMapper {

    public DatasetResponse toResponse(Dataset dataset) {
        DatasetResponse response = new DatasetResponse();
        response.setId(dataset.getId());
        response.setName(dataset.getName());
        response.setDescription(dataset.getDescription());
        response.setCreatedAt(dataset.getCreatedAt());
        // Flatten project info — avoid exposing nested entity
        response.setProjectId(dataset.getProject().getId());
        response.setProjectName(dataset.getProject().getName());
        return response;
    }

    public void updateEntity(DatasetRequest request, Dataset dataset) {
        dataset.setName(request.getName());
        dataset.setDescription(request.getDescription());
        // Note: projectId is NOT updatable — dataset stays in its original project
    }
}
