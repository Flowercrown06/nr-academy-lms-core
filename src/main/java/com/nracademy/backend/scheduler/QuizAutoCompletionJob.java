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

/**
 * Background safety net for quiz attempts (doc 12.7: "Scheduled job also
 * auto-submits expired IN_PROGRESS attempts").
 *
 * Why this exists at all: QuizAttemptService.submitAttempt() only runs when
 * a student actively calls the submit endpoint. If a student closes their
 * browser, loses connection, or simply never clicks submit, their attempt
 * would stay IN_PROGRESS forever with no score - which would be wrong both
 * for the student (no result ever recorded) and for the teacher (can't see
 * who finished). This job finds every attempt across ALL tenants whose
 * expiresAt has already passed and grades them exactly the same way
 * QuizGradingService.grade() would from the manual submit path.
 *
 * Important: this class must NEVER call TenantGuard/CurrentUserService.
 * Those assume a logged-in HTTP request is in progress (they read
 * SecurityContextHolder). A scheduled job has no such request - there is no
 * "current user" here, so we query repositories directly and pass courseId
 * values straight through from the attempt rows themselves.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class QuizAutoCompletionJob {

    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizGradingService quizGradingService;

    /**
     * Runs every 2 minutes. Interval is a balance: frequent enough that a
     * student who finishes right at the deadline doesn't wait long for a
     * result, but not so frequent that it hammers the DB with a full-table
     * scan-ish query every few seconds.
     */
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