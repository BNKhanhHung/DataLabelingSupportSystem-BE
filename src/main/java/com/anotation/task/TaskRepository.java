package com.anotation.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
  Page<Task> findByProjectId(UUID projectId, Pageable pageable);

  Page<Task> findByAnnotatorId(UUID annotatorId, Pageable pageable);

  Page<Task> findByReviewerId(UUID reviewerId, Pageable pageable);

  /** Task được giao cho annotator, loại trừ trạng thái COMPLETED và REVIEWED (chỉ hiện task còn cần làm). */
  Page<Task> findByAnnotatorIdAndStatusNotIn(UUID annotatorId, Collection<TaskStatus> statuses, Pageable pageable);

  /** Task cần review: chỉ trạng thái SUBMITTED (annotator đã nộp, chờ reviewer). */
  Page<Task> findByReviewerIdAndStatus(UUID reviewerId, TaskStatus status, Pageable pageable);

  @Query("""
      SELECT t FROM Task t
      WHERE (:name IS NULL OR LOWER(t.project.name) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:status IS NULL OR t.status = :status)
      """)
  Page<Task> search(@Param("name") String name, @Param("status") TaskStatus status, Pageable pageable);

  // ── Overdue queries ──────────────────────────────────────────────────────────

  @Query("""
      SELECT t FROM Task t
      WHERE t.dueDate IS NOT NULL
        AND t.dueDate < :now
        AND t.status NOT IN (com.anotation.task.TaskStatus.COMPLETED, com.anotation.task.TaskStatus.REVIEWED)
      """)
  Page<Task> findOverdueTasks(@Param("now") LocalDateTime now, Pageable pageable);

  // ── KPI count queries ────────────────────────────────────────────────────────

  long countByAnnotatorId(UUID annotatorId);

  long countByAnnotatorIdAndStatus(UUID annotatorId, TaskStatus status);

  @Query("""
      SELECT COUNT(t) FROM Task t
      WHERE t.annotator.id = :userId
        AND t.dueDate IS NOT NULL
        AND t.dueDate < :now
        AND t.status NOT IN (com.anotation.task.TaskStatus.COMPLETED, com.anotation.task.TaskStatus.REVIEWED)
      """)
  long countOverdueByAnnotatorId(@Param("userId") UUID userId, @Param("now") LocalDateTime now);
}
