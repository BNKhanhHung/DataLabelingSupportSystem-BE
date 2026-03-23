package com.anotation.annotation;

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

/**
 * REST controller cho nghiệp vụ annotation: người dùng đã đăng nhập (theo {@link com.anotation.auth.SecurityConfig}) có thể đọc và thao tác annotation.
 * Tiền tố cơ sở: {@code /api/annotations}. Các endpoint chính: {@code GET /} (phân trang toàn bộ), {@code GET /{id}}, {@code GET /task/{taskId}} (theo task),
 * {@code POST /} (nộp nhãn cho một task item), {@code PATCH /{id}/content} (sửa nội dung khi trạng thái REJECTED), {@code DELETE /{id}}.
 * Phân quyền: USER được phép GET/POST/PATCH/DELETE trên nhánh này; ADMIN/MANAGER cũng có quyền qua rule chung cho {@code /api/annotations/**}.
 * Request/response dùng {@link AnnotationRequest}, {@link AnnotationResponse} và {@link com.anotation.common.PageResponse} cho danh sách phân trang.
 * Tài liệu OpenAPI: nhóm tag {@code Annotation}; một số GET hỗ trợ sort an toàn (vd. {@code id}, {@code status}, {@code createdAt}).
 */
@RestController
@RequestMapping("/api/annotations")
@Tag(name = "Annotation", description = "Annotation submission APIs")
public class AnnotationController {

    private final AnnotationService annotationService;

    public AnnotationController(AnnotationService annotationService) {
        this.annotationService = annotationService;
    }

    @GetMapping
    @Operation(summary = "Get all annotations",
            description = "Sort hợp lệ: id, status, createdAt (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<AnnotationResponse>> getAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(annotationService.getAll(pageable)); // 200
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get annotation by ID")
    public ResponseEntity<AnnotationResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(annotationService.getById(id)); // 200
    }

    @GetMapping("/task/{taskId}")
    @Operation(summary = "Get all annotations in a task",
            description = "Sort hợp lệ: id, createdAt (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<AnnotationResponse>> getByTask(
            @PathVariable UUID taskId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(annotationService.getByTask(taskId, pageable)); // 200
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
