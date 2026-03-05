package com.anotation.task;

public enum TaskStatus {
    OPEN, // Mới tạo, chưa có item nào được gán nhãn
    IN_PROGRESS, // Annotator đang gán nhãn (ít nhất 1 DataItem đã ANNOTATED)
    SUBMITTED, // Annotator đã nộp → chờ Reviewer kiểm duyệt
    REVIEWED, // Reviewer đã duyệt xong → Manager xem kết quả
    COMPLETED // Manager xác nhận hoàn tất (hoặc tất cả đều APPROVED)
}
