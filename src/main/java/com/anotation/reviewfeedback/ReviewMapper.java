package com.anotation.reviewfeedback;

import org.springframework.stereotype.Component;

/**
 * Map {@link ReviewFeedback} sang {@link ReviewResponse}: làm phẳng chuỗi annotation → task item
 * → data item (id, URL nội dung) và thông tin reviewer (id, username).
 */
@Component
public class ReviewMapper {

    /**
     * Chuyển entity review sang DTO cho API.
     * <p>
     * Giả định các quan hệ LAZY đã được khởi tạo trong ngữ cảnh gọi (transaction đọc).
     *
     * @param rf entity {@link ReviewFeedback}
     * @return {@link ReviewResponse} đã điền đủ trường phẳng
     */
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
