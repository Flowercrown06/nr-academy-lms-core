package com.nracademy.backend.service.impl;

import com.nracademy.backend.entity.enums.QuizAttemptStatus;
import com.nracademy.backend.entity.quiz.QuizAnswer;
import com.nracademy.backend.entity.quiz.QuizAttempt;
import com.nracademy.backend.entity.quiz.QuizOption;
import com.nracademy.backend.entity.quiz.QuizQuestion;
import com.nracademy.backend.repository.QuizAnswerRepository;
import com.nracademy.backend.repository.QuizOptionRepository;
import com.nracademy.backend.service.QuizGradingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Pure grading logic for quiz attempts.
 *
 * Deliberately separated from QuizAttemptService: this method is called from
 * two different places -
 *   1) the student's own "submit" endpoint (QuizAttemptService)
 *   2) the scheduled job that force-submits attempts past their expiresAt
 * Keeping grading in one place means both callers always score identically,
 * and if the scoring rule ever changes, there is exactly one method to update.
 *
 * Per doc 12.8: this method finalizes and stores score/correctCount/totalQuestions
 * as a permanent snapshot on the attempt and on each answer. It never re-reads
 * "live" quiz/question data on a later date to recompute a past attempt -
 * whatever QuizQuestion/QuizOption rows exist AT THE MOMENT grading runs are
 * what get used, once, and then the numbers are frozen in the DB.
 */
@Service
@RequiredArgsConstructor
public class QuizGradingServiceImpl implements QuizGradingService {

    private final QuizAnswerRepository quizAnswerRepository;
    private final QuizOptionRepository quizOptionRepository;

    /**
     * Grades an attempt and mutates it in place (status, score, correctCount,
     * totalQuestions, submittedAt). Does not save/flush - the caller's
     * transaction is responsible for persisting.
     *
     * @param attempt        the attempt being graded (must currently be IN_PROGRESS)
     * @param questions      the quiz's questions AS THEY EXIST RIGHT NOW (attempt-time snapshot)
     * @param finalStatus    SUBMITTED (student clicked submit) or AUTO_SUBMITTED (expired)
     * @param submittedAt    the timestamp to record as submission time
     */
    @Override
    public void grade(QuizAttempt attempt,
                      List<QuizQuestion> questions,
                      QuizAttemptStatus finalStatus,
                      LocalDateTime submittedAt) {

        List<QuizAnswer> answers = quizAnswerRepository.findByCourseIdAndAttemptId(
                attempt.getCourseId(), attempt.getId());

        // Index answers by questionId for O(1) lookup per question.
        Map<UUID, QuizAnswer> answersByQuestion = answers.stream()
                .collect(Collectors.toMap(QuizAnswer::getQuestionId, a -> a));

        BigDecimal totalScore = BigDecimal.ZERO;
        int correctCount = 0;

        for (QuizQuestion question : questions) {
            QuizAnswer answer = answersByQuestion.get(question.getId());

            // Student never answered this question at all: 0 points, not correct.
            if (answer == null || answer.getSelectedOptionId() == null) {
                if (answer != null) {
                    answer.setCorrect(false);
                    answer.setEarnedPoints(BigDecimal.ZERO);
                }
                continue;
            }

            boolean isCorrect = isSelectedOptionCorrect(attempt.getCourseId(), question, answer.getSelectedOptionId());

            BigDecimal earned = isCorrect ? question.getPoints() : BigDecimal.ZERO;
            answer.setCorrect(isCorrect);
            answer.setEarnedPoints(earned);

            totalScore = totalScore.add(earned);
            if (isCorrect) {
                correctCount++;
            }
        }

        attempt.setScore(totalScore);
        attempt.setCorrectCount(correctCount);
        attempt.setTotalQuestions(questions.size());
        attempt.setStatus(finalStatus);
        attempt.setSubmittedAt(submittedAt);
    }

    /**
     * Looks up whether the selected option is the correct one for this question.
     * We re-fetch the option's `correct` flag at grading time (attempt-time
     * snapshot of the question set), not at answer-save time, because a
     * student might select an answer minutes before submitting - grading
     * always happens once, at the end, against the question set that exists
     * when grading runs.
     */
    private boolean isSelectedOptionCorrect(UUID courseId, QuizQuestion question, UUID selectedOptionId) {
        List<QuizOption> options = quizOptionRepository
                .findByCourseIdAndQuestionIdOrderBySortOrderAsc(courseId, question.getId());

        return options.stream()
                .filter(o -> o.getId().equals(selectedOptionId))
                .findFirst()
                .map(QuizOption::getCorrect)
                .orElse(false);
    }
}