package com.nracademy.backend.entity.assignment;

import com.nracademy.backend.entity.course.Course;
import com.nracademy.backend.entity.enums.SubmissionStatus;
import com.nracademy.backend.entity.enums.SubmissionType;
import com.nracademy.backend.entity.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(
        name = "assignment_submissions",
        indexes = {
                @Index(name = "idx_submissions_course_assignment_student", columnList = "course_id, assignment_id, student_id"),
                @Index(name = "idx_submissions_course_student_status", columnList = "course_id, student_id, status")
        }
)
public class AssignmentSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "course_id", nullable = false)
    UUID courseId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    Course course;

    @Column(name = "assignment_id", nullable = false)
    UUID assignmentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assignment_id", insertable = false, updatable = false)
    Assignment assignment;

    @Column(name = "student_id", nullable = false)
    UUID studentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    User student;

    @Enumerated(EnumType.STRING)
    @Column(name = "submission_type", nullable = false)
    SubmissionType submissionType;

    @Column(name = "file_url")
    String fileUrl;

    @Column(name = "file_key")
    String fileKey;

    @Lob
    @Column(name = "text_answer", columnDefinition = "TEXT")
    String textAnswer;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    SubmissionStatus status = SubmissionStatus.SUBMITTED;

    @Column(precision = 5, scale = 2)
    BigDecimal grade;

    @Lob
    @Column(name = "teacher_comment", columnDefinition = "TEXT")
    String teacherComment;

    @Column(name = "reviewed_by")
    UUID reviewedBy;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    Instant submittedAt;

    @Column(name = "reviewed_at")
    Instant reviewedAt;

    @PrePersist
    void prePersist() {
        if (submittedAt == null) {
            submittedAt = Instant.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssignmentSubmission other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
