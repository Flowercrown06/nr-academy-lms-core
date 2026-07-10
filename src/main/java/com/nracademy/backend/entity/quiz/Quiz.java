package com.nracademy.backend.entity.quiz;

import com.nracademy.backend.entity.course.Course;
import com.nracademy.backend.entity.enums.QuizStatus;
import com.nracademy.backend.entity.group.Group;
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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
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
        name = "quizzes",
        indexes = @Index(name = "idx_quizzes_course_group_status", columnList = "course_id, group_id, status")
)
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "course_id", nullable = false)
    UUID courseId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    Course course;

    @Column(name = "group_id", nullable = false)
    UUID groupId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", insertable = false, updatable = false)
    Group group;

    @Column(name = "teacher_id", nullable = false)
    UUID teacherId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    User teacher;

    @Column(nullable = false)
    String title;

    @Column(name = "duration_minutes", nullable = false)
    Integer durationMinutes;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    QuizStatus status = QuizStatus.DRAFT;

    @Column(name = "available_from")
    LocalDateTime availableFrom;

    @Column(name = "available_until")
    LocalDateTime availableUntil;

    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    Instant updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "quiz")
    List<QuizQuestion> questions = new ArrayList<>();

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quiz other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
