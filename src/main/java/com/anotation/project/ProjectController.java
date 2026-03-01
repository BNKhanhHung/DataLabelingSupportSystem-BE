package com.anotation.project;

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
@RequestMapping("/api/projects")
@Tag(name = "Project", description = "Project management APIs")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    @Operation(summary = "Get all projects",
            description = "Sort hợp lệ: id, name, description, createdAt (vd: sort=name,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<ProjectResponse>> getAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(projectService.getAll(pageable)); // 200
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID")
    public ResponseEntity<ProjectResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(projectService.getById(id)); // 200
    }

    @GetMapping("/search")
    @Operation(summary = "Search projects by name",
            description = "Sort hợp lệ: id, name, description, createdAt (vd: sort=name,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<ProjectResponse>> search(
            @RequestParam(required = false) String name,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(projectService.searchByName(name, pageable)); // 200
    }

    @PostMapping
    @Operation(summary = "Create a new project")
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.create(request)); // 201
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a project")
    public ResponseEntity<ProjectResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.update(id, request)); // 200
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a project")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
