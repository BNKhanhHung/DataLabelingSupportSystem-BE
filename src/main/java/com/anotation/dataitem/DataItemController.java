package com.anotation.dataitem;

import com.anotation.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * DataItem API: CRUD data item, lấy theo dataset (và status), upload file, bulk tạo từ URL. Ảnh phục vụ qua /api/uploads/{folder}/{filename}.
 * GET /, /{id}, /dataset/{id}, /dataset/{id}/status/{status}; POST /, /upload (multipart), /bulk; PATCH /{id}/status; DELETE /{id}.
 */
@RestController
@RequestMapping("/api/data-items")
@Tag(name = "DataItem", description = "DataItem management APIs")
public class DataItemController {

    private final DataItemService dataItemService;

    public DataItemController(DataItemService dataItemService) {
        this.dataItemService = dataItemService;
    }

    @GetMapping
    @Operation(summary = "Get all data items", description = "Sort hợp lệ: id, status, createdAt, contentUrl (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<DataItemResponse>> getAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(dataItemService.getAll(pageable)); // 200
    }

    @GetMapping("/export")
    @Operation(summary = "Xuất data items đã gắn nhãn theo project", description = "Trả về data items kèm cột nhãn (label) cho export CSV/JSON.")
    public ResponseEntity<List<DataItemExportResponse>> getLabeledByProjectForExport(@RequestParam UUID projectId) {
        return ResponseEntity.ok(dataItemService.getLabeledByProjectForExport(projectId)); // 200
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get data item by ID")
    public ResponseEntity<DataItemResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(dataItemService.getById(id)); // 200
    }

    @GetMapping("/dataset/{datasetId}")
    @Operation(summary = "Get all items in a dataset", description = "Sort hợp lệ: id, status, createdAt (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<DataItemResponse>> getByDataset(
            @PathVariable UUID datasetId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(dataItemService.getByDataset(datasetId, pageable)); // 200
    }

    @GetMapping("/dataset/{datasetId}/status/{status}")
    @Operation(summary = "Get items in a dataset filtered by status", description = "Sort hợp lệ: id, createdAt (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<DataItemResponse>> getByDatasetAndStatus(
            @PathVariable UUID datasetId,
            @PathVariable DataItemStatus status,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(dataItemService.getByDatasetAndStatus(datasetId, status, pageable)); // 200
    }

    @PostMapping
    @Operation(summary = "Create a new data item")
    public ResponseEntity<DataItemResponse> create(@Valid @RequestBody DataItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dataItemService.create(request)); // 201
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a file and create a data item")
    public ResponseEntity<DataItemResponse> upload(
            @RequestParam UUID datasetId,
            @RequestParam MultipartFile file,
            @RequestParam(required = false) String metadata) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dataItemService.upload(datasetId, file, metadata)); // 201
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update status of a data item")
    public ResponseEntity<DataItemResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam DataItemStatus status) {
        return ResponseEntity.ok(dataItemService.updateStatus(id, status)); // 200
    }

    @PostMapping("/bulk")
    @Operation(summary = "Create multiple data items at once", description = "Provide a datasetId and a list of contentUrls. Duplicate URLs are skipped.")
    public ResponseEntity<List<DataItemResponse>> bulkCreate(@Valid @RequestBody DataItemBulkRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dataItemService.bulkCreate(request)); // 201
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a data item")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        dataItemService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
