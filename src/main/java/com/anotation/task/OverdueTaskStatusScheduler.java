package com.anotation.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Lịch Spring gọi định kỳ {@link TaskService#markOverdueTasks()} để gắn trạng thái
 * {@link TaskStatus#OVERDUE} cho các task đã quá {@code dueDate} nhưng vẫn trong nhóm
 * trạng thái được xem là “chưa kết thúc” (OPEN, IN_PROGRESS, DENIED, SUBMITTED — ví dụ
 * annotator đã nộp nhưng reviewer chưa xử lý kịp).
 */
@Component
public class OverdueTaskStatusScheduler {

    private final TaskService taskService;

    /**
     * @param taskService nghiệp vụ task (cập nhật quá hạn)
     */
    public OverdueTaskStatusScheduler(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Chạy mỗi 15 phút; biểu thức cron Spring gắn trực tiếp trên {@link Scheduled} ở dòng khai báo phương thức.
     */
    @Scheduled(cron = "0 */15 * * * *")
    public void markOverdueTasksEvery15Minutes() {
        taskService.markOverdueTasks();
    }
}

