package com.nracademy.backend.entity.quiz;

import com.nracademy.backend.entity.course.Course;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
        name = "quiz_answers",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_quiz_answers_course_attempt_question",
                columnNames = {"course_id", "attempt_id", "question_id"}
        )
)
public class QuizAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "course_id", nullable = false)
    UUID courseId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    Course course;

    @Column(name = "attempt_id", nullable = false)
    UUID attemptId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attempt_id", insertable = false, updatable = false)
    QuizAttempt attempt;

    @Column(name = "question_id", nullable = false)
    UUID questionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    QuizQuestion question;

    @Column(name = "selected_option_id")
    UUID selectedOptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id", insertable = false, updatable = false)
    QuizOption selectedOption;

    Boolean correct;

    @Column(name = "earned_points", precision = 8, scale = 2)
    BigDecimal earnedPoints;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuizAnswer other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
