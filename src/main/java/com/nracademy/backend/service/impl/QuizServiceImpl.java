package com.nracademy.backend.service.impl;

import com.nracademy.backend.dto.request.CreateQuestionRequest;
import com.nracademy.backend.dto.request.CreateQuizRequest;
import com.nracademy.backend.dto.request.QuestionOptionRequest;
import com.nracademy.backend.dto.request.QuizStatusUpdateRequest;
import com.nracademy.backend.dto.request.UpdateQuestionRequest;
import com.nracademy.backend.dto.request.UpdateQuizRequest;
import com.nracademy.backend.dto.response.QuizDto;
import com.nracademy.backend.dto.response.TeacherQuestionDto;
import com.nracademy.backend.entity.enums.QuizStatus;
import com.nracademy.backend.entity.enums.Role;
import com.nracademy.backend.entity.quiz.Quiz;
import com.nracademy.backend.entity.quiz.QuizOption;
import com.nracademy.backend.entity.quiz.QuizQuestion;
import com.nracademy.backend.entity.user.User;
import com.nracademy.backend.exception.common.QuizQuestionHasNoCorrectOptionException;
import com.nracademy.backend.exception.common.QuizQuestionNotFoundException;
import com.nracademy.backend.exception.common.QuizNotFoundException;
import com.nracademy.backend.exception.common.QuizStatusTransitionInvalidException;
import com.nracademy.backend.exception.common.RoleForbiddenException;
import com.nracademy.backend.mapper.QuizMapper;
import com.nracademy.backend.repository.QuizOptionRepository;
import com.nracademy.backend.repository.QuizQuestionRepository;
import com.nracademy.backend.repository.QuizRepository;
import com.nracademy.backend.service.QuizService;
import com.nracademy.backend.tenant.TenantGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Teacher / Course Owner side of quiz management: create quizzes, manage
 * questions and options, and transition quiz status (DRAFT -> PUBLISHED -> CLOSED).
 *
 * Student-facing concerns (starting attempts, answering, grading) live in
 * QuizAttemptService and QuizGradingService instead - this class never
 * returns a student-safe DTO, only Teacher* DTOs, since everything here is
 * gated to COURSE_OWNER / TEACHER roles.
 */
