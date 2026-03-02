package com.anotation.dataitem;

import com.anotation.common.PageResponse;
import com.anotation.exception.BadRequestException;
import com.anotation.exception.DuplicateException;
import com.anotation.exception.NotFoundException;
import com.anotation.dataset.Dataset;
import com.anotation.dataset.DatasetRepository;
import com.anotation.storage.SupabaseStorageService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@Transactional
public class DataItemServiceImpl implements DataItemService {

    private final DataItemRepository dataItemRepository;
    private final DatasetRepository datasetRepository;
    private final DataItemMapper dataItemMapper;
    private final SupabaseStorageService storageService;

    public DataItemServiceImpl(DataItemRepository dataItemRepository,
            DatasetRepository datasetRepository,
            DataItemMapper dataItemMapper,
            SupabaseStorageService storageService) {
        this.dataItemRepository = dataItemRepository;
        this.datasetRepository = datasetRepository;
        this.dataItemMapper = dataItemMapper;
        this.storageService = storageService;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DataItemResponse> getAll(Pageable pageable) {
        return PageResponse.from(dataItemRepository.findAll(pageable), dataItemMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public DataItemResponse getById(UUID id) {
        return dataItemMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DataItemResponse> getByDataset(UUID datasetId, Pageable pageable) {
        return PageResponse.from(dataItemRepository.findByDatasetId(datasetId, pageable),
                dataItemMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DataItemResponse> getByDatasetAndStatus(
            UUID datasetId, DataItemStatus status, Pageable pageable) {
        return PageResponse.from(
                dataItemRepository.findByDatasetIdAndStatus(datasetId, status, pageable),
                dataItemMapper::toResponse);
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
    public void delete(UUID id) {
        findOrThrow(id);
        dataItemRepository.deleteById(id);
    }

    // ── Private helpers ─────────────────────────────────────────────────────────

    private DataItem findOrThrow(UUID id) {
        return dataItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("DataItem not found with id: " + id));
    }
}
