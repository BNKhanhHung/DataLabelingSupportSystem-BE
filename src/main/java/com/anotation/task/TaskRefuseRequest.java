package com.anotation.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Body khi annotator hoặc reviewer từ chối nhận/giữ task đã được giao.
 * <p>
 * {@link #reason} bắt buộc, độ dài 10–500 ký tự; điều kiện trạng thái task cho phép từ chối
 * do {@link TaskService#refuseTask} xử lý.
 */
public class TaskRefuseRequest {

    @NotBlank(message = "Reason is required when refusing a task")
    @Size(min = 10, max = 500, message = "Reason must be between 10 and 500 characters")
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
