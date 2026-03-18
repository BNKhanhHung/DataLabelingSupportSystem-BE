package com.anotation.dataitem;

import com.anotation.annotation.AnnotationRepository;
import com.anotation.common.PageResponse;
import com.anotation.exception.BadRequestException;
import com.anotation.exception.DuplicateException;
import com.anotation.exception.NotFoundException;
import com.anotation.dataset.Dataset;
import com.anotation.dataset.DatasetRepository;
import com.anotation.storage.SupabaseStorageService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class DataItemServiceImpl implements DataItemService {

    private final DataItemRepository dataItemRepository;
    private final DatasetRepository datasetRepository;
    private final DataItemMapper dataItemMapper;
    private final SupabaseStorageService storageService;
    private final AnnotationRepository annotationRepository;

    public DataItemServiceImpl(DataItemRepository dataItemRepository,
            DatasetRepository datasetRepository,
            DataItemMapper dataItemMapper,
            SupabaseStorageService storageService,
            AnnotationRepository annotationRepository) {
        this.dataItemRepository = dataItemRepository;
        this.datasetRepository = datasetRepository;
        this.dataItemMapper = dataItemMapper;
        this.storageService = storageService;
        this.annotationRepository = annotationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DataItemResponse> getAll(Pageable pageable) {
        try {
            return PageResponse.from(dataItemRepository.findAll(pageable), dataItemMapper::toResponse);
        } catch (PropertyReferenceException e) {
            Pageable safe = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id"));
            return PageResponse.from(dataItemRepository.findAll(safe), dataItemMapper::toResponse);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DataItemResponse getById(UUID id) {
        return dataItemMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DataItemResponse> getByDataset(UUID datasetId, Pageable pageable) {
        try {
            return PageResponse.from(dataItemRepository.findByDatasetId(datasetId, pageable),
                    dataItemMapper::toResponse);
        } catch (PropertyReferenceException e) {
            Pageable safe = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id"));
            return PageResponse.from(dataItemRepository.findByDatasetId(datasetId, safe),
                    dataItemMapper::toResponse);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DataItemResponse> getByDatasetAndStatus(
            UUID datasetId, DataItemStatus status, Pageable pageable) {
        try {
            return PageResponse.from(
                    dataItemRepository.findByDatasetIdAndStatus(datasetId, status, pageable),
                    dataItemMapper::toResponse);
        } catch (PropertyReferenceException e) {
            Pageable safe = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("id"));
            return PageResponse.from(
                    dataItemRepository.findByDatasetIdAndStatus(datasetId, status, safe),
                    dataItemMapper::toResponse);
        }
    }

    @Override
    public DataItemResponse upload(UUID datasetId, MultipartFile file, String metadata) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required.");
        }

        Dataset dataset = datasetRepository.findById(datasetId)
                .orElseThrow(() -> new NotFoundException("Dataset not found: " + datasetId));

        String contentUrl = storageService.upload(file, "data-items");

        if (dataItemRepository.existsByContentUrlAndDatasetId(contentUrl, datasetId)) {
            throw new DuplicateException("Data item already exists for this dataset.");
        }

        DataItem dataItem = new DataItem();
        dataItem.setDataset(dataset);
        dataItem.setContentUrl(contentUrl);
        dataItem.setMetadata(metadata);

        return dataItemMapper.toResponse(dataItemRepository.save(dataItem));
    }

    @Override
    public DataItemResponse create(DataItemRequest request) {
        // 1. Validate dataset exists
        Dataset dataset = datasetRepository.findById(request.getDatasetId())
                .orElseThrow(() -> new NotFoundException(
                        "Dataset not found: " + request.getDatasetId()));

        // 2. Prevent duplicate contentUrl within same dataset
        if (dataItemRepository.existsByContentUrlAndDatasetId(
                request.getContentUrl(), request.getDatasetId())) {
            throw new DuplicateException(
                    "Content URL already exists in this dataset: " + request.getContentUrl());
        }

        DataItem item = new DataItem();
        item.setDataset(dataset);
        item.setContentUrl(request.getContentUrl());
        item.setMetadata(request.getMetadata());
        // status defaults to NEW via @PrePersist

        return dataItemMapper.toResponse(dataItemRepository.save(item));
    }

    @Override
    public DataItemResponse updateStatus(UUID id, DataItemStatus status) {
        DataItem item = findOrThrow(id);
        item.setStatus(status);
        return dataItemMapper.toResponse(dataItemRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataItemResponse> getLabeledByProject(UUID projectId) {
        var seen = new java.util.HashSet<UUID>();
        var merged = new ArrayList<DataItem>();
        // 1) Theo status ANNOTATED (query riêng, tránh lỗi IN collection)
        for (DataItem d : dataItemRepository.findByDatasetProjectIdAndStatusAnnotated(projectId)) {
            if (seen.add(d.getId())) merged.add(d);
        }
        // 2) Theo status REVIEWED
        for (DataItem d : dataItemRepository.findByDatasetProjectIdAndStatusReviewed(projectId)) {
            if (seen.add(d.getId())) merged.add(d);
        }
        // 3) Có ít nhất một annotation trong project (phòng status chưa cập nhật đúng)
        for (DataItem d : dataItemRepository.findByProjectIdAndHasAnnotation(projectId)) {
            if (seen.add(d.getId())) merged.add(d);
        }
        merged.sort((a, b) -> (a.getCreatedAt() != null && b.getCreatedAt() != null)
                ? a.getCreatedAt().compareTo(b.getCreatedAt())
                : 0);
        return merged.stream().map(dataItemMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataItemExportResponse> getLabeledByProjectForExport(UUID projectId) {
        var seen = new java.util.HashSet<UUID>();
        var merged = new ArrayList<DataItem>();
        for (DataItem d : dataItemRepository.findByDatasetProjectIdAndStatusAnnotated(projectId)) {
            if (seen.add(d.getId())) merged.add(d);
        }
        for (DataItem d : dataItemRepository.findByDatasetProjectIdAndStatusReviewed(projectId)) {
            if (seen.add(d.getId())) merged.add(d);
        }
        for (DataItem d : dataItemRepository.findByProjectIdAndHasAnnotation(projectId)) {
            if (seen.add(d.getId())) merged.add(d);
        }
        merged.sort((a, b) -> (a.getCreatedAt() != null && b.getCreatedAt() != null)
                ? a.getCreatedAt().compareTo(b.getCreatedAt())
                : 0);

        if (merged.isEmpty()) return List.of();

        List<UUID> ids = merged.stream().map(DataItem::getId).toList();
        List<Object[]> rows = annotationRepository.findContentByDataItemIdIn(ids);
        Map<UUID, String> idToLabel = new LinkedHashMap<>();
        for (Object[] row : rows) {
            UUID dataItemId = (UUID) row[0];
            String content = row[1] != null ? row[1].toString() : "";
            idToLabel.putIfAbsent(dataItemId, content);
        }

        List<DataItemExportResponse> result = new ArrayList<>();
        for (DataItem d : merged) {
            DataItemResponse base = dataItemMapper.toResponse(d);
            DataItemExportResponse ex = new DataItemExportResponse();
            ex.setId(base.getId());
            ex.setDatasetId(base.getDatasetId());
            ex.setDatasetName(base.getDatasetName());
            ex.setContentUrl(base.getContentUrl());
            ex.setMetadata(base.getMetadata());
            ex.setStatus(base.getStatus());
            ex.setCreatedAt(base.getCreatedAt());
            ex.setLabel(idToLabel.getOrDefault(d.getId(), ""));
            result.add(ex);
        }
        return result;
    }

    @Override
    public void delete(UUID id) {
        findOrThrow(id);
        dataItemRepository.deleteById(id);
    }

    // ── Bulk create ──────────────────────────────────────────────────────────────

    @Override
    public List<DataItemResponse> bulkCreate(DataItemBulkRequest request) {
        Dataset dataset = datasetRepository.findById(request.getDatasetId())
                .orElseThrow(() -> new NotFoundException(
                        "Dataset not found: " + request.getDatasetId()));

        List<DataItemResponse> results = new ArrayList<>();
        for (String url : request.getContentUrls()) {
            if (url == null || url.isBlank()) {
                continue; // skip empty URLs
            }
            // Skip duplicates silently
            if (dataItemRepository.existsByContentUrlAndDatasetId(url, request.getDatasetId())) {
                continue;
            }

            DataItem item = new DataItem();
            item.setDataset(dataset);
            item.setContentUrl(url.trim());
            results.add(dataItemMapper.toResponse(dataItemRepository.save(item)));
        }

        if (results.isEmpty()) {
            throw new BadRequestException("No new data items were created. All URLs may be duplicates or empty.");
        }

        return results;
    }

    // ── Private helpers ─────────────────────────────────────────────────────────

    private DataItem findOrThrow(UUID id) {
        return dataItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("DataItem not found with id: " + id));
    }
}
