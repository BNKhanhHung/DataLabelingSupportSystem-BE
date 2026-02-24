package com.anotation.dataset;

import java.util.List;
import java.util.UUID;

public interface DatasetService {
    List<DatasetResponse> getAll();

    DatasetResponse getById(UUID id);

    List<DatasetResponse> getByProject(UUID projectId);

    DatasetResponse create(DatasetRequest request);

    DatasetResponse update(UUID id, DatasetRequest request);

    void delete(UUID id);
}
