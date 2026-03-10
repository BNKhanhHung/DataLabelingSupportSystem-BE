package com.anotation.task;

import java.util.UUID;

/**
 * KPI (Key Performance Indicator) response for a user.
 * Used by Manager to evaluate Annotator/Reviewer performance.
 */
public class KpiResponse {

    private UUID userId;
    private String username;

    // Task metrics
    private long totalTasks;
    private long completedTasks;
    private long overdueTasks;

    // Annotation metrics
    private long totalAnnotations;
    private long approvedCount;
    private long rejectedCount;

    // Calculated rate (%)
    private double approvalRate;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(long totalTasks) {
        this.totalTasks = totalTasks;
    }

    public long getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(long completedTasks) {
        this.completedTasks = completedTasks;
    }

    public long getOverdueTasks() {
        return overdueTasks;
    }

    public void setOverdueTasks(long overdueTasks) {
        this.overdueTasks = overdueTasks;
    }

    public long getTotalAnnotations() {
        return totalAnnotations;
    }

    public void setTotalAnnotations(long totalAnnotations) {
        this.totalAnnotations = totalAnnotations;
    }

    public long getApprovedCount() {
        return approvedCount;
    }

    public void setApprovedCount(long approvedCount) {
        this.approvedCount = approvedCount;
    }

    public long getRejectedCount() {
        return rejectedCount;
    }

    public void setRejectedCount(long rejectedCount) {
        this.rejectedCount = rejectedCount;
    }

    public double getApprovalRate() {
        return approvalRate;
    }

    public void setApprovalRate(double approvalRate) {
        this.approvalRate = approvalRate;
    }
}
