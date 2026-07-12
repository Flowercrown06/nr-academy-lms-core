package com.nracademy.backend.repository;

import com.nracademy.backend.entity.quiz.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, UUID>, JpaSpecificationExecutor<Quiz> {


    Optional<Quiz> findByCourseIdAndId(UUID courseId, UUID quizId);


    List<Quiz> findByCourseIdAndGroupId(UUID courseId, UUID groupId);


    List<Quiz> findByCourseIdAndGroupIdAndStatus(
            UUID courseId, UUID groupId, com.nracademy.backend.entity.enums.QuizStatus status);
}