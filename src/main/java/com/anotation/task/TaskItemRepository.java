package com.anotation.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskItemRepository extends JpaRepository<TaskItem, UUID> {

        List<TaskItem> findByTaskId(UUID taskId);

        // Check if a DataItem is already in an ACTIVE task (OPEN, IN_PROGRESS,
        // SUBMITTED, REVIEWED, OVERDUE)
        @Query("""
                        SELECT COUNT(ti) > 0 FROM TaskItem ti
                        WHERE ti.dataItem.id = :dataItemId
                        AND ti.task.status IN (com.anotation.task.TaskStatus.OPEN,
                                               com.anotation.task.TaskStatus.IN_PROGRESS,
                                               com.anotation.task.TaskStatus.OVERDUE,
                                               com.anotation.task.TaskStatus.SUBMITTED,
                                               com.anotation.task.TaskStatus.REVIEWED)
                        """)
        boolean existsActiveTaskForDataItem(@Param("dataItemId") UUID dataItemId);

        // Count DataItems in a task that are NOT yet REVIEWED
        @Query("""
                        SELECT COUNT(ti) FROM TaskItem ti
                        WHERE ti.task.id = :taskId
                        AND ti.dataItem.status != com.anotation.dataitem.DataItemStatus.REVIEWED
                        """)
        long countNonReviewedItemsInTask(@Param("taskId") UUID taskId);

        // Count TaskItems that do NOT have any annotation yet (used by submitForReview)
        @Query("""
                        SELECT COUNT(ti) FROM TaskItem ti
                        WHERE ti.task.id = :taskId
                        AND NOT EXISTS (SELECT 1 FROM com.anotation.annotation.Annotation a WHERE a.taskItem.id = ti.id)
                        """)
        long countItemsWithoutAnnotation(@Param("taskId") UUID taskId);
}
