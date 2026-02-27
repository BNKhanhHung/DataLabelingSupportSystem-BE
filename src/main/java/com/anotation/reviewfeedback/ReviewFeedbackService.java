package com.anotation.reviewfeedback;

import com.anotation.common.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ReviewFeedbackService {
    PageResponse<ReviewResponse> getAll(Pageable pageable);

    ReviewResponse getById(UUID id);

    PageResponse<ReviewResponse> getByTask(UUID taskId, Pageable pageable);

    PageResponse<ReviewResponse> getByReviewer(UUID reviewerId, Pageable pageable);

    ReviewResponse review(ReviewRequest request);

    void delete(UUID id);
}
