package com.anotation.dataitem;

import com.anotation.common.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Hợp đồng dịch vụ data item: đọc phân trang, theo dataset và status, tạo từ request, upload file, đổi status, bulk URL, xuất đã gán nhãn, xóa.
 * Triển khai: {@link DataItemServiceImpl} (lưu trữ Supabase qua {@link com.anotation.storage.SupabaseStorageService} khi upload).
 * {@code getLabeledByProject} / {@code getLabeledByProjectForExport} phục vụ báo cáo và {@code GET /export} với nhãn từ annotation.
 * Kiểu trả về danh sách phân trang dùng {@link com.anotation.common.PageResponse}{@code <}{@link DataItemResponse}{@code >}.
 */
public interface DataItemService {
    PageResponse<DataItemResponse> getAll(Pageable pageable);

    DataItemResponse getById(UUID id);

    PageResponse<DataItemResponse> getByDataset(UUID datasetId, Pageable pageable);

    PageResponse<DataItemResponse> getByDatasetAndStatus(
            UUID datasetId, DataItemStatus status, Pageable pageable);

    DataItemResponse upload(UUID datasetId, MultipartFile file, String metadata);

    DataItemResponse create(DataItemRequest request);

    DataItemResponse updateStatus(UUID id, DataItemStatus status);

    /** Create multiple DataItems at once from a list of URLs. */
    List<DataItemResponse> bulkCreate(DataItemBulkRequest request);

    /** Xuất data items đã gắn nhãn (ANNOTATED, REVIEWED) của một project. */
    List<DataItemResponse> getLabeledByProject(UUID projectId);

    /** Xuất data items đã gắn nhãn kèm cột nhãn (annotation content) cho export CSV/JSON. */
    List<DataItemExportResponse> getLabeledByProjectForExport(UUID projectId);

    void delete(UUID id);
}
