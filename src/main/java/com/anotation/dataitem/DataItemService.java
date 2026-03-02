package com.anotation.dataitem;

import com.anotation.common.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface DataItemService {
    PageResponse<DataItemResponse> getAll(Pageable pageable);

    DataItemResponse getById(UUID id);

    PageResponse<DataItemResponse> getByDataset(UUID datasetId, Pageable pageable);

    PageResponse<DataItemResponse> getByDatasetAndStatus(
            UUID datasetId, DataItemStatus status, Pageable pageable);

    DataItemResponse upload(UUID datasetId, MultipartFile file, String metadata);

    DataItemResponse create(DataItemRequest request);

    DataItemResponse updateStatus(UUID id, DataItemStatus status);

    void delete(UUID id);
}
