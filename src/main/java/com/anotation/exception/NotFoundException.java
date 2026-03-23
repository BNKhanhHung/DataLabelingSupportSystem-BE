package com.anotation.exception;

/**
 * Ngoại lệ báo tài nguyên không tồn tại hoặc không truy cập được trong ngữ cảnh hiện tại (HTTP 404).
 * <p>
 * Thông điệp từ constructor được đưa nguyên vào phần {@code message} của {@link ErrorResponse}.
 * Một số luồng còn dùng lớp này để che giấu chi tiết (ví dụ thông báo không thuộc user hiện tại).
 * </p>
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
