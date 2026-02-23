package com.anotation.reviewfeedback;

import java.util.List;
import java.util.UUID;

public interface ReviewFeedbackService {
    List<ReviewResponse> getAll();

    ReviewResponse getById(UUID id);

    List<ReviewResponse> getByTask(UUID taskId);

    List<ReviewResponse> getByReviewer(UUID reviewerId);

    ReviewResponse review(ReviewRequest request);

    void delete(UUID id);
}
