package com.anotation.reviewfeedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA cho {@link ReviewFeedback}: truy vấn theo annotation, theo task,
 * và theo reviewer.
 */
@Repository
public interface ReviewFeedbackRepository extends JpaRepository<ReviewFeedback, UUID> {

    /**
     * Kiểm tra đã tồn tại phản hồi review cho annotation hay chưa (phục vụ chống review trùng).
     *
     * @param annotationId UUID annotation
     * @return {@code true} nếu đã có bản ghi
     */
    boolean existsByAnnotationId(UUID annotationId);

    /**
     * Tìm bản ghi review theo id của annotation (quan hệ 1-1 theo ràng buộc DB).
     *
     * @param annotationId UUID annotation
     * @return optional chứa {@link ReviewFeedback} nếu có
     */
    java.util.Optional<ReviewFeedback> findByAnnotationId(UUID annotationId);

    /**
     * Lấy trang các phản hồi review mà annotation của chúng thuộc task chỉ định
     * (đi theo {@code annotation → taskItem → task}).
     *
     * @param taskId   UUID task
     * @param pageable phân trang
     * @return trang entity
     */
    @Query("""
            SELECT rf FROM ReviewFeedback rf
            WHERE rf.annotation.taskItem.task.id = :taskId
            """)
    Page<ReviewFeedback> findByTaskId(@Param("taskId") UUID taskId, Pageable pageable);

    /**
     * Lấy trang phản hồi review do một reviewer thực hiện.
     *
     * @param reviewerId UUID user reviewer
     * @param pageable   phân trang
     * @return trang entity
     */
    Page<ReviewFeedback> findByReviewerId(UUID reviewerId, Pageable pageable);
}
