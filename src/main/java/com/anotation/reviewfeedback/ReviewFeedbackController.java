package com.anotation.reviewfeedback;

import com.anotation.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST API quản lý phản hồi review ({@link ReviewFeedback}) theo từng annotation.
 * <p>
 * Reviewer gửi kết quả duyệt qua {@link ReviewRequest} (annotationId, reviewerId,
 * {@link ReviewStatus}, comment). Việc chuyển trạng thái tổng thể của {@link com.anotation.task.Task}
 * (ví dụ sau khi review xong toàn bộ) do luồng {@code complete-review} trong tầng task xử lý,
 * không hoàn tất chỉ sau một lần POST review đơn lẻ.
 * <p>
 * Đường dẫn cơ sở: {@code /api/review-feedbacks}. Các endpoint chính: danh sách phân trang,
 * chi tiết theo id, theo task, theo reviewer; tạo review; xóa theo id.
 */
@RestController
@RequestMapping("/api/review-feedbacks")
@Tag(name = "ReviewFeedback", description = "Review feedback APIs")
public class ReviewFeedbackController {

    private final ReviewFeedbackService reviewFeedbackService;

    /**
     * @param reviewFeedbackService tầng nghiệp vụ review
     */
    public ReviewFeedbackController(ReviewFeedbackService reviewFeedbackService) {
        this.reviewFeedbackService = reviewFeedbackService;
    }

    /**
     * Lấy tất cả phản hồi review có phân trang.
     * <p>
     * Sort hợp lệ trên entity: ví dụ {@code id}, {@code status}, {@code createdAt}. Tránh
     * {@code sort} trỏ tới kiểu chuỗi không map được để giảm lỗi sort.
     *
     * @param pageable tham số phân trang OpenAPI/Spring
     * @return HTTP 200 và {@link PageResponse} chứa {@link ReviewResponse}
     */
    @GetMapping
    @Operation(summary = "Get all review feedbacks",
            description = "Sort hợp lệ: id, status, createdAt (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<ReviewResponse>> getAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(reviewFeedbackService.getAll(pageable)); // 200
    }

    /**
     * Lấy một phản hồi review theo định danh.
     *
     * @param id UUID bản ghi {@link ReviewFeedback}
     * @return HTTP 200 và {@link ReviewResponse}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get review feedback by ID")
    public ResponseEntity<ReviewResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(reviewFeedbackService.getById(id)); // 200
    }

    /**
     * Lấy các phản hồi review thuộc một task (thông qua chuỗi annotation → task item → task).
     *
     * @param taskId   UUID task
     * @param pageable phân trang; sort gợi ý: {@code id}, {@code createdAt}
     * @return HTTP 200 và trang kết quả
     */
    @GetMapping("/task/{taskId}")
    @Operation(summary = "Get all review feedbacks in a task",
            description = "Sort hợp lệ: id, createdAt (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<ReviewResponse>> getByTask(
            @PathVariable UUID taskId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(reviewFeedbackService.getByTask(taskId, pageable)); // 200
    }

    /**
     * Lấy các phản hồi review do một reviewer cụ thể gửi.
     *
     * @param reviewerId UUID user đóng vai reviewer
     * @param pageable   phân trang
     * @return HTTP 200 và trang kết quả
     */
    @GetMapping("/reviewer/{reviewerId}")
    @Operation(summary = "Get all review feedbacks submitted by a reviewer",
            description = "Sort hợp lệ: id, createdAt (vd: sort=id,asc). Tránh sort=string.")
    public ResponseEntity<PageResponse<ReviewResponse>> getByReviewer(
            @PathVariable UUID reviewerId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(reviewFeedbackService.getByReviewer(reviewerId, pageable)); // 200
    }

    @PostMapping
    @Operation(summary = "Submit a review for an annotation")
    public ResponseEntity<ReviewResponse> review(@Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewFeedbackService.review(request)); // 201
    }

    /**
     * Xóa một bản ghi phản hồi review theo id.
     *
     * @param id UUID cần xóa
     * @return HTTP 204 không có body
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a review feedback")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        reviewFeedbackService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
