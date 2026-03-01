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

import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Task", description = "Task management APIs")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "Get all tasks",
            description = "Sort hợp lệ: id, status, createdAt (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<TaskResponse>> getAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(taskService.getAll(pageable)); // 200
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<TaskResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getById(id)); // 200
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get all tasks in a project",
            description = "Sort hợp lệ: id, status, createdAt (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<TaskResponse>> getByProject(
            @PathVariable UUID projectId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(taskService.getByProject(projectId, pageable)); // 200
    }

    @GetMapping("/annotator/{annotatorId}")
    @Operation(summary = "Get all tasks assigned to an annotator",
            description = "Sort hợp lệ: id, status, createdAt (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<TaskResponse>> getByAnnotator(
            @PathVariable UUID annotatorId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(taskService.getByAnnotator(annotatorId, pageable)); // 200
    }

    @GetMapping("/search")
    @Operation(summary = "Search tasks by project name and status",
            description = "Sort hợp lệ: id, status, createdAt (vd: sort=id,asc). Tránh sort=string.")
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
    @Operation(summary = "Update task status")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam TaskStatus status) {
        return ResponseEntity.ok(taskService.updateStatus(id, status)); // 200
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
