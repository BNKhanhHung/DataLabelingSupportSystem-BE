package com.anotation.activitylog;

/**
 * Enum liệt kê tất cả hành động có thể ghi nhật ký trong hệ thống.
 * <p>
 * Dùng làm bằng chứng truy vết: ai đã làm gì, lúc nào, trên đối tượng nào.
 */
public enum ActivityAction {

    // ── Task ─────────────────────────────────────────────────────────────────────
    TASK_CREATED,
    TASK_ASSIGNED,
    TASK_REASSIGNED,
    TASK_STATUS_CHANGED,
    TASK_SUBMITTED,
    TASK_REFUSED,
    TASK_REVIEW_COMPLETED,
    TASK_OVERDUE_MARKED,
    TASK_DUE_DATE_UPDATED,
    TASK_DELETED,

    // ── Annotation ───────────────────────────────────────────────────────────────
    ANNOTATION_SUBMITTED,
    ANNOTATION_UPDATED,
    ANNOTATION_DELETED,

    // ── Review Feedback ──────────────────────────────────────────────────────────
    REVIEW_SUBMITTED,
    REVIEW_DELETED,

    // ── Project ──────────────────────────────────────────────────────────────────
    PROJECT_CREATED,
    PROJECT_UPDATED,
    PROJECT_DELETED
}
