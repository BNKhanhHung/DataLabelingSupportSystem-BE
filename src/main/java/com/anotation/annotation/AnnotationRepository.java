package com.anotation.annotation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnnotationRepository extends JpaRepository<Annotation, UUID> {

        // Check duplicate — only one annotation per TaskItem
        boolean existsByTaskItemId(UUID taskItemId);

        // Get annotation of a specific TaskItem
        Optional<Annotation> findByTaskItemId(UUID taskItemId);

        // Get all annotations in a Task
        @Query("""
                        SELECT a FROM Annotation a
                        WHERE a.taskItem.task.id = :taskId
                        """)
        Page<Annotation> findByTaskId(@Param("taskId") UUID taskId, Pageable pageable);

        // Count APPROVED annotations in a Task (used for COMPLETED check)
        @Query("""
                        SELECT COUNT(a) FROM Annotation a
                        WHERE a.taskItem.task.id = :taskId
                        AND a.status = com.anotation.annotation.AnnotationStatus.APPROVED
                        """)
        long countApprovedByTaskId(@Param("taskId") UUID taskId);

        // Count annotations that are still SUBMITTED (not yet reviewed) in a task
        @Query("""
                        SELECT COUNT(a) FROM Annotation a
                        WHERE a.taskItem.task.id = :taskId
                        AND a.status = com.anotation.annotation.AnnotationStatus.SUBMITTED
                        """)
        long countSubmittedByTaskId(@Param("taskId") UUID taskId);

        // Count REJECTED annotations in a task
        @Query("""
                        SELECT COUNT(a) FROM Annotation a
                        WHERE a.taskItem.task.id = :taskId
                        AND a.status = com.anotation.annotation.AnnotationStatus.REJECTED
                        """)
        long countRejectedByTaskId(@Param("taskId") UUID taskId);

        // ── KPI queries by annotator user ─────────────────────────────────────────

        @Query("""
                        SELECT COUNT(a) FROM Annotation a
                        WHERE a.taskItem.task.annotator.id = :userId
                        """)
        long countByAnnotatorUserId(@Param("userId") UUID userId);

        @Query("""
                        SELECT COUNT(a) FROM Annotation a
                        WHERE a.taskItem.task.annotator.id = :userId
                        AND a.status = com.anotation.annotation.AnnotationStatus.APPROVED
                        """)
        long countApprovedByAnnotatorUserId(@Param("userId") UUID userId);

        @Query("""
                        SELECT COUNT(a) FROM Annotation a
                        WHERE a.taskItem.task.annotator.id = :userId
                        AND a.status = com.anotation.annotation.AnnotationStatus.REJECTED
                        """)
        long countRejectedByAnnotatorUserId(@Param("userId") UUID userId);
}
