package com.anotation.reviewfeedback;

import com.anotation.annotation.Annotation;
import com.anotation.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity JPA lưu một lần reviewer duyệt/từ chối cho đúng một {@link Annotation}.
 * <p>
 * Ràng buộc duy nhất {@code uq_review_annotation}: mỗi annotation chỉ được có tối đa một
 * bản ghi phản hồi review. Liên kết {@code reviewer} trỏ tới {@link User} thực hiện review.
 * {@link #createdAt} được gán tự động trong {@link #onCreate()}.
 */
@Entity
@Table(name = "review_feedbacks", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "uq_review_annotation", columnNames = { "annotation_id" })
})
public class ReviewFeedback {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "annotation_id", nullable = false)
    private Annotation annotation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Callback JPA trước khi insert: gán thời điểm tạo bản ghi.
     */
    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
