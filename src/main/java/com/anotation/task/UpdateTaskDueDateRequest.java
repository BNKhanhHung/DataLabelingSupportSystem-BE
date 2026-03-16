package com.anotation.task;

import java.time.LocalDateTime;

/**
 * Request body for PATCH /api/tasks/{id}/due-date.
 * dueDate can be null to clear the deadline.
 */
public class UpdateTaskDueDateRequest {

    private LocalDateTime dueDate;

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
}
