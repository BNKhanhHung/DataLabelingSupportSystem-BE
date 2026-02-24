package com.anotation.annotation;

import java.util.List;
import java.util.UUID;

public interface AnnotationService {
    List<AnnotationResponse> getAll();

    AnnotationResponse getById(UUID id);

    List<AnnotationResponse> getByTask(UUID taskId);

    AnnotationResponse submit(AnnotationRequest request);

    AnnotationResponse updateContent(UUID id, String content);

    void delete(UUID id);
}
