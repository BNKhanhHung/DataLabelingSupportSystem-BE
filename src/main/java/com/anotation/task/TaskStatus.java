package com.anotation.task;

public enum TaskStatus {
    OPEN, // Mới tạo, chưa có item nào được gán nhãn
    IN_PROGRESS, // Ít nhất 1 DataItem đã ANNOTATED
    COMPLETED // Tất cả DataItem đã REVIEWED
}
