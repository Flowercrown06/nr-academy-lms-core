package com.nracademy.backend.service.impl;

import com.nracademy.backend.dto.request.SaveQuizAnswersRequest;
import com.nracademy.backend.dto.response.QuizAttemptResultDto;
import com.nracademy.backend.dto.response.StartQuizAttemptResponse;
import com.nracademy.backend.entity.enums.QuizAttemptStatus;
import com.nracademy.backend.entity.enums.QuizStatus;
import com.nracademy.backend.entity.quiz.Quiz;
import com.nracademy.backend.entity.quiz.QuizAnswer;
import com.nracademy.backend.entity.quiz.QuizAttempt;
import com.nracademy.backend.entity.quiz.QuizQuestion;
import com.nracademy.backend.entity.user.User;
import com.nracademy.backend.exception.common.QuizAnswerQuestionMismatchException;
import com.nracademy.backend.exception.common.QuizAttemptAlreadyStartedException;
import com.nracademy.backend.exception.common.QuizAttemptAlreadySubmittedException;
import com.nracademy.backend.exception.common.QuizAttemptExpiredException;
import com.nracademy.backend.exception.common.QuizAttemptNotFoundException;
import com.nracademy.backend.exception.common.QuizAttemptNotInProgressException;
import com.nracademy.backend.exception.common.QuizNotAssignedToStudentException;
import com.nracademy.backend.exception.common.QuizNotAvailableException;
import com.nracademy.backend.exception.common.QuizNotFoundException;
import com.nracademy.backend.exception.common.QuizNotPublishedException;
import com.nracademy.backend.exception.common.QuizQuestionOptionNotFoundException;
import com.nracademy.backend.mapper.QuizMapper;
import com.nracademy.backend.repository.GroupStudentRepository;
import com.nracademy.backend.repository.QuizAnswerRepository;
import com.nracademy.backend.repository.QuizAttemptRepository;
import com.nracademy.backend.repository.QuizOptionRepository;
import com.nracademy.backend.repository.QuizQuestionRepository;
import com.nracademy.backend.repository.QuizRepository;
import com.nracademy.backend.service.QuizAttemptService;
import com.nracademy.backend.service.QuizGradingService;
import com.nracademy.backend.tenant.TenantGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuizAttemptServiceImpl implements QuizAttemptService {

    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizOptionRepository quizOptionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    private final GroupStudentRepository groupStudentRepository;
    private final QuizGradingService quizGradingService;
    private final QuizMapper quizMapper;
    private final TenantGuard tenantGuard;

    @Transactional
    @Override
    public StartQuizAttemptResponse startAttempt(UUID quizId) {
        User student = tenantGuard.requireAuthenticatedUser();
        UUID courseId = student.getCourseId();

        Quiz quiz = quizRepository.findByCourseIdAndId(courseId, quizId)
                .orElseThrow(() -> new QuizNotFoundException(quizId));

        if (quiz.getStatus() != QuizStatus.PUBLISHED) {
            throw new QuizNotPublishedException();
        }

        validateAvailabilityWindow(quiz);

        boolean belongsToGroup = groupStudentRepository
                .existsByCourseIdAndGroupIdAndStudentId(courseId, quiz.getGroupId(), student.getId());
        if (!belongsToGroup) {
            throw new QuizNotAssignedToStudentException();
        }

        quizAttemptRepository
                .findByCourseIdAndQuizIdAndStudentIdAndStatus(
                        courseId, quizId, student.getId(), QuizAttemptStatus.IN_PROGRESS)
                .ifPresent(existing -> {
                    throw new QuizAttemptAlreadyStartedException();
                });

        List<QuizQuestion> questions = quizQuestionRepository
                .findByCourseIdAndQuizIdOrderBySortOrderAsc(courseId, quizId);

        int nextAttemptNo = quizAttemptRepository
                .findByCourseIdAndQuizIdAndStudentIdOrderByAttemptNoAsc(courseId, quizId, student.getId())
                .size() + 1;

        LocalDateTime startedAt = LocalDateTime.now();
        QuizAttempt attempt = QuizAttempt.builder()
                .courseId(courseId)
                .quizId(quizId)
                .studentId(student.getId())
                .attemptNo(nextAttemptNo)
                .status(QuizAttemptStatus.IN_PROGRESS)
                .startedAt(startedAt)
                .expiresAt(startedAt.plusMinutes(quiz.getDurationMinutes()))
                .build();
        attempt = quizAttemptRepository.save(attempt);

        List<com.nracademy.backend.dto.response.StudentQuestionDto> questionDtos = questions.stream()
                .map(q -> quizMapper.toStudentQuestion(
                        q, quizOptionRepository.findByCourseIdAndQuestionIdOrderBySortOrderAsc(courseId, q.getId())))
                .toList();

        return StartQuizAttemptResponse.builder()
                .attemptId(attempt.getId())
                .quizId(quizId)
                .startedAt(attempt.getStartedAt())
                .expiresAt(attempt.getExpiresAt())
                .questions(questionDtos)
                .build();
    }

    @Transactional
    @Override
    public void saveAnswers(UUID attemptId, SaveQuizAnswersRequest request) {
        User student = tenantGuard.requireAuthenticatedUser();
        UUID courseId = student.getCourseId();

        QuizAttempt attempt = requireOwnAttempt(courseId, student.getId(), attemptId);
        requireWritable(attempt);

        List<QuizQuestion> questions = quizQuestionRepository
                .findByCourseIdAndQuizIdOrderBySortOrderAsc(courseId, attempt.getQuizId());
        List<UUID> validQuestionIds = questions.stream().map(QuizQuestion::getId).toList();

        for (var answerRequest : request.getAnswers()) {
            if (!validQuestionIds.contains(answerRequest.getQuestionId())) {
                throw new QuizAnswerQuestionMismatchException();
            }

            if (answerRequest.getSelectedOptionId() != null) {
                boolean optionBelongsToQuestion = quizOptionRepository
                        .findByCourseIdAndQuestionIdOrderBySortOrderAsc(courseId, answerRequest.getQuestionId())
                        .stream()
                        .anyMatch(o -> o.getId().equals(answerRequest.getSelectedOptionId()));
                if (!optionBelongsToQuestion) {
                    throw new QuizQuestionOptionNotFoundException(answerRequest.getSelectedOptionId());
                }
            }

            QuizAnswer answer = quizAnswerRepository
                    .findByCourseIdAndAttemptIdAndQuestionId(courseId, attemptId, answerRequest.getQuestionId())
                    .orElseGet(() -> QuizAnswer.builder()
                            .courseId(courseId)
                            .attemptId(attemptId)
                            .questionId(answerRequest.getQuestionId())
                            .build());

            answer.setSelectedOptionId(answerRequest.getSelectedOptionId());
            quizAnswerRepository.save(answer);
        }
    }

    @Transactional
    @Override
    public QuizAttemptResultDto submitAttempt(UUID attemptId) {
        User student = tenantGuard.requireAuthenticatedUser();
        UUID courseId = student.getCourseId();

        QuizAttempt attempt = requireOwnAttempt(courseId, student.getId(), attemptId);

        if (attempt.getStatus() != QuizAttemptStatus.IN_PROGRESS) {
            throw new QuizAttemptAlreadySubmittedException();
        }

        List<QuizQuestion> questions = quizQuestionRepository
                .findByCourseIdAndQuizIdOrderBySortOrderAsc(courseId, attempt.getQuizId());

        LocalDateTime now = LocalDateTime.now();
        boolean expired = now.isAfter(attempt.getExpiresAt());

        QuizAttemptStatus finalStatus = expired ? QuizAttemptStatus.AUTO_SUBMITTED : QuizAttemptStatus.SUBMITTED;

        quizGradingService.grade(attempt, questions, finalStatus, now);
        quizAttemptRepository.save(attempt);

        return toResultDto(attempt);
    }

    @Transactional(readOnly = true)
    @Override
    public QuizAttemptResultDto getResult(UUID attemptId) {
        User student = tenantGuard.requireAuthenticatedUser();
        QuizAttempt attempt = requireOwnAttempt(student.getCourseId(), student.getId(), attemptId);
        return toResultDto(attempt);
    }


    private QuizAttempt requireOwnAttempt(UUID courseId, UUID studentId, UUID attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findByCourseIdAndId(courseId, attemptId)
                .orElseThrow(() -> new QuizAttemptNotFoundException(attemptId));

        if (!attempt.getStudentId().equals(studentId)) {
            throw new QuizAttemptNotFoundException(attemptId);
        }
        return attempt;
    }

    private void requireWritable(QuizAttempt attempt) {
        if (attempt.getStatus() != QuizAttemptStatus.IN_PROGRESS) {
            throw new QuizAttemptNotInProgressException();
        }
        if (LocalDateTime.now().isAfter(attempt.getExpiresAt())) {
            throw new QuizAttemptExpiredException();
        }
    }

    private void validateAvailabilityWindow(Quiz quiz) {
        LocalDateTime now = LocalDateTime.now();
        if (quiz.getAvailableFrom() != null && now.isBefore(quiz.getAvailableFrom())) {
            throw new QuizNotAvailableException();
        }
        if (quiz.getAvailableUntil() != null && now.isAfter(quiz.getAvailableUntil())) {
            throw new QuizNotAvailableException();
        }
    }

    private QuizAttemptResultDto toResultDto(QuizAttempt attempt) {
        return QuizAttemptResultDto.builder()
                .attemptId(attempt.getId())
                .status(attempt.getStatus())
                .score(attempt.getScore())
                .correctCount(attempt.getCorrectCount())
                .totalQuestions(attempt.getTotalQuestions())
                .submittedAt(attempt.getSubmittedAt())
                .build();
    }
}