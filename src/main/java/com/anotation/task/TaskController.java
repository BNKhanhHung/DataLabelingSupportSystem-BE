package com.anotation.task;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    @Operation(summary = "Get all tasks")
    public ResponseEntity<List<TaskResponse>> getAll() {
        return ResponseEntity.ok(taskService.getAll()); // 200
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<TaskResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getById(id)); // 200
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get all tasks in a project")
    public ResponseEntity<List<TaskResponse>> getByProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(taskService.getByProject(projectId)); // 200
    }

    @GetMapping("/annotator/{annotatorId}")
    @Operation(summary = "Get all tasks assigned to an annotator")
    public ResponseEntity<List<TaskResponse>> getByAnnotator(@PathVariable UUID annotatorId) {
        return ResponseEntity.ok(taskService.getByAnnotator(annotatorId)); // 200
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
