package com.anotation.task;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Map {@link Task} + danh sách id data item sang {@link TaskResponse}: làm phẳng project,
 * annotator, reviewer và tính cờ {@link TaskResponse#setOverdue(boolean)} khi quá hạn và
 * task chưa ở trạng thái kết thúc (COMPLETED/REVIEWED).
 */
@Component
public class TaskMapper {

    /**
     * @param task         entity task (quan hệ project/annotator/reviewer đã load)
     * @param dataItemIds  danh sách id data item gán trong task (từ task items)
     * @return DTO đầy đủ cho API
     */
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
