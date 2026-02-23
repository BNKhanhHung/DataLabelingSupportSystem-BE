package com.anotation.dataitem;

import java.util.List;
import java.util.UUID;

public interface DataItemService {
    List<DataItemResponse> getAll();

    DataItemResponse getById(UUID id);

    List<DataItemResponse> getByDataset(UUID datasetId);

    List<DataItemResponse> getByDatasetAndStatus(UUID datasetId, DataItemStatus status);

    DataItemResponse create(DataItemRequest request);

    DataItemResponse updateStatus(UUID id, DataItemStatus status);

    void delete(UUID id);
}
