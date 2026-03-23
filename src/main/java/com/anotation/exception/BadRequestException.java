package com.anotation.exception;

/**
 * Ngoại lệ nghiệp vụ báo yêu cầu không hợp lệ (HTTP 400).
 * <p>
 * Dùng khi dữ liệu đúng định dạng nhưng vi phạm quy tắc nghiệp vụ; {@link GlobalExceptionHandler}
 * ánh xạ sang {@link ErrorResponse} với trạng thái {@code BAD_REQUEST}.
 * </p>
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
