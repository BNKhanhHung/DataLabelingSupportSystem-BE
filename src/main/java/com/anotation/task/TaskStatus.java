package com.anotation.task;

/**
 * Trạng thái vòng đời của một {@link Task} trong hệ thống gán nhãn dữ liệu.
 * <p>
 * Luồng điển hình: task được tạo/phân công → annotator làm việc → nộp bản review →
 * reviewer duyệt → manager xác nhận hoàn tất; có các nhánh quá hạn, từ chối nhãn và từ chối nhận việc.
 */
public enum TaskStatus {
    /**
     * Task mở, chưa có (hoặc chưa đủ) phân công hoặc đang chờ gán lại sau khi bị từ chối nhận.
     */
    OPEN,
    /**
     * Annotator đang thực hiện gán nhãn; task đã có người được giao và đang trong tiến độ xử lý.
     */
    IN_PROGRESS,
    /**
     * Đã quá {@code dueDate} nhưng chưa hoàn tất bước tương ứng (nộp hoặc review) theo quy tắc hệ thống.
     */
    OVERDUE,
    /**
     * Annotator đã nộp toàn bộ nhãn; task chờ reviewer kiểm duyệt.
     */
    SUBMITTED,
    /**
     * Reviewer đã hoàn tất review và tất cả nhãn được chấp nhận; chờ bước xác nhận phía quản lý nếu có.
     */
    REVIEWED,
    /**
     * Có ít nhất một nhãn bị reviewer từ chối; annotator cần chỉnh sửa và nộp lại.
     */
    DENIED,
    /**
     * Task đã kết thúc thành công (ví dụ manager xác nhận hoặc luồng nghiệp vụ coi là hoàn tất).
     */
    COMPLETED
}
