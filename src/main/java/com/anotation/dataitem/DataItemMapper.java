package com.anotation.dataitem;

import org.springframework.stereotype.Component;

/**
 * Chuyển đổi {@link DataItem} sang {@link DataItemResponse} cho các API đọc và sau khi tạo/cập nhật.
 * Sao chép trường trực tiếp: id, contentUrl, metadata, status, createdAt; làm phẳng dataset thành {@code datasetId} và {@code datasetName}.
 * Dùng trong {@link DataItemServiceImpl} cho phân trang, upload, bulk, export (export tái sử dụng response rồi mở rộng thêm label).
 * Giữ component đơn giản, không chứa logic nghiệp vụ hay truy vấn lazy ngoài entity đã nạp.
 */
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
