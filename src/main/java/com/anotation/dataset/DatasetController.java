package com.anotation.dataset;

import com.anotation.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/datasets")
@Tag(name = "Dataset", description = "Dataset management APIs")
public class DatasetController {

    private final DatasetService datasetService;

    public DatasetController(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    @GetMapping
    @Operation(summary = "Get all datasets")
    public ResponseEntity<PageResponse<DatasetResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(datasetService.getAll(pageable)); // 200
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get dataset by ID")
    public ResponseEntity<DatasetResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(datasetService.getById(id)); // 200
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "Get all datasets in a project")
    public ResponseEntity<PageResponse<DatasetResponse>> getByProject(
            @PathVariable UUID projectId,
            Pageable pageable) {
        return ResponseEntity.ok(datasetService.getByProject(projectId, pageable)); // 200
    }

    @PostMapping
    @Operation(summary = "Create a new dataset")
    public ResponseEntity<DatasetResponse> create(@Valid @RequestBody DatasetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(datasetService.create(request)); // 201
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a dataset")
    public ResponseEntity<DatasetResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody DatasetRequest request) {
        return ResponseEntity.ok(datasetService.update(id, request)); // 200
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a dataset")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        datasetService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
