package com.anotation.task;

public enum TaskStatus {
    OPEN, // Mới tạo, chưa có item nào được gán nhãn
    IN_PROGRESS, // Annotator đang gán nhãn (ít nhất 1 DataItem đã ANNOTATED)
    OVERDUE, // Quá hạn: task đã trễ deadline nhưng chưa nộp/hoàn tất
    SUBMITTED, // Annotator đã nộp → chờ Reviewer kiểm duyệt
    REVIEWED, // Reviewer đã duyệt xong → Manager xem kết quả
    DENIED, // Reviewer từ chối ít nhất một nhãn → Annotator cần sửa lại
    COMPLETED // Manager xác nhận hoàn tất (hoặc tất cả đều APPROVED)
}
