package com.anotation.task;

import java.time.LocalDateTime;

/**
 * Body request cho thao tác cập nhật hạn hoàn thành (deadline) của task, thường gắn với
 * {@code PATCH /api/tasks/{id}/due-date}.
 * <p>
 * Trường {@link #dueDate} có thể để {@code null} để xóa hạn (clear deadline) theo nghiệp vụ cho phép.
 */
public class UpdateTaskDueDateRequest {

    /** Thời điểm hạn mới; {@code null} nghĩa là gỡ bỏ deadline trên task. */
    private LocalDateTime dueDate;

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
}
