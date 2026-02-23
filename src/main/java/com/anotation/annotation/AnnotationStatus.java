package com.anotation.annotation;

public enum AnnotationStatus {
    SUBMITTED, // Annotator đã nộp, chờ reviewer duyệt
    APPROVED, // Reviewer chấp thuận
    REJECTED // Reviewer từ chối, Annotator cần sửa lại
}
