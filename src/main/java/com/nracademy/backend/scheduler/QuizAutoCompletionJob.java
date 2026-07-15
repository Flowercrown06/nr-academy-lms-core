package com.nracademy.backend.scheduler;

import com.nracademy.backend.entity.enums.QuizAttemptStatus;
import com.nracademy.backend.entity.quiz.QuizAttempt;
import com.nracademy.backend.entity.quiz.QuizQuestion;
import com.nracademy.backend.exception.common.QuizAutoCompletionFailedException;
import com.nracademy.backend.repository.QuizAttemptRepository;
import com.nracademy.backend.repository.QuizQuestionRepository;
import com.nracademy.backend.service.QuizGradingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class QuizAutoCompletionJob {

    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizGradingService quizGradingService;

    @Scheduled(fixedRate = 2 * 60 * 1000)
    @Transactional
    public void autoSubmitExpiredAttempts() {
        LocalDateTime now = LocalDateTime.now();

        List<QuizAttempt> expiredAttempts = quizAttemptRepository
                .findByStatusAndExpiresAtBefore(QuizAttemptStatus.IN_PROGRESS, now);

        if (expiredAttempts.isEmpty()) {
            return;
        }

        log.info("QuizAutoCompletionJob: found {} expired IN_PROGRESS attempt(s) to grade", expiredAttempts.size());

        int succeeded = 0;
        int failed = 0;

        for (QuizAttempt attempt : expiredAttempts) {
            try {
                gradeOne(attempt, now);
                succeeded++;
            } catch (Exception e) {
                // One bad attempt must never stop the rest of the batch from
                // being processed - log and move on. A single malformed row
                // shouldn't leave every other student's attempt ungraded.
                failed++;
                log.error("QuizAutoCompletionJob: failed to auto-grade attempt {}",
                        attempt.getId(), new QuizAutoCompletionFailedException());
            }
        }

        log.info("QuizAutoCompletionJob: finished batch - {} succeeded, {} failed", succeeded, failed);
    }

    private void gradeOne(QuizAttempt attempt, LocalDateTime now) {
        List<QuizQuestion> questions = quizQuestionRepository
                .findByCourseIdAndQuizIdOrderBySortOrderAsc(attempt.getCourseId(), attempt.getQuizId());

        quizGradingService.grade(attempt, questions, QuizAttemptStatus.AUTO_SUBMITTED, now);
        quizAttemptRepository.save(attempt);
    }
}