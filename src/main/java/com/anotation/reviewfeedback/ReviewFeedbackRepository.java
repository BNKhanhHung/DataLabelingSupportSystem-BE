package com.anotation.reviewfeedback;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewFeedbackRepository extends JpaRepository<ReviewFeedback, UUID> {

    // Prevent duplicate review for same annotation
    boolean existsByAnnotationId(UUID annotationId);

    // Get review by annotation
    java.util.Optional<ReviewFeedback> findByAnnotationId(UUID annotationId);

    // Get all reviews in a task
    @Query("""
            SELECT rf FROM ReviewFeedback rf
            WHERE rf.annotation.taskItem.task.id = :taskId
            """)
    List<ReviewFeedback> findByTaskId(@Param("taskId") UUID taskId);

    // Get all reviews done by reviewer
    List<ReviewFeedback> findByReviewerId(UUID reviewerId);
}
