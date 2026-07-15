package com.nracademy.backend.service;

import com.nracademy.backend.dto.request.CreateQuestionRequest;
import com.nracademy.backend.dto.request.CreateQuizRequest;
import com.nracademy.backend.dto.request.QuizStatusUpdateRequest;
import com.nracademy.backend.dto.request.UpdateQuestionRequest;
import com.nracademy.backend.dto.request.UpdateQuizRequest;
import com.nracademy.backend.dto.response.QuizDto;
import com.nracademy.backend.dto.response.TeacherQuestionDto;

import java.util.List;
import java.util.UUID;

/**
 * Teacher / Course Owner side of quiz management: create quizzes, manage
 * questions and options, and transition quiz status (DRAFT -> PUBLISHED -> CLOSED).
 *
 * Student-facing concerns (starting attempts, answering, grading) live in
 * QuizAttemptService and QuizGradingService instead - this service never
 * returns a student-safe DTO, only Teacher* DTOs, since everything here is
 * gated to COURSE_OWNER / TEACHER roles.
 */
public interface QuizService {
    QuizDto createQuiz(CreateQuizRequest request);

    QuizDto updateQuiz(UUID quizId, UpdateQuizRequest request);

    QuizDto updateStatus(UUID quizId, QuizStatusUpdateRequest request);

    QuizDto getQuiz(UUID quizId);

    List<TeacherQuestionDto> getQuestions(UUID quizId);

    TeacherQuestionDto addQuestion(UUID quizId, CreateQuestionRequest request);

    TeacherQuestionDto updateQuestion(UUID quizId, UUID questionId, UpdateQuestionRequest request);

    void deleteQuestion(UUID quizId, UUID questionId);
}
