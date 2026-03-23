package com.anotation.exception;

import java.time.LocalDateTime;

/**
 * Cấu trúc JSON thống nhất cho lỗi API trả về từ {@link GlobalExceptionHandler}.
 * <p>
 * Gồm mã HTTP ({@code status}), nhóm lỗi ngắn ({@code error}), mô tả chi tiết ({@code message}) và
 * {@code timestamp} thời điểm sinh phản hồi (giúp client log và hỗ trợ).
 * </p>
 */
public class ErrorResponse {

    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
