package com.nracademy.backend.controller;

import com.nracademy.backend.dto.request.SaveQuizAnswersRequest;
import com.nracademy.backend.dto.response.QuizAttemptResultDto;
import com.nracademy.backend.dto.response.QuizDto;
import com.nracademy.backend.dto.response.StartQuizAttemptResponse;
import com.nracademy.backend.entity.enums.QuizStatus;
import com.nracademy.backend.service.impl.QuizAttemptServiceImpl;
import com.nracademy.backend.util.PageRequestUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Student-facing quiz attempt endpoints.-
 * SECURITY-CRITICAL: every response here must come from StudentQuestionDto /
 * StudentOptionDto (via QuizAttemptService), never TeacherQuestionDto /
 * TeacherOptionDto. If you ever need to add an endpoint here, reuse
 * QuizAttemptService - do not call QuizService from this controller.
 */
@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentQuizController {

    private final QuizAttemptServiceImpl quizAttemptServiceImpl;

    // Doc 9: allowed sort fields for GET /api/v1/student/quizzes (doc's
    // "startAt"/"endAt" map onto our availableFrom/availableUntil columns).
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("availableFrom", "availableUntil", "title");
    private static final String DEFAULT_SORT = "availableFrom,asc";

    @GetMapping("/quizzes")
    public ResponseEntity<Page<QuizDto>> listAssignedQuizzes(
            @RequestParam(required = false) QuizStatus status,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime availableFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime availableTo,
            @RequestParam(required = false) Boolean attempted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<String> sort) {

        Pageable pageable = PageRequestUtil.create(page, size, sort, ALLOWED_SORT_FIELDS, DEFAULT_SORT);
        Page<QuizDto> result = quizAttemptServiceImpl
                .listAssignedQuizzes(status, q, availableFrom, availableTo, attempted, pageable);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/quizzes/{quizId}/attempts")
    public ResponseEntity<StartQuizAttemptResponse> startAttempt(@PathVariable UUID quizId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quizAttemptServiceImpl.startAttempt(quizId));
    }

    @PutMapping("/quiz-attempts/{attemptId}/answers")
    public ResponseEntity<Void> saveAnswers(
            @PathVariable UUID attemptId,
            @Valid @RequestBody SaveQuizAnswersRequest request) {
        quizAttemptServiceImpl.saveAnswers(attemptId, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/quiz-attempts/{attemptId}/submit")
    public ResponseEntity<QuizAttemptResultDto> submitAttempt(@PathVariable UUID attemptId) {
        return ResponseEntity.ok(quizAttemptServiceImpl.submitAttempt(attemptId));
    }

    @GetMapping("/quiz-attempts/{attemptId}/result")
    public ResponseEntity<QuizAttemptResultDto> getResult(@PathVariable UUID attemptId) {
        return ResponseEntity.ok(quizAttemptServiceImpl.getResult(attemptId));
    }
}