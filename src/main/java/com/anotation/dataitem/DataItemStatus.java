package com.anotation.dataitem;

/**
 * Trạng thái vòng đời của {@link DataItem} từ lúc nhập kho đến khi reviewer hoàn tất.
 * {@code NEW}: mới tạo, chưa giao việc; {@code ASSIGNED}: đã gán cho annotator trong task; {@code ANNOTATED}: annotator đã nộp nhãn; {@code REVIEWED}: reviewer đã duyệt xong.
 * Đồng bộ với luồng {@link com.anotation.annotation.AnnotationServiceImpl} (ASSIGNED khi giao, ANNOTATED khi nộp/sửa, …) và kiểm tra nghiệp vụ gán nhãn.
 * Lưu dạng chuỗi trong JPA; dùng trong filter API {@code GET .../status/{status}} và PATCH cập nhật trạng thái.
 */
public enum DataItemStatus {
    NEW, // Vừa được upload, chưa giao cho ai
    ASSIGNED, // Đã giao cho Annotator
    ANNOTATED, // Annotator đã hoàn thành gán nhãn
    REVIEWED // Reviewer đã duyệt xong
}
