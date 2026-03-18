package com.anotation.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Tự động cập nhật trạng thái task sang OVERDUE khi quá hạn.
 * Chỉ áp dụng cho các task chưa nộp: OPEN/IN_PROGRESS/DENIED.
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

