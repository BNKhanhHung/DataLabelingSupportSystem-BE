package com.anotation.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO phản hồi (response) cho thông tin một {@link Task} trả về qua REST API.
 * <p>
 * Gồm định danh task, dự án, người gán nhãn/reviewer (kèm tên hiển thị), trạng thái,
 * danh sách {@code dataItemIds} thuộc task, hạn hoàn thành, cờ quá hạn và thời điểm tạo.
 * Dữ liệu thường được lắp từ entity và mapper sau khi đọc/ghi cơ sở dữ liệu.
 */
public class TaskResponse {

    /** Định danh duy nhất của task (UUID). */
    private UUID id;
    /** Định danh dự án chứa task. */
    private UUID projectId;
    /** Tên dự án (denormalized để client không cần gọi thêm API project). */
    private String projectName;
    /** Định danh user được giao vai trò annotator. */
    private UUID annotatorId;
    /** Tên đăng nhập của annotator. */
    private String annotatorUsername;
    /** Định danh user được giao vai trò reviewer. */
    private UUID reviewerId;
    /** Tên đăng nhập của reviewer. */
    private String reviewerUsername;
    /** Trạng thái vòng đời task ({@link TaskStatus}). */
    private TaskStatus status;
    /** Danh sách ID các {@code DataItem} gắn với task qua bảng task items. */
    private List<UUID> dataItemIds;
    /** Thời điểm hạn hoàn thành (deadline); có thể {@code null} nếu chưa đặt hoặc đã xóa hạn. */
    private LocalDateTime dueDate;
    /** {@code true} khi task đã quá {@link #dueDate} và vẫn chưa hoàn tất theo quy tắc nghiệp vụ. */
    private boolean overdue;
    /** Thời điểm bản ghi task được tạo. */
    private LocalDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public UUID getAnnotatorId() {
        return annotatorId;
    }

    public void setAnnotatorId(UUID annotatorId) {
        this.annotatorId = annotatorId;
    }

    public String getAnnotatorUsername() {
        return annotatorUsername;
    }

    public void setAnnotatorUsername(String annotatorUsername) {
        this.annotatorUsername = annotatorUsername;
    }

    public UUID getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(UUID reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getReviewerUsername() {
        return reviewerUsername;
    }

    public void setReviewerUsername(String reviewerUsername) {
        this.reviewerUsername = reviewerUsername;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public List<UUID> getDataItemIds() {
        return dataItemIds;
    }

    public void setDataItemIds(List<UUID> dataItemIds) {
        this.dataItemIds = dataItemIds;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isOverdue() {
        return overdue;
    }

    public void setOverdue(boolean overdue) {
        this.overdue = overdue;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
