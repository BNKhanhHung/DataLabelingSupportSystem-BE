package com.anotation.label;

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
 * REST controller API quản lý <strong>label</strong> (nhãn phân loại) theo project, gốc {@code /api/labels}.
 * <p>
 * Nhãn được annotator/reviewer chọn khi làm việc trên từng data item. Hỗ trợ CRUD đầy đủ, lọc theo project và phân trang.
 * </p>
 * <ul>
 *   <li>{@code GET /api/labels} — danh sách (phân trang)</li>
 *   <li>{@code GET /api/labels/{id}} — theo id</li>
 *   <li>{@code GET /api/labels/project/{projectId}} — nhãn trong project</li>
 *   <li>{@code POST /api/labels} — tạo (201)</li>
 *   <li>{@code PUT /api/labels/{id}} — sửa</li>
 *   <li>{@code DELETE /api/labels/{id}} — xóa (204)</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/labels")
@Tag(name = "Label", description = "Label management APIs")
public class LabelController {

    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @GetMapping
    @Operation(summary = "Get all labels",
            description = "Sort hợp lệ: id, name (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<LabelResponse>> getAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(labelService.getAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get label by ID")
    public ResponseEntity<LabelResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(labelService.getById(id));
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get all labels in a project",
            description = "Sort hợp lệ: id, name (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<LabelResponse>> getByProject(
            @PathVariable UUID projectId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(labelService.getByProject(projectId, pageable));
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
