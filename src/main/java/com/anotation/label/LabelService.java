package com.anotation.label;

import com.anotation.common.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface LabelService {
    PageResponse<LabelResponse> getAll(Pageable pageable);

    LabelResponse getById(UUID id);

    PageResponse<LabelResponse> getByProject(UUID projectId, Pageable pageable);

    LabelResponse create(LabelRequest request);

    LabelResponse update(UUID id, LabelRequest request);

    void delete(UUID id);
}