@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizOptionRepository quizOptionRepository;
    private final QuizMapper quizMapper;
    private final TenantGuard tenantGuard;

    private static final Set<Role> QUIZ_MANAGER_ROLES = Set.of(Role.COURSE_OWNER, Role.TEACHER);

    // Legal status transitions. DRAFT -> PUBLISHED -> CLOSED, no going back.
    private static final java.util.Map<QuizStatus, Set<QuizStatus>> ALLOWED_TRANSITIONS = java.util.Map.of(
            QuizStatus.DRAFT, Set.of(QuizStatus.PUBLISHED),
            QuizStatus.PUBLISHED, Set.of(QuizStatus.CLOSED),
            QuizStatus.CLOSED, Set.of()
    );

    @Transactional
    @Override
    public QuizDto createQuiz(CreateQuizRequest request) {
        User user = requireQuizManager();

        Quiz quiz = Quiz.builder()
                .courseId(user.getCourseId())
                .groupId(request.getGroupId())
                .teacherId(user.getId())
                .title(request.getTitle())
                .durationMinutes(request.getDurationMinutes())
                .availableFrom(request.getAvailableFrom())
                .availableUntil(request.getAvailableUntil())
                .status(request.getStatus() != null ? request.getStatus() : QuizStatus.DRAFT)
                .build();

        quiz = quizRepository.save(quiz);
        return quizMapper.toDto(quiz, 0);
    }

    @Transactional
    @Override
    public QuizDto updateQuiz(UUID quizId, UpdateQuizRequest request) {
        User user = requireQuizManager();
        Quiz quiz = requireOwnedQuiz(user, quizId);

        if (request.getTitle() != null) {
            quiz.setTitle(request.getTitle());
        }
        if (request.getDurationMinutes() != null) {
            quiz.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getAvailableFrom() != null) {
            quiz.setAvailableFrom(request.getAvailableFrom());
        }
        if (request.getAvailableUntil() != null) {
            quiz.setAvailableUntil(request.getAvailableUntil());
        }
        if (request.getStatus() != null && request.getStatus() != quiz.getStatus()) {
            applyStatusTransition(quiz, request.getStatus());
        }

        quiz = quizRepository.save(quiz);
        int questionCount = quizQuestionRepository
                .findByCourseIdAndQuizIdOrderBySortOrderAsc(user.getCourseId(), quizId).size();
        return quizMapper.toDto(quiz, questionCount);
    }

    @Transactional
    @Override
    public QuizDto updateStatus(UUID quizId, QuizStatusUpdateRequest request) {
        User user = requireQuizManager();
        Quiz quiz = requireOwnedQuiz(user, quizId);

        applyStatusTransition(quiz, request.getStatus());
        quiz = quizRepository.save(quiz);

        int questionCount = quizQuestionRepository
                .findByCourseIdAndQuizIdOrderBySortOrderAsc(user.getCourseId(), quizId).size();
        return quizMapper.toDto(quiz, questionCount);
    }

    @Transactional(readOnly = true)
    @Override
    public QuizDto getQuiz(UUID quizId) {
        User user = requireQuizManager();
        Quiz quiz = requireOwnedQuiz(user, quizId);
        int questionCount = quizQuestionRepository
                .findByCourseIdAndQuizIdOrderBySortOrderAsc(user.getCourseId(), quizId).size();
        return quizMapper.toDto(quiz, questionCount);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TeacherQuestionDto> getQuestions(UUID quizId) {
        User user = requireQuizManager();
        requireOwnedQuiz(user, quizId);

        List<QuizQuestion> questions = quizQuestionRepository
                .findByCourseIdAndQuizIdOrderBySortOrderAsc(user.getCourseId(), quizId);

        return questions.stream()
                .map(q -> quizMapper.toTeacherQuestion(q,
                        quizOptionRepository.findByCourseIdAndQuestionIdOrderBySortOrderAsc(user.getCourseId(), q.getId())))
                .toList();
    }

    @Transactional
    @Override
    public TeacherQuestionDto addQuestion(UUID quizId, CreateQuestionRequest request) {
        User user = requireQuizManager();
        Quiz quiz = requireOwnedQuiz(user, quizId);

        validateHasCorrectOption(request.getOptions());

        QuizQuestion question = QuizQuestion.builder()
                .courseId(quiz.getCourseId())
                .quizId(quiz.getId())
                .questionText(request.getQuestionText())
                .points(request.getPoints())
                .sortOrder(request.getSortOrder())
                .build();
        QuizQuestion savedQuestion = quizQuestionRepository.save(question);

        List<QuizOption> options = request.getOptions().stream()
                .map(optionReq -> QuizOption.builder()
                        .questionId(savedQuestion.getId())
                        .optionText(optionReq.getOptionText())
                        .correct(optionReq.getCorrect())
                        .sortOrder(optionReq.getSortOrder())
                        .build())
                .map(quizOptionRepository::save)
                .toList();

        return quizMapper.toTeacherQuestion(question, options);
    }

    @Transactional
    @Override
    public TeacherQuestionDto updateQuestion(UUID quizId, UUID questionId, UpdateQuestionRequest request) {
        User user = requireQuizManager();
        Quiz quiz = requireOwnedQuiz(user, quizId);

        QuizQuestion question = quizQuestionRepository.findById(questionId)
                .filter(q -> q.getQuizId().equals(quizId) && q.getCourseId().equals(quiz.getCourseId()))
                .orElseThrow(() -> new QuizQuestionNotFoundException(questionId));

        if (request.getQuestionText() != null) {
            question.setQuestionText(request.getQuestionText());
        }
        if (request.getPoints() != null) {
            question.setPoints(request.getPoints());
        }
        if (request.getSortOrder() != null) {
            question.setSortOrder(request.getSortOrder());
        }
        question = quizQuestionRepository.save(question);

        List<QuizOption> options;
        if (request.getOptions() != null) {
            validateHasCorrectOption(request.getOptions());

            // Replace-all strategy: simplest correct approach for a small
            // per-question option list. Delete existing options for this
            // question, then re-insert the submitted set. Avoids having to
            // diff/match old vs new options by id.
            List<QuizOption> existing = quizOptionRepository
                    .findByCourseIdAndQuestionIdOrderBySortOrderAsc(quiz.getCourseId(), question.getId());
            quizOptionRepository.deleteAll(existing);

            QuizQuestion finalQuestion = question;
            options = request.getOptions().stream()
                    .map(optionReq -> QuizOption.builder()
                            .courseId(quiz.getCourseId())
                            .questionId(finalQuestion.getId())
                            .optionText(optionReq.getOptionText())
                            .correct(optionReq.getCorrect())
                            .sortOrder(optionReq.getSortOrder())
                            .build())
                    .map(quizOptionRepository::save)
                    .toList();
        } else {
            options = quizOptionRepository
                    .findByCourseIdAndQuestionIdOrderBySortOrderAsc(quiz.getCourseId(), question.getId());
        }

        return quizMapper.toTeacherQuestion(question, options);
    }

    @Transactional
    @Override
    public void deleteQuestion(UUID quizId, UUID questionId) {
        User user = requireQuizManager();
        Quiz quiz = requireOwnedQuiz(user, quizId);

        QuizQuestion question = quizQuestionRepository.findById(questionId)
                .filter(q -> q.getQuizId().equals(quizId) && q.getCourseId().equals(quiz.getCourseId()))
                .orElseThrow(() -> new QuizQuestionNotFoundException(questionId));

        // Options cascade-delete at the DB level (fk_quiz_options_question ON DELETE CASCADE).
        quizQuestionRepository.delete(question);
    }

    // ---- helpers ----

    private User requireQuizManager() {
        User user = tenantGuard.requireAuthenticatedUser();
        if (!QUIZ_MANAGER_ROLES.contains(user.getRole())) {
            throw new RoleForbiddenException("Only Course Owners and Teachers can manage quizzes.");
        }
        return user;
    }

    private Quiz requireOwnedQuiz(User user, UUID quizId) {
        Quiz quiz = quizRepository.findByCourseIdAndId(user.getCourseId(), quizId)
                .orElseThrow(() -> new QuizNotFoundException(quizId));

        // A TEACHER may only manage quizzes they created; a COURSE_OWNER can
        // manage any quiz within their own tenant (their whole course).
        if (user.getRole() == Role.TEACHER && !quiz.getTeacherId().equals(user.getId())) {
            throw new RoleForbiddenException("You can only manage quizzes you created.");
        }
        return quiz;
    }

    private void applyStatusTransition(Quiz quiz, QuizStatus target) {
        Set<QuizStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(quiz.getStatus(), Set.of());
        if (!allowed.contains(target)) {
            throw new QuizStatusTransitionInvalidException(quiz.getStatus().name(), target.name());
        }
        quiz.setStatus(target);
    }

    private void validateHasCorrectOption(List<QuestionOptionRequest> options) {
        boolean hasCorrect = options.stream().anyMatch(QuestionOptionRequest::getCorrect);
        if (!hasCorrect) {
            throw new QuizQuestionHasNoCorrectOptionException();
        }
    }
}