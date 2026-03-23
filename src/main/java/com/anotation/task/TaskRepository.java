package com.anotation.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA cho {@link Task}: lọc theo project, annotator, reviewer, tìm kiếm theo tên project
 * và trạng thái, truy vấn quá hạn, giới hạn WIP và thống kê KPI.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

  /**
   * @param projectId UUID project
   * @param pageable  phân trang
   * @return trang task thuộc project
   */
  Page<Task> findByProjectId(UUID projectId, Pageable pageable);

  /**
   * @param annotatorId UUID annotator
   * @param pageable    phân trang
   * @return trang task được giao cho annotator
   */
  Page<Task> findByAnnotatorId(UUID annotatorId, Pageable pageable);

  /**
   * @param reviewerId UUID reviewer
   * @param pageable   phân trang
   * @return trang task có reviewer tương ứng
   */
  Page<Task> findByReviewerId(UUID reviewerId, Pageable pageable);

  /**
   * Task của annotator nhưng loại trừ các trạng thái trong tập (ví dụ ẩn COMPLETED và REVIEWED để chỉ
   * hiện việc còn làm).
   *
   * @param annotatorId UUID annotator
   * @param statuses    tập trạng thái cần loại trừ
   * @param pageable    phân trang
   * @return trang kết quả
   */
  Page<Task> findByAnnotatorIdAndStatusNotIn(UUID annotatorId, Collection<TaskStatus> statuses, Pageable pageable);

  /**
   * Task của reviewer khớp đúng một {@link TaskStatus}.
   *
   * @param reviewerId UUID reviewer
   * @param status     trạng thái
   * @param pageable   phân trang
   * @return trang task
   */
  Page<Task> findByReviewerIdAndStatus(UUID reviewerId, TaskStatus status, Pageable pageable);

  /**
   * Task của reviewer có trạng thái thuộc tập (ví dụ SUBMITTED và OVERDUE — đã nộp hoặc quá hạn chờ xử lý).
   *
   * @param reviewerId UUID reviewer
   * @param statuses   tập trạng thái
   * @param pageable   phân trang
   * @return trang task
   */
  Page<Task> findByReviewerIdAndStatusIn(UUID reviewerId, Collection<TaskStatus> statuses, Pageable pageable);

  @Query("""
      SELECT t FROM Task t
      WHERE (:name IS NULL OR LOWER(t.project.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:status IS NULL OR t.status = :status)
      """)
  /**
   * Tìm task theo tên project (LIKE không phân biệt hoa thường) và/hoặc trạng thái task.
   *
   * @param name     chuỗi con tên project; null → bỏ lọc tên
   * @param status   trạng thái task; null → bỏ lọc trạng thái
   * @param pageable phân trang
   * @return trang task
   */
  Page<Task> search(@Param("name") String name, @Param("status") TaskStatus status, Pageable pageable);

  // ── Overdue queries ──────────────────────────────────────────────────────────

  /**
   * Task có {@code dueDate} trước {@code now} và chưa COMPLETED/REVIEWED — dùng cho danh sách quá hạn hiển thị.
   *
   * @param now      mốc thời gian so sánh
   * @param pageable phân trang
   * @return trang task quá hạn
   */
  @Query("""
      SELECT t FROM Task t
      WHERE t.dueDate IS NOT NULL
        AND t.dueDate < :now
        AND t.status NOT IN (com.anotation.task.TaskStatus.COMPLETED, com.anotation.task.TaskStatus.REVIEWED)
      """)
  Page<Task> findOverdueTasks(@Param("now") LocalDateTime now, Pageable pageable);

  /**
   * Task đủ điều kiện gắn trạng thái OVERDUE: quá {@code dueDate}, trạng thái hiện tại thuộc nhóm
   * OPEN, IN_PROGRESS, DENIED hoặc SUBMITTED (annotator đã nộp nhưng reviewer chưa hoàn tất).
   *
   * @param now      mốc thời gian
   * @param pageable giới hạn batch (nếu dùng)
   * @return danh sách task cần cập nhật
   */
  @Query("""
      SELECT t FROM Task t
      WHERE t.dueDate IS NOT NULL
        AND t.dueDate < :now
        AND t.status IN (com.anotation.task.TaskStatus.OPEN,
                         com.anotation.task.TaskStatus.IN_PROGRESS,
                         com.anotation.task.TaskStatus.DENIED,
                         com.anotation.task.TaskStatus.SUBMITTED)
      """)
  List<Task> findTasksToMarkOverdue(@Param("now") LocalDateTime now, Pageable pageable);

  // ── WIP Limit count queries ──────────────────────────────────────────────────

  /**
   * Số task “active” của annotator: trạng thái OPEN, IN_PROGRESS, OVERDUE, SUBMITTED (chưa kết thúc luồng).
   *
   * @param userId UUID annotator
   * @return số lượng
   */
  @Query("""
      SELECT COUNT(t) FROM Task t
      WHERE t.annotator.id = :userId
        AND t.status IN (com.anotation.task.TaskStatus.OPEN,
                         com.anotation.task.TaskStatus.IN_PROGRESS,
                         com.anotation.task.TaskStatus.OVERDUE,
                         com.anotation.task.TaskStatus.SUBMITTED)
      """)
  long countActiveTasksByAnnotatorId(@Param("userId") UUID userId);

  /**
   * Số task active của reviewer (cùng tập trạng thái như annotator) — giới hạn WIP phía reviewer.
   *
   * @param userId UUID reviewer
   * @return số lượng
   */
  @Query("""
      SELECT COUNT(t) FROM Task t
      WHERE t.reviewer.id = :userId
        AND t.status IN (com.anotation.task.TaskStatus.OPEN,
                         com.anotation.task.TaskStatus.IN_PROGRESS,
                         com.anotation.task.TaskStatus.OVERDUE,
                         com.anotation.task.TaskStatus.SUBMITTED)
      """)
  long countActiveTasksByReviewerId(@Param("userId") UUID userId);

  /**
   * Task của annotator lọc theo một trạng thái cụ thể.
   *
   * @param annotatorId UUID annotator
   * @param status      trạng thái
   * @param pageable    phân trang
   * @return trang task
   */
  Page<Task> findByAnnotatorIdAndStatus(UUID annotatorId, TaskStatus status, Pageable pageable);

  // ── KPI count queries ────────────────────────────────────────────────────────

  /**
   * @param annotatorId UUID annotator
   * @return tổng số task (mọi trạng thái) có annotator đó
   */
  long countByAnnotatorId(UUID annotatorId);

  /**
   * @param annotatorId UUID annotator
   * @param status      trạng thái cần đếm
   * @return số task khớp
   */
  long countByAnnotatorIdAndStatus(UUID annotatorId, TaskStatus status);

  /**
   * Số task của annotator quá hạn (dueDate trước {@code now}) và chưa COMPLETED/REVIEWED.
   *
   * @param userId UUID annotator
   * @param now    mốc thời gian
   * @return số task
   */
  @Query("""
      SELECT COUNT(t) FROM Task t
      WHERE t.annotator.id = :userId
        AND t.dueDate IS NOT NULL
        AND t.dueDate < :now
        AND t.status NOT IN (com.anotation.task.TaskStatus.COMPLETED, com.anotation.task.TaskStatus.REVIEWED)
      """)
  long countOverdueByAnnotatorId(@Param("userId") UUID userId, @Param("now") LocalDateTime now);
}
