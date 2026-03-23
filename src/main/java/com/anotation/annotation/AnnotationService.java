package com.anotation.annotation;

import com.anotation.common.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Hợp đồng dịch vụ nghiệp vụ cho annotation: phân trang danh sách, tra cứu theo id, lọc theo task, tạo mới, cập nhật nội dung, xóa.
 * Triển khai chính: {@link AnnotationServiceImpl} (giao dịch, kiểm tra quyền annotator, trạng thái data item và task).
 * Đầu ra danh sách dùng {@link com.anotation.common.PageResponse}{@code <}{@link AnnotationResponse}{@code >} tương thích với controller.
 * Luồng nộp ({@code submit}) đồng bộ trạng thái {@link com.anotation.dataitem.DataItemStatus} và {@link com.anotation.task.TaskStatus} khi hợp lệ.
 * Cập nhật nội dung ({@code updateContent}) chỉ khi REJECTED và xóa feedback cũ để reviewer xem lại.
 */
public interface AnnotationService {
    PageResponse<AnnotationResponse> getAll(Pageable pageable);

    AnnotationResponse getById(UUID id);

    PageResponse<AnnotationResponse> getByTask(UUID taskId, Pageable pageable);

    AnnotationResponse submit(AnnotationRequest request);

    AnnotationResponse updateContent(UUID id, String content);

    void delete(UUID id);
}
