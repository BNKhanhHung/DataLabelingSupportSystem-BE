package com.anotation.task;

import com.anotation.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Task API: CRUD task, lấy theo project/annotator/reviewer, cập nhật status/due-date, annotator nộp (submit), reviewer hoàn tất (complete-review), xóa task (cascade review_feedbacks → annotations → task_items).
 * GET /, /{id}, /{id}/items, /project/{id}, /annotator/{id}, /reviewer/{id}, /search, /overdue, /kpi/{userId}; POST /; PATCH /{id}/status, /{id}/due-date, /{id}/submit, /{id}/complete-review; DELETE /{id}.
 */
@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Task", description = "Task management APIs")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "Get all tasks", description = "Sort hợp lệ: id, status, createdAt (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<TaskResponse>> getAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(taskService.getAll(pageable)); // 200
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<TaskResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getById(id)); // 200
    }

    @GetMapping("/{id}/items")
    @Operation(summary = "Get task items (for annotator labeling: taskItemId, dataItemId, contentUrl, hasAnnotation)")
    public ResponseEntity<List<TaskItemResponse>> getTaskItems(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getTaskItems(id)); // 200
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get all tasks in a project", description = "Sort hợp lệ: id, status, createdAt (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<TaskResponse>> getByProject(
            @PathVariable UUID projectId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(taskService.getByProject(projectId, pageable)); // 200
    }

    @GetMapping("/annotator/{annotatorId}")
    @Operation(summary = "Get all tasks assigned to an annotator", description = "Sort hợp lệ: id, status, createdAt (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<TaskResponse>> getByAnnotator(
            @PathVariable UUID annotatorId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(taskService.getByAnnotator(annotatorId, pageable)); // 200
    }

    @GetMapping("/reviewer/{reviewerId}")
    @Operation(summary = "Get all tasks assigned to a reviewer", description = "Sort hợp lệ: id, status, createdAt (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<TaskResponse>> getByReviewer(
            @PathVariable UUID reviewerId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(taskService.getByReviewer(reviewerId, pageable)); // 200
    }

    @GetMapping("/search")
    @Operation(summary = "Search tasks by project name and status", description = "Sort hợp lệ: id, status, createdAt (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<TaskResponse>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) TaskStatus status,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(taskService.search(name, status, pageable)); // 200
    }

    @PostMapping
    @Operation(summary = "Create a new task with DataItem assignment")
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.create(request)); // 201
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update task status (Manager/Admin)")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam TaskStatus status) {
        return ResponseEntity.ok(taskService.updateStatus(id, status)); // 200
    }

    @PatchMapping("/{id}/due-date")
    @Operation(summary = "Update task deadline (due date). Send dueDate: null to clear.")
    public ResponseEntity<TaskResponse> updateDueDate(
            @PathVariable UUID id,
            @RequestBody UpdateTaskDueDateRequest request) {
        return ResponseEntity.ok(taskService.updateDueDate(id, request != null ? request.getDueDate() : null)); // 200
    }

    @PatchMapping("/{id}/submit")
    @Operation(summary = "Annotator submits task for review", description = "Chuyển task từ IN_PROGRESS → SUBMITTED. Yêu cầu tất cả items đã được gán nhãn.")
    public ResponseEntity<TaskResponse> submitForReview(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.submitForReview(id)); // 200
    }

    @PatchMapping("/{id}/complete-review")
    @Operation(summary = "Reviewer completes review", description = "Nếu tất cả annotations APPROVED → REVIEWED (gửi Manager). Nếu có REJECTED → IN_PROGRESS (trả Annotator).")
    public ResponseEntity<TaskResponse> completeReview(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.completeReview(id)); // 200
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get all overdue tasks", description = "Tasks past their due date that are not yet COMPLETED or REVIEWED.")
    public ResponseEntity<PageResponse<TaskResponse>> getOverdueTasks(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(taskService.getOverdueTasks(pageable)); // 200
    }

    @GetMapping("/kpi/{userId}")
    @Operation(summary = "Get KPI metrics for a user", description = "Returns task count, annotation accuracy, overdue count for performance evaluation.")
    public ResponseEntity<KpiResponse> getAnnotatorKpi(@PathVariable UUID userId) {
        return ResponseEntity.ok(taskService.getAnnotatorKpi(userId)); // 200
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
