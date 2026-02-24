package com.anotation.reviewfeedback;

import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewResponse toResponse(ReviewFeedback rf) {
        ReviewResponse response = new ReviewResponse();
        response.setId(rf.getId());
        response.setStatus(rf.getStatus());
        response.setComment(rf.getComment());
        response.setCreatedAt(rf.getCreatedAt());

        // Flatten Annotation chain: Annotation → TaskItem → DataItem
        response.setAnnotationId(rf.getAnnotation().getId());
        response.setTaskItemId(rf.getAnnotation().getTaskItem().getId());
        response.setDataItemId(rf.getAnnotation().getTaskItem().getDataItem().getId());
        response.setContentUrl(rf.getAnnotation().getTaskItem().getDataItem().getContentUrl());

        // Flatten Reviewer
        response.setReviewerId(rf.getReviewer().getId());
        response.setReviewerUsername(rf.getReviewer().getUsername());

        return response;
    }
}
