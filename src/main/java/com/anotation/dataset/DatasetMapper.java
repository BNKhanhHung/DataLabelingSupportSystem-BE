package com.anotation.dataset;

import org.springframework.stereotype.Component;

/**
 * Bộ ánh xạ (mapper) giữa thực thể {@link Dataset} và các DTO request/response.
 * <p>
 * {@link #toResponse(Dataset)} làm phẳng thông tin project (chỉ trả về {@code projectId}, {@code projectName})
 * để tránh lộ toàn bộ graph entity. {@link #updateEntity(DatasetRequest, Dataset)} cập nhật tên và mô tả;
 * project của dataset <strong>không</strong> đổi qua request (dataset luôn thuộc project ban đầu).
 * </p>
 */
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
