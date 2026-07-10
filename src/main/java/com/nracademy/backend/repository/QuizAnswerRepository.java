package com.nracademy.backend.repository;

import com.nracademy.backend.entity.quiz.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, UUID> {

    Optional<QuizAnswer> findByCourseIdAndAttemptIdAndQuestionId(UUID courseId, UUID attemptId, UUID questionId);

    List<QuizAnswer> findByCourseIdAndAttemptId(UUID courseId, UUID attemptId);
}
