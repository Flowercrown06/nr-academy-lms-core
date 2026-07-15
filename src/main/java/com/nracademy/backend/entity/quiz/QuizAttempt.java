package com.nracademy.backend.entity.quiz;

import com.nracademy.backend.entity.course.Course;
import com.nracademy.backend.entity.enums.QuizAttemptStatus;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
        name = "quiz_attempts",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_quiz_attempts_course_quiz_student_attempt",
                columnNames = {"course_id", "quiz_id", "student_id", "attempt_no"}
        ),
        indexes = {
                @Index(name = "idx_quiz_attempts_course_status_expires", columnList = "course_id, status, expires_at")
        }
)
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "course_id", nullable = false)
    UUID courseId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    Course course;

    @Column(name = "quiz_id", nullable = false)
    UUID quizId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", insertable = false, updatable = false)
    Quiz quiz;

    @Column(name = "student_id", nullable = false)
    UUID studentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    User student;

    @Column(name = "attempt_no", nullable = false)
    Integer attemptNo;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    QuizAttemptStatus status = QuizAttemptStatus.IN_PROGRESS;

    @Column(name = "started_at", nullable = false)
    LocalDateTime startedAt;

    @Column(name = "expires_at", nullable = false)
    LocalDateTime expiresAt;

    @Column(name = "submitted_at")
    LocalDateTime submittedAt;

    @Column(precision = 8, scale = 2)
    BigDecimal score;

    @Column(name = "correct_count")
    Integer correctCount;

    @Column(name = "total_questions")
    Integer totalQuestions;

    @Builder.Default
    @OneToMany(mappedBy = "attempt")
    List<QuizAnswer> answers = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (status == null) {
            status = QuizAttemptStatus.IN_PROGRESS;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuizAttempt other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
