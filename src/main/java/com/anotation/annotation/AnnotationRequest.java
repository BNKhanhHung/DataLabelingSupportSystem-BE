package com.anotation.annotation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

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
