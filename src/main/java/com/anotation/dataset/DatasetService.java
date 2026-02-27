package com.anotation.dataset;

import com.anotation.common.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DatasetService {
    PageResponse<DatasetResponse> getAll(Pageable pageable);

    DatasetResponse getById(UUID id);

    PageResponse<DatasetResponse> getByProject(UUID projectId, Pageable pageable);

    DatasetResponse create(DatasetRequest request);

    DatasetResponse update(UUID id, DatasetRequest request);

    void delete(UUID id);
}
