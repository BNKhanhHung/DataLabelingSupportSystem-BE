package com.anotation.exception;

/**
 * Ngoại lệ báo xung đột dữ liệu trùng lặp (HTTP 409 Conflict).
 * <p>
 * Thường dùng khi vi phạm ràng buộc duy nhất theo nghiệp vụ (ví dụ tên dataset/label trùng trong cùng project);
 * {@link GlobalExceptionHandler} trả về thông điệp từ {@code getMessage()}.
 * </p>
 */
public class DuplicateException extends RuntimeException {
    public DuplicateException(String message) {
        super(message);
    }
}
