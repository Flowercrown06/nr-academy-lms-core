package com.nracademy.backend.repository;

import com.nracademy.backend.entity.quiz.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, UUID> {

    List<QuizQuestion> findByCourseIdAndQuizIdOrderBySortOrderAsc(UUID courseId, UUID quizId);
}
