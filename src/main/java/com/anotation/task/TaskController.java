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
 * REST API quản lý {@link Task} tại {@code /api/tasks}.
 * <p>
 * Hỗ trợ: phân trang danh sách; chi tiết task; danh sách task item cho màn gán nhãn; lọc theo
 * project, annotator, reviewer (kèm biến thể “in-progress”); tìm theo tên project + trạng thái;
 * tạo task kèm gán {@link com.anotation.dataitem.DataItem}; cập nhật trạng thái, hạn, gán lại
 * người; annotator nộp bài; reviewer hoàn tất review; danh sách quá hạn; chạy tay đánh dấu quá hạn;
 * KPI theo user; annotator/reviewer từ chối task có lý do; xóa task (cascade phụ thuộc schema:
 * review feedbacks, annotations, task items).
 */
@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Task", description = "Task management APIs")
public class TaskController {

    private final TaskService taskService;

    /**
     * @param taskService tầng nghiệp vụ task
     */
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Danh sách task phân trang; sort hợp lệ: {@code id}, {@code status}, {@code createdAt}.
     *
     * @param pageable tham số phân trang
     * @return HTTP 200 và {@link PageResponse} {@link TaskResponse}
     */
    @GetMapping
    @Operation(summary = "Get all tasks", description = "Sort hợp lệ: id, status, createdAt (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<TaskResponse>> getAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(taskService.getAll(pageable)); // 200
    }

