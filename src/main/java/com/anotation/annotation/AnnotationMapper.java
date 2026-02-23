package com.anotation.annotation;

import org.springframework.stereotype.Component;

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
