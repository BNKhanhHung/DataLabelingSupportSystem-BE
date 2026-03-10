package com.anotation.project;

/**
 * Computed status of a Project based on its tasks.
 *
 * NOT_STARTED — No tasks yet, or all tasks are still OPEN (nobody started
 * working).
 * IN_PROGRESS — At least one task is being worked on (IN_PROGRESS / SUBMITTED /
 * REVIEWED).
 * OVERDUE — At least one task has passed its dueDate and is not yet COMPLETED.
 * COMPLETED — All tasks in the project are COMPLETED.
 */
public enum ProjectStatus {
    NOT_STARTED,
    IN_PROGRESS,
    OVERDUE,
    COMPLETED
}
