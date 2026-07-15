package com.nracademy.backend.repository;

import com.nracademy.backend.entity.enums.QuizAttemptStatus;
import com.nracademy.backend.entity.quiz.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
