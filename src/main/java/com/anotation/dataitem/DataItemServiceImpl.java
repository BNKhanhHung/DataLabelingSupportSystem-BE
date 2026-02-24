package com.anotation.dataitem;

import com.anotation.exception.DuplicateException;
import com.anotation.exception.NotFoundException;
import com.anotation.dataset.Dataset;
import com.anotation.dataset.DatasetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DataItemServiceImpl implements DataItemService {

    private final DataItemRepository dataItemRepository;
    private final DatasetRepository datasetRepository;
    private final DataItemMapper dataItemMapper;

    public DataItemServiceImpl(DataItemRepository dataItemRepository,
            DatasetRepository datasetRepository,
            DataItemMapper dataItemMapper) {
        this.dataItemRepository = dataItemRepository;
        this.datasetRepository = datasetRepository;
        this.dataItemMapper = dataItemMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataItemResponse> getAll() {
        return dataItemRepository.findAll()
                .stream()
                .map(dataItemMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DataItemResponse getById(UUID id) {
        return dataItemMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataItemResponse> getByDataset(UUID datasetId) {
        return dataItemRepository.findByDatasetId(datasetId)
                .stream()
                .map(dataItemMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataItemResponse> getByDatasetAndStatus(UUID datasetId, DataItemStatus status) {
        return dataItemRepository.findByDatasetIdAndStatus(datasetId, status)
                .stream()
                .map(dataItemMapper::toResponse)
                .toList();
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
