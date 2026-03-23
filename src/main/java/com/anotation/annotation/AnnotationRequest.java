package com.anotation.annotation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * DTO JSON cho thân yêu cầu khi nộp annotation mới qua {@code POST /api/annotations} ({@link AnnotationController#submit}).
 * {@code taskItemId}: định danh mục công việc con cần gán nhãn; bắt buộc, phải tồn tại trong hệ thống.
 * {@code annotatorId}: id người gán nhãn; bắt buộc và phải khớp với người dùng đang xác thực và là annotator của task (kiểm tra trong {@link AnnotationServiceImpl}).
 * {@code content}: chuỗi nội dung nhãn (vd. tên lớp nhãn); bắt buộc, không rỗng.
 * Validation: Jakarta Bean Validation ({@code @NotNull}, {@code @NotBlank}) trả lỗi 400 khi thiếu trường.
 */
public class AnnotationRequest {

    @NotNull(message = "TaskItem ID is required")
    private UUID taskItemId;

    @NotNull(message = "Annotator ID is required")
    private UUID annotatorId;

    @NotBlank(message = "Content is required")
    private String content;

    public UUID getTaskItemId() {
        return taskItemId;
    }

    public void setTaskItemId(UUID taskItemId) {
        this.taskItemId = taskItemId;
    }

    public UUID getAnnotatorId() {
        return annotatorId;
    }

    public void setAnnotatorId(UUID annotatorId) {
        this.annotatorId = annotatorId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
