package com.anotation.label;

import org.springframework.stereotype.Component;

@Component
public class LabelMapper {

    public LabelResponse toResponse(Label label) {
        LabelResponse response = new LabelResponse();
        response.setId(label.getId());
        response.setName(label.getName());
        response.setDescription(label.getDescription());
        response.setColor(label.getColor());
        response.setProjectId(label.getProject().getId());
        response.setProjectName(label.getProject().getName());
        response.setCreatedAt(label.getCreatedAt());
        response.setUpdatedAt(label.getUpdatedAt());
        return response;
    }

    public void updateEntity(LabelRequest request, Label label) {
        label.setName(request.getName());
        label.setDescription(request.getDescription());
        label.setColor(request.getColor());
    }
}
