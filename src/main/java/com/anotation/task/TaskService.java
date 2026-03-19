package com.anotation.task;

import com.anotation.common.PageResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TaskService {
    PageResponse<TaskResponse> getAll(Pageable pageable);

    TaskResponse getById(UUID id);

    /**
     * Items in this task (for annotator labeling: taskItemId, dataItemId,
     * contentUrl, hasAnnotation).
     */
    List<TaskItemResponse> getTaskItems(UUID taskId);

    PageResponse<TaskResponse> getByProject(UUID projectId, Pageable pageable);

    PageResponse<TaskResponse> getByAnnotator(UUID annotatorId, Pageable pageable);

    PageResponse<TaskResponse> getByReviewer(UUID reviewerId, Pageable pageable);

    PageResponse<TaskResponse> search(String name, TaskStatus status, Pageable pageable);

    TaskResponse create(TaskRequest request);

    TaskResponse updateStatus(UUID id, TaskStatus status);

    /** Update task deadline (due date). Pass null to clear. */
    TaskResponse updateDueDate(UUID id, LocalDateTime dueDate);

    /**
     * Annotator nộp task đã gán nhãn xong → chuyển sang SUBMITTED để Reviewer kiểm
     * duyệt.
     */
    TaskResponse submitForReview(UUID taskId);

    /**
     * Reviewer hoàn tất review → nếu tất cả APPROVED → REVIEWED; nếu có REJECTED →
     * trả về IN_PROGRESS.
     */
    TaskResponse completeReview(UUID taskId);

    /** Get all overdue tasks (dueDate passed and not completed). */
    PageResponse<TaskResponse> getOverdueTasks(Pageable pageable);

    /** Đánh dấu task quá hạn sang OVERDUE (OPEN/IN_PROGRESS/DENIED). */
    void markOverdueTasks();

    /** Get KPI metrics for a specific user (Annotator performance). */
    KpiResponse getAnnotatorKpi(UUID userId);

    /**
     * Annotator/Reviewer từ chối nhận Task.
     * Task chuyển về OPEN, gỡ assignee, bắn Notification cho Manager.
     */
    TaskResponse refuseTask(UUID taskId, String reason);

    void delete(UUID id);
}
