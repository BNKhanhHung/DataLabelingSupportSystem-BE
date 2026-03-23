package com.anotation.reviewfeedback;

/**
 * Kết quả review ở cấp một annotation (một reviewer cho một lần quyết định).
 * <ul>
 *   <li>{@link #APPROVED} — Reviewer chấp nhận annotation; data item chuyển trạng thái reviewed
 *       theo luồng nghiệp vụ.</li>
 *   <li>{@link #REJECTED} — Reviewer từ chối; annotator cần chỉnh sửa/làm lại; thường bắt buộc
 *       kèm lý do trong comment.</li>
 * </ul>
 */
public enum ReviewStatus {
    /** Reviewer chấp thuận annotation. */
    APPROVED,
    /** Reviewer từ chối; annotator phải xử lý lại. */
    REJECTED
}
