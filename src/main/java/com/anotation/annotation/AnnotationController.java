package com.anotation.annotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/annotations")
@Tag(name = "Annotation", description = "Annotation submission APIs")
public class AnnotationController {

    private final AnnotationService annotationService;

    public AnnotationController(AnnotationService annotationService) {
        this.annotationService = annotationService;
    }

    @GetMapping
    @Operation(summary = "Get all annotations")
    public ResponseEntity<List<AnnotationResponse>> getAll() {
        return ResponseEntity.ok(annotationService.getAll()); // 200
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get annotation by ID")
    public ResponseEntity<AnnotationResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(annotationService.getById(id)); // 200
    }

    @GetMapping("/task/{taskId}")
    @Operation(summary = "Get all annotations in a task")
    public ResponseEntity<List<AnnotationResponse>> getByTask(@PathVariable UUID taskId) {
        return ResponseEntity.ok(annotationService.getByTask(taskId)); // 200
    }

    @PostMapping
    @Operation(summary = "Submit an annotation for a TaskItem")
    public ResponseEntity<AnnotationResponse> submit(@Valid @RequestBody AnnotationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(annotationService.submit(request)); // 201
    }

    @PatchMapping("/{id}/content")
    @Operation(summary = "Update annotation content (only when REJECTED)")
    public ResponseEntity<AnnotationResponse> updateContent(
            @PathVariable UUID id,
            @RequestParam String content) {
        return ResponseEntity.ok(annotationService.updateContent(id, content)); // 200
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an annotation")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        annotationService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
