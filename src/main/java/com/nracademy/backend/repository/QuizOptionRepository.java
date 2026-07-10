package com.nracademy.backend.repository;

import com.nracademy.backend.entity.quiz.QuizOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizOptionRepository extends JpaRepository<QuizOption, UUID> {

    List<QuizOption> findByCourseIdAndQuestionIdOrderBySortOrderAsc(UUID courseId, UUID questionId);
}
