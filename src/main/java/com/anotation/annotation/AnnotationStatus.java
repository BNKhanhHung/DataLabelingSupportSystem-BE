package com.anotation.annotation;

/**
 * Trạng thái vòng đời của một {@link Annotation} trong quy trình review.
 * {@code SUBMITTED}: annotator đã nộp, chờ reviewer xử lý; là giá trị mặc định khi tạo mới hoặc sau khi sửa lại sau REJECTED.
 * {@code APPROVED}: reviewer chấp nhận nhãn; dùng trong đếm hoàn thành task và thống kê.
 * {@code REJECTED}: reviewer từ chối; annotator được phép cập nhật nội dung qua {@code PATCH /api/annotations/{id}/content}.
 * Lưu dạng chuỗi trong cột JPA ({@code @Enumerated(EnumType.STRING)}) để dễ đọc trên DB và API.
 */
public enum AnnotationStatus {
    SUBMITTED, // Annotator đã nộp, chờ reviewer duyệt
    APPROVED, // Reviewer chấp thuận
    REJECTED // Reviewer từ chối, Annotator cần sửa lại
}
