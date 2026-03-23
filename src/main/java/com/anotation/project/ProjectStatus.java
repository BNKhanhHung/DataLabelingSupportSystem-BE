package com.anotation.project;

/**
 * Trạng thái tổng hợp của một dự án, được suy ra từ tập hợp các task thuộc dự án
 * (không lưu trực tiếp như cột cố định trên bảng project).
 * <ul>
 *   <li>{@link #NOT_STARTED} — Chưa có task, hoặc mọi task vẫn ở trạng thái OPEN
 *       (chưa ai bắt đầu làm việc thực chất).</li>
 *   <li>{@link #IN_PROGRESS} — Có ít nhất một task đang trong quy trình làm việc
 *       (ví dụ IN_PROGRESS, SUBMITTED, REVIEWED — tùy quy tắc mapper).</li>
 *   <li>{@link #OVERDUE} — Có ít nhất một task đã quá {@code dueDate} và chưa hoàn thành
 *       (chưa COMPLETED theo logic nghiệp vụ).</li>
 *   <li>{@link #COMPLETED} — Tất cả task trong dự án đều ở trạng thái COMPLETED.</li>
 * </ul>
 */
public enum ProjectStatus {
    NOT_STARTED,
    IN_PROGRESS,
    OVERDUE,
    COMPLETED
}