    /**
     * Chi tiết một task theo id.
     *
     * @param id UUID task
     * @return HTTP 200
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<TaskResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getById(id)); // 200
    }

    /**
     * Danh sách task item của một task (dùng cho UI annotator: id item, data item, URL, đã có annotation chưa).
     *
     * @param id UUID task
     * @return HTTP 200 và list {@link TaskItemResponse}
     */
    @GetMapping("/{id}/items")
    @Operation(summary = "Get task items (for annotator labeling: taskItemId, dataItemId, contentUrl, hasAnnotation)")
    public ResponseEntity<List<TaskItemResponse>> getTaskItems(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getTaskItems(id)); // 200
    }

    /**
     * Task thuộc một project, có phân trang.
     *
     * @param projectId UUID project
     * @param pageable  phân trang
     * @return HTTP 200
     */
    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get all tasks in a project", description = "Sort hợp lệ: id, status, createdAt (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<TaskResponse>> getByProject(
            @PathVariable UUID projectId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(taskService.getByProject(projectId, pageable)); // 200
    }

    /**
     * Task được giao cho một annotator (mọi trạng thái theo query mặc định của service).
     *
     * @param annotatorId UUID annotator
     * @param pageable    phân trang
     * @return HTTP 200
     */
    @GetMapping("/annotator/{annotatorId}")
    @Operation(summary = "Get all tasks assigned to an annotator", description = "Sort hợp lệ: id, status, createdAt (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<TaskResponse>> getByAnnotator(
            @PathVariable UUID annotatorId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(taskService.getByAnnotator(annotatorId, pageable)); // 200
    }

    /**
     * Lịch sử task annotator: các task đã nộp/được duyệt/đã hoàn tất (không còn trong danh sách “cần gán nhãn”).
     */
    @GetMapping("/annotator/{annotatorId}/history")
    @Operation(summary = "Annotator task history (SUBMITTED/REVIEWED/COMPLETED)")
    public ResponseEntity<PageResponse<TaskResponse>> getAnnotatorHistory(
            @PathVariable UUID annotatorId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(taskService.getAnnotatorHistory(annotatorId, pageable));
    }

    /**
     * Task đang IN_PROGRESS của annotator (đang làm việc).
     *
     * @param annotatorId UUID annotator
     * @param pageable    phân trang
     * @return HTTP 200
     */
    @GetMapping("/annotator/{annotatorId}/in-progress")
    @Operation(summary = "Task được giao (IN_PROGRESS) cho annotator")
    public ResponseEntity<PageResponse<TaskResponse>> getAssignedInProgressByAnnotator(
            @PathVariable UUID annotatorId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(taskService.getAssignedInProgressByAnnotator(annotatorId, pageable)); // 200
    }

    /**
     * Task chờ reviewer xử lý (thường SUBMITTED; chi tiết theo {@link TaskService}).
     *
     * @param reviewerId UUID reviewer
     * @param pageable   phân trang
     * @return HTTP 200
     */
    @GetMapping("/reviewer/{reviewerId}")
    @Operation(summary = "Task cần review (SUBMITTED)", description = "Chỉ task đã nộp (SUBMITTED), chờ reviewer. Sort: id, status, createdAt.")
    public ResponseEntity<PageResponse<TaskResponse>> getByReviewer(
            @PathVariable UUID reviewerId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(taskService.getByReviewer(reviewerId, pageable)); // 200
    }

    /**
     * Lịch sử task reviewer: các task đã review xong hoặc đã hoàn tất.
     */
    @GetMapping("/reviewer/{reviewerId}/history")
    @Operation(summary = "Reviewer task history (REVIEWED/COMPLETED)")
    public ResponseEntity<PageResponse<TaskResponse>> getReviewerHistory(
            @PathVariable UUID reviewerId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(taskService.getReviewerHistory(reviewerId, pageable));
    }

    /**
     * Task IN_PROGRESS gắn với reviewer.
     *
     * @param reviewerId UUID reviewer
     * @param pageable   phân trang
     * @return HTTP 200
     */
    @GetMapping("/reviewer/{reviewerId}/in-progress")
    @Operation(summary = "Task được giao (IN_PROGRESS) cho reviewer")
    public ResponseEntity<PageResponse<TaskResponse>> getAssignedInProgressByReviewer(
            @PathVariable UUID reviewerId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(taskService.getAssignedInProgressByReviewer(reviewerId, pageable)); // 200
    }

    /**
     * Tìm task theo tên project (tùy chọn) và trạng thái (tùy chọn).
     *
     * @param name     tên project (LIKE)
     * @param status   trạng thái task
     * @param pageable phân trang
     * @return HTTP 200
     */
    @GetMapping("/search")
    @Operation(summary = "Search tasks by project name and status", description = "Sort hợp lệ: id, status, createdAt (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<TaskResponse>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) TaskStatus status,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(taskService.search(name, status, pageable)); // 200
    }

    /**
     * Tạo task mới và gán các data item qua task items.
     *
     * @param request body {@link TaskRequest}
     * @return HTTP 201 và {@link TaskResponse}
     */
    @PostMapping
    @Operation(summary = "Create a new task with DataItem assignment")
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.create(request)); // 201
    }

    /**
     * Cập nhật trạng thái task (Manager/Admin).
     *
     * @param id     UUID task
     * @param status trạng thái mới
     * @return HTTP 200
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update task status (Manager/Admin)")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam TaskStatus status) {
        return ResponseEntity.ok(taskService.updateStatus(id, status)); // 200
    }

    /**
     * Cập nhật hoặc xóa hạn task ({@code dueDate: null} để bỏ hạn).
     *
     * @param id      UUID task
     * @param request body chứa {@code dueDate} (có thể null)
     * @return HTTP 200
     */
    @PatchMapping("/{id}/due-date")
    @Operation(summary = "Update task deadline (due date). Send dueDate: null to clear.")
    public ResponseEntity<TaskResponse> updateDueDate(
            @PathVariable UUID id,
            @RequestBody UpdateTaskDueDateRequest request) {
        return ResponseEntity.ok(taskService.updateDueDate(id, request != null ? request.getDueDate() : null)); // 200
    }

    /**
     * Gán lại annotator và reviewer cho task.
     *
     * @param id      UUID task
     * @param request {@link TaskAssignRequest}
     * @return HTTP 200
     */
    @PatchMapping("/{id}/assign")
    @Operation(summary = "Re-assign annotator & reviewer (Manager/Admin)")
    public ResponseEntity<TaskResponse> assign(
            @PathVariable UUID id,
            @Valid @RequestBody TaskAssignRequest request) {
        return ResponseEntity.ok(taskService.assign(id, request.getAnnotatorId(), request.getReviewerId())); // 200
    }

    /**
     * Annotator nộp task để reviewer duyệt (chuyển sang SUBMITTED khi đủ điều kiện nghiệp vụ).
     *
     * @param id UUID task
     * @return HTTP 200
     */
    @PatchMapping("/{id}/submit")
    @Operation(summary = "Annotator submits task for review", description = "Chuyển task từ IN_PROGRESS → SUBMITTED. Yêu cầu tất cả items đã được gán nhãn.")
    public ResponseEntity<TaskResponse> submitForReview(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.submitForReview(id)); // 200
    }

    /**
     * Reviewer kết thúc vòng review: nếu mọi annotation được duyệt → trạng thái gửi manager (REVIEWED);
     * nếu còn từ chối → trả annotator (IN_PROGRESS).
     *
     * @param id UUID task
     * @return HTTP 200
     */
    @PatchMapping("/{id}/complete-review")
    @Operation(summary = "Reviewer completes review", description = "Nếu tất cả annotations APPROVED → REVIEWED (gửi Manager). Nếu có REJECTED → IN_PROGRESS (trả Annotator).")
    public ResponseEntity<TaskResponse> completeReview(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.completeReview(id)); // 200
    }

    /**
     * Danh sách task quá hạn (đã qua due date, chưa COMPLETED/REVIEWED).
     *
     * @param pageable phân trang
     * @return HTTP 200
     */
    @GetMapping("/overdue")
    @Operation(summary = "Get all overdue tasks", description = "Tasks past their due date that are not yet COMPLETED or REVIEWED.")
    public ResponseEntity<PageResponse<TaskResponse>> getOverdueTasks(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(taskService.getOverdueTasks(pageable)); // 200
    }

    /**
     * Gọi ngay logic đánh dấu task quá hạn (tương tự scheduler), trả 204.
     *
     * @return HTTP 204
     */
    @PostMapping("/mark-overdue")
    @Operation(summary = "Mark overdue tasks", description = "Chuyển các task quá hạn (OPEN/IN_PROGRESS/DENIED) sang OVERDUE. Dùng khi muốn chạy ngay thay vì chờ scheduler.")
    public ResponseEntity<Void> markOverdueNow() {
        taskService.markOverdueTasks();
        return ResponseEntity.noContent().build(); // 204
    }

    /**
     * KPI hiệu suất cho một user (task, annotation, quá hạn, tỷ lệ duyệt).
     *
     * @param userId UUID user
     * @return HTTP 200 và {@link KpiResponse}
     */
    @GetMapping("/kpi/{userId}")
    @Operation(summary = "Get KPI metrics for a user", description = "Returns task count, annotation accuracy, overdue count for performance evaluation.")
    public ResponseEntity<KpiResponse> getAnnotatorKpi(@PathVariable UUID userId) {
        return ResponseEntity.ok(taskService.getAnnotatorKpi(userId)); // 200
    }

    /**
     * Annotator hoặc reviewer từ chối task kèm lý do; điều kiện trạng thái theo mô tả OpenAPI.
     *
     * @param id      UUID task
     * @param request {@link TaskRefuseRequest}
     * @return HTTP 200
     */
    @PatchMapping("/{id}/refuse")
    @Operation(summary = "Refuse (decline) an assigned task",
            description = "Annotator/Reviewer từ chối task được giao. Task chuyển về OPEN, Manager nhận thông báo kèm lý do. Chỉ cho phép khi task ở trạng thái OPEN/IN_PROGRESS/OVERDUE.")
    public ResponseEntity<TaskResponse> refuseTask(
            @PathVariable UUID id,
            @Valid @RequestBody TaskRefuseRequest request) {
        return ResponseEntity.ok(taskService.refuseTask(id, request.getReason())); // 200
    }

    /**
     * Xóa task theo id.
     *
     * @param id UUID task
     * @return HTTP 204
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
