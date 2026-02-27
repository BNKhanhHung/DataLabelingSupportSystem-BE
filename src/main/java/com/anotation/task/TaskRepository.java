package com.anotation.task;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    Page<Task> findByProjectId(UUID projectId, Pageable pageable);

    Page<Task> findByAnnotatorId(UUID annotatorId, Pageable pageable);

    Page<Task> findByReviewerId(UUID reviewerId, Pageable pageable);

    @Query("""
            SELECT t FROM Task t
            WHERE (:name IS NULL OR LOWER(t.project.name) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:status IS NULL OR t.status = :status)
            """)
    Page<Task> search(@Param("name") String name, @Param("status") TaskStatus status, Pageable pageable);
}
