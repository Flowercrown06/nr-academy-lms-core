package com.nracademy.backend.controller;

import com.nracademy.backend.dto.request.SaveQuizAnswersRequest;
import com.nracademy.backend.dto.response.QuizAttemptResultDto;
import com.nracademy.backend.dto.response.StartQuizAttemptResponse;
import com.nracademy.backend.service.QuizAttemptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentQuizController {

    private final QuizAttemptService quizAttemptService;

    @PostMapping("/quizzes/{quizId}/attempts")
    public ResponseEntity<StartQuizAttemptResponse> startAttempt(@PathVariable UUID quizId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quizAttemptService.startAttempt(quizId));
    }

    @PutMapping("/quiz-attempts/{attemptId}/answers")
    public ResponseEntity<Void> saveAnswers(
            @PathVariable UUID attemptId,
            @Valid @RequestBody SaveQuizAnswersRequest request) {
        quizAttemptService.saveAnswers(attemptId, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/quiz-attempts/{attemptId}/submit")
    public ResponseEntity<QuizAttemptResultDto> submitAttempt(@PathVariable UUID attemptId) {
        return ResponseEntity.ok(quizAttemptService.submitAttempt(attemptId));
    }

    @GetMapping("/quiz-attempts/{attemptId}/result")
    public ResponseEntity<QuizAttemptResultDto> getResult(@PathVariable UUID attemptId) {
        return ResponseEntity.ok(quizAttemptService.getResult(attemptId));
    }
}