package com.anotation.task;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class TaskMapper {

    public TaskResponse toResponse(Task task, List<UUID> dataItemIds) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setStatus(task.getStatus());
        response.setCreatedAt(task.getCreatedAt());

        // Deadline fields
        response.setDueDate(task.getDueDate());
        boolean isOverdue = task.getDueDate() != null
                && LocalDateTime.now().isAfter(task.getDueDate())
                && task.getStatus() != TaskStatus.COMPLETED
                && task.getStatus() != TaskStatus.REVIEWED;
        response.setOverdue(isOverdue);

        // Flatten Project
        response.setProjectId(task.getProject().getId());
        response.setProjectName(task.getProject().getName());

        // Flatten Annotator
        response.setAnnotatorId(task.getAnnotator().getId());
        response.setAnnotatorUsername(task.getAnnotator().getUsername());

        // Flatten Reviewer
        response.setReviewerId(task.getReviewer().getId());
        response.setReviewerUsername(task.getReviewer().getUsername());

        // DataItem IDs from TaskItems
        response.setDataItemIds(dataItemIds);

        return response;
    }
}
