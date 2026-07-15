package com.nracademy.backend.controller;

import com.nracademy.backend.dto.request.CreateQuestionRequest;
import com.nracademy.backend.dto.request.CreateQuizRequest;
import com.nracademy.backend.dto.request.QuizStatusUpdateRequest;
import com.nracademy.backend.dto.request.UpdateQuestionRequest;
import com.nracademy.backend.dto.request.UpdateQuizRequest;
import com.nracademy.backend.dto.response.QuizDto;
import com.nracademy.backend.dto.response.TeacherQuestionDto;
import com.nracademy.backend.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PostMapping
    public ResponseEntity<QuizDto> createQuiz(@Valid @RequestBody CreateQuizRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quizService.createQuiz(request));
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<QuizDto> getQuiz(@PathVariable UUID quizId) {
        return ResponseEntity.ok(quizService.getQuiz(quizId));
    }

    @PatchMapping("/{quizId}")
    public ResponseEntity<QuizDto> updateQuiz(
            @PathVariable UUID quizId,
            @Valid @RequestBody UpdateQuizRequest request) {
        return ResponseEntity.ok(quizService.updateQuiz(quizId, request));
    }

    @PatchMapping("/{quizId}/status")
    public ResponseEntity<QuizDto> updateStatus(
            @PathVariable UUID quizId,
            @Valid @RequestBody QuizStatusUpdateRequest request) {
        return ResponseEntity.ok(quizService.updateStatus(quizId, request));
    }

    @GetMapping("/{quizId}/questions")
    public ResponseEntity<List<TeacherQuestionDto>> getQuestions(@PathVariable UUID quizId) {
        return ResponseEntity.ok(quizService.getQuestions(quizId));
    }

    @PostMapping("/{quizId}/questions")
    public ResponseEntity<TeacherQuestionDto> addQuestion(
            @PathVariable UUID quizId,
            @Valid @RequestBody CreateQuestionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quizService.addQuestion(quizId, request));
    }

    @PatchMapping("/{quizId}/questions/{questionId}")
    public ResponseEntity<TeacherQuestionDto> updateQuestion(
            @PathVariable UUID quizId,
            @PathVariable UUID questionId,
            @Valid @RequestBody UpdateQuestionRequest request) {
        return ResponseEntity.ok(quizService.updateQuestion(quizId, questionId, request));
    }

    @DeleteMapping("/{quizId}/questions/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable UUID quizId,
            @PathVariable UUID questionId) {
        quizService.deleteQuestion(quizId, questionId);
        return ResponseEntity.noContent().build();
    }
}