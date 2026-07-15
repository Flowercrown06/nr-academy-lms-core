package com.nracademy.backend.service;

import com.nracademy.backend.entity.enums.QuizAttemptStatus;
import com.nracademy.backend.entity.quiz.QuizAttempt;
import com.nracademy.backend.entity.quiz.QuizQuestion;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Pure grading logic for quiz attempts.
 *
 * Deliberately separated from QuizAttemptService: this method is called from
 * two different places -
 *   1) the student's own "submit" endpoint (QuizAttemptService)
 *   2) the scheduled job that force-submits attempts past their expiresAt
 * Keeping grading in one place means both callers always score identically,
 * and if the scoring rule ever changes, there is exactly one method to update.
 */
public interface QuizGradingService {

    /**
     * Grades an attempt and mutates it in place (status, score, correctCount,
     * totalQuestions, submittedAt). Does not save/flush - the caller's
     * transaction is responsible for persisting.
     *
     * @param attempt     the attempt being graded (must currently be IN_PROGRESS)
     * @param questions   the quiz's questions AS THEY EXIST RIGHT NOW (attempt-time snapshot)
     * @param finalStatus SUBMITTED (student clicked submit) or AUTO_SUBMITTED (expired)
     * @param submittedAt the timestamp to record as submission time
     */
    void grade(QuizAttempt attempt,
               List<QuizQuestion> questions,
               QuizAttemptStatus finalStatus,
               LocalDateTime submittedAt);
}
