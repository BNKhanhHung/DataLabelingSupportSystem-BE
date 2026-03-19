package com.anotation.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Tự động cập nhật trạng thái task sang OVERDUE khi quá hạn.
 * Áp dụng cho OPEN/IN_PROGRESS/DENIED và cả SUBMITTED (reviewer trễ review).
 */
@Component
public class OverdueTaskStatusScheduler {

    private final TaskService taskService;

    public OverdueTaskStatusScheduler(TaskService taskService) {
        this.taskService = taskService;
    }

    // Mỗi 15 phút
    @Scheduled(cron = "0 */15 * * * *")
    public void markOverdueTasksEvery15Minutes() {
        taskService.markOverdueTasks();
    }
}

