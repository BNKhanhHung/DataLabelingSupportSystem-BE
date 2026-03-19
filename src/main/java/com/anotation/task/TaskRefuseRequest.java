package com.anotation.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for refusing (declining) an assigned task.
 * Both Annotator and Reviewer can refuse a task with a mandatory reason.
 */
public class TaskRefuseRequest {

    @NotBlank(message = "Reason is required when refusing a task")
    @Size(min = 10, max = 500, message = "Reason must be between 10 and 500 characters")
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
