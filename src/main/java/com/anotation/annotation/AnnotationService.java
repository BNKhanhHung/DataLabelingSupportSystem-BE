package com.anotation.annotation;

import com.anotation.common.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AnnotationService {
    PageResponse<AnnotationResponse> getAll(Pageable pageable);

    AnnotationResponse getById(UUID id);

    PageResponse<AnnotationResponse> getByTask(UUID taskId, Pageable pageable);

    AnnotationResponse submit(AnnotationRequest request);

    AnnotationResponse updateContent(UUID id, String content);

    void delete(UUID id);
}
