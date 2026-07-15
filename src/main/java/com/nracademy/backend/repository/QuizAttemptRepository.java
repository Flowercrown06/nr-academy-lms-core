package com.nracademy.backend.repository;

import com.nracademy.backend.entity.enums.QuizAttemptStatus;
import com.nracademy.backend.entity.quiz.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {

    Optional<QuizAttempt> findByCourseIdAndId(UUID courseId, UUID attemptId);

    List<QuizAttempt> findByStatusAndExpiresAtBefore(QuizAttemptStatus status, LocalDateTime now);

    List<QuizAttempt> findByCourseIdAndQuizIdAndStudentIdOrderByAttemptNoAsc(
            UUID courseId, UUID quizId, UUID studentId);

    Optional<QuizAttempt> findByCourseIdAndQuizIdAndStudentIdAndStatus(
            UUID courseId, UUID quizId, UUID studentId, QuizAttemptStatus status);

    // Used by GET /api/v1/student/quizzes 'attempted' filter - a student has
    // "attempted" a quiz if any attempt row (in any status) exists for them.
    @Query(
            "select distinct a.quizId from QuizAttempt a where a.courseId = :courseId and a.studentId = :studentId")
    List<UUID> findAttemptedQuizIdsByCourseIdAndStudentId(UUID courseId, UUID studentId);
}
