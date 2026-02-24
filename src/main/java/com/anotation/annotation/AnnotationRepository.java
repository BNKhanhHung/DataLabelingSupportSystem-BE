package com.anotation.annotation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    List<Annotation> findByTaskId(@Param("taskId") UUID taskId);

    // Count APPROVED annotations in a Task (used for COMPLETED check)
    @Query("""
            SELECT COUNT(a) FROM Annotation a
            WHERE a.taskItem.task.id = :taskId
            AND a.status = com.anotation.annotation.AnnotationStatus.APPROVED
            """)
    long countApprovedByTaskId(@Param("taskId") UUID taskId);
}
