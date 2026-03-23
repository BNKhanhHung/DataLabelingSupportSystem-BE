package com.anotation.annotation;

import org.springframework.stereotype.Component;

/**
 * Bean Spring chuyển đổi thực thể {@link Annotation} sang DTO {@link AnnotationResponse} cho lớp API và tầng dịch vụ.
 * Làm phẳng thông tin {@code TaskItem} và {@code com.anotation.dataitem.DataItem} (id task item, id data item, URL nội dung) để client không cần nested object.
 * Làm phẳng người gán nhãn: {@code annotatorId}, {@code annotatorUsername} lấy từ {@link com.anotation.user.User}.
 * Giữ nguyên các trường trực tiếp trên annotation: id, content, status, createdAt, updatedAt.
 * Được {@link AnnotationServiceImpl} và các luồng đọc annotation sử dụng sau khi truy vấn repository.
 */
@Component
public class AnnotationMapper {

    public AnnotationResponse toResponse(Annotation annotation) {
        AnnotationResponse response = new AnnotationResponse();
        response.setId(annotation.getId());
        response.setContent(annotation.getContent());
        response.setStatus(annotation.getStatus());
        response.setCreatedAt(annotation.getCreatedAt());
        response.setUpdatedAt(annotation.getUpdatedAt());

        // Flatten TaskItem & DataItem
        response.setTaskItemId(annotation.getTaskItem().getId());
        response.setDataItemId(annotation.getTaskItem().getDataItem().getId());
        response.setContentUrl(annotation.getTaskItem().getDataItem().getContentUrl());

        // Flatten Annotator
        response.setAnnotatorId(annotation.getAnnotator().getId());
        response.setAnnotatorUsername(annotation.getAnnotator().getUsername());

        return response;
    }
}
