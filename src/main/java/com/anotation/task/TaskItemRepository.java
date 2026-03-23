package com.anotation.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA cho {@link TaskItem}: truy vấn theo task và các đếm phục vụ nghiệp vụ
 * (data item đang trong task active, số item chưa reviewed, số item chưa có annotation).
 */
@Repository
public interface TaskItemRepository extends JpaRepository<TaskItem, UUID> {

        /**
         * Tất cả task item thuộc một task.
         *
         * @param taskId UUID task
         * @return danh sách (thứ tự không đảm bảo trừ khi service sort)
         */
        List<TaskItem> findByTaskId(UUID taskId);

        /**
         * {@code true} nếu data item đã nằm trong ít nhất một task “đang sống” (OPEN, IN_PROGRESS,
         * OVERDUE, SUBMITTED, REVIEWED) — dùng để tránh gán trùng mẫu vào nhiều luồng active.
         *
         * @param dataItemId UUID data item
         * @return có tồn tại task item như vậy hay không
         */
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

        /**
         * Số lượng task item trong task mà data item liên kết chưa đạt {@link com.anotation.dataitem.DataItemStatus#REVIEWED}.
         *
         * @param taskId UUID task
         * @return số lượng (0 nếu tất cả đã reviewed)
         */
        @Query("""
                        SELECT COUNT(ti) FROM TaskItem ti
                        WHERE ti.task.id = :taskId
                        AND ti.dataItem.status != com.anotation.dataitem.DataItemStatus.REVIEWED
                        """)
        long countNonReviewedItemsInTask(@Param("taskId") UUID taskId);

        /**
         * Số task item chưa có bất kỳ {@link com.anotation.annotation.Annotation} nào — dùng trước khi
         * cho phép annotator nộp task (submit for review).
         *
         * @param taskId UUID task
         * @return số item thiếu annotation
         */
        @Query("""
                        SELECT COUNT(ti) FROM TaskItem ti
                        WHERE ti.task.id = :taskId
                        AND NOT EXISTS (SELECT 1 FROM com.anotation.annotation.Annotation a WHERE a.taskItem.id = ti.id)
                        """)
        long countItemsWithoutAnnotation(@Param("taskId") UUID taskId);
}
