package com.anotation.dataitem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/data-items")
@Tag(name = "DataItem", description = "DataItem management APIs")
public class DataItemController {

    private final DataItemService dataItemService;

    public DataItemController(DataItemService dataItemService) {
        this.dataItemService = dataItemService;
    }

    @GetMapping
    @Operation(summary = "Get all data items")
    public ResponseEntity<List<DataItemResponse>> getAll() {
        return ResponseEntity.ok(dataItemService.getAll()); // 200
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get data item by ID")
    public ResponseEntity<DataItemResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(dataItemService.getById(id)); // 200
    }

    @GetMapping("/dataset/{datasetId}")
    @Operation(summary = "Get all items in a dataset")
    public ResponseEntity<List<DataItemResponse>> getByDataset(@PathVariable UUID datasetId) {
        return ResponseEntity.ok(dataItemService.getByDataset(datasetId)); // 200
    }

    @GetMapping("/dataset/{datasetId}/status/{status}")
    @Operation(summary = "Get items in a dataset filtered by status")
    public ResponseEntity<List<DataItemResponse>> getByDatasetAndStatus(
            @PathVariable UUID datasetId,
            @PathVariable DataItemStatus status) {
        return ResponseEntity.ok(dataItemService.getByDatasetAndStatus(datasetId, status)); // 200
    }

    @PostMapping
    @Operation(summary = "Create a new data item")
    public ResponseEntity<DataItemResponse> create(@Valid @RequestBody DataItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dataItemService.create(request)); // 201
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update status of a data item")
    public ResponseEntity<DataItemResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam DataItemStatus status) {
        return ResponseEntity.ok(dataItemService.updateStatus(id, status)); // 200
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a data item")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        dataItemService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
