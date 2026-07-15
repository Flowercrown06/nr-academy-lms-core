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
