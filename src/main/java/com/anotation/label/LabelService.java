package com.anotation.label;

import java.util.List;
import java.util.UUID;

public interface LabelService {
    List<LabelResponse> getAll();

    LabelResponse getById(UUID id);

    List<LabelResponse> getByProject(UUID projectId);

    LabelResponse create(LabelRequest request);

    LabelResponse update(UUID id, LabelRequest request);

    void delete(UUID id);
}
