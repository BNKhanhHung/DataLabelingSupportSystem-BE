package com.anotation.dataitem;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * DTO JSON cho tạo hàng loạt data item qua {@code POST /api/data-items/bulk} ({@link DataItemController#bulkCreate}); yêu cầu quyền ADMIN hoặc MANAGER.
 * {@code datasetId}: dataset đích; bắt buộc, phải tồn tại; {@code contentUrls}: danh sách URL/chuỗi nội dung, ít nhất một phần tử ({@code @NotEmpty}).
 * {@link DataItemServiceImpl#bulkCreate}: bỏ qua URL rỗng và trùng {@code contentUrl} trong cùng dataset; nếu không tạo được mục nào mới thì báo lỗi 400.
 * Phù hợp nhập nhanh nhiều ảnh/liên kết từ giao diện quản lý thay vì gọi {@code POST /api/data-items} từng cái.
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
