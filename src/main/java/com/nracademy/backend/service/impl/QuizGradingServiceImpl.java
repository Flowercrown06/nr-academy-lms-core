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

@Service
@RequiredArgsConstructor
public class QuizGradingServiceImpl implements QuizGradingService {

    private final QuizAnswerRepository quizAnswerRepository;
    private final QuizOptionRepository quizOptionRepository;

    @Override
    public void grade(QuizAttempt attempt,
                      List<QuizQuestion> questions,
                      QuizAttemptStatus finalStatus,
                      LocalDateTime submittedAt) {

        List<QuizAnswer> answers = quizAnswerRepository.findByCourseIdAndAttemptId(
                attempt.getCourseId(), attempt.getId());

        Map<UUID, QuizAnswer> answersByQuestion = answers.stream()
                .collect(Collectors.toMap(QuizAnswer::getQuestionId, a -> a));

        BigDecimal totalScore = BigDecimal.ZERO;
        int correctCount = 0;

        for (QuizQuestion question : questions) {
            QuizAnswer answer = answersByQuestion.get(question.getId());

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