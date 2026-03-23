package com.anotation.reviewfeedback;

import com.anotation.common.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Hợp đồng nghiệp vụ đọc/ghi {@link ReviewFeedback} và thực hiện luồng review annotation
 * (cập nhật trạng thái annotation, data item theo {@link ReviewStatus}).
 */
public interface ReviewFeedbackService {

    /**
     * Danh sách phản hồi review có phân trang; sort không hợp lệ được xử lý an toàn ở tầng triển khai.
     *
     * @param pageable phân trang
     * @return {@link PageResponse} chứa {@link ReviewResponse}
     */
    PageResponse<ReviewResponse> getAll(Pageable pageable);

    /**
     * Chi tiết một phản hồi theo id.
     *
     * @param id UUID bản ghi
     * @return DTO phản hồi
     * @throws com.anotation.exception.NotFoundException nếu không tồn tại
     */
    ReviewResponse getById(UUID id);

    /**
     * Các phản hồi review liên quan tới một task.
     *
     * @param taskId   UUID task
     * @param pageable phân trang
     * @return trang DTO
     */
    PageResponse<ReviewResponse> getByTask(UUID taskId, Pageable pageable);

    /**
     * Các phản hồi do một reviewer gửi.
     *
     * @param reviewerId UUID reviewer
     * @param pageable   phân trang
     * @return trang DTO
     */
    PageResponse<ReviewResponse> getByReviewer(UUID reviewerId, Pageable pageable);

    /**
     * Tạo review cho annotation: kiểm tra quyền, trạng thái task/annotation, chống trùng,
     * rồi cập nhật trạng thái annotation và data item tương ứng.
     *
     * @param request payload review
     * @return DTO sau khi lưu
     * @throws com.anotation.exception.NotFoundException     nếu annotation không tồn tại
     * @throws com.anotation.exception.BadRequestException   nếu vi phạm điều kiện nghiệp vụ hoặc chưa đăng nhập hợp lệ
     * @throws com.anotation.exception.DuplicateException    nếu annotation đã có review
     */
    ReviewResponse review(ReviewRequest request);

    /**
     * Xóa bản ghi review theo id (bản ghi phải tồn tại).
     *
     * @param id UUID
     * @throws com.anotation.exception.NotFoundException nếu không tìm thấy
     */
    void delete(UUID id);
}
