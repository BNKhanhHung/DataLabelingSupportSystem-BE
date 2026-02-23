package com.anotation.label;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/labels")
@Tag(name = "Label", description = "Label management APIs")
public class LabelController {

    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @GetMapping
    @Operation(summary = "Get all labels")
    public ResponseEntity<List<LabelResponse>> getAll() {
        return ResponseEntity.ok(labelService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get label by ID")
    public ResponseEntity<LabelResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(labelService.getById(id));
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get all labels in a project")
    public ResponseEntity<List<LabelResponse>> getByProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(labelService.getByProject(projectId));
    }

    @PostMapping
    @Operation(summary = "Create a new label")
    public ResponseEntity<LabelResponse> create(@Valid @RequestBody LabelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(labelService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing label")
    public ResponseEntity<LabelResponse> update(@PathVariable UUID id,
            @Valid @RequestBody LabelRequest request) {
        return ResponseEntity.ok(labelService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a label")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        labelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
