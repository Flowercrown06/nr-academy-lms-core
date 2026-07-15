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
import com.nracademy.backend.entity.User;
import com.nracademy.backend.exception.common.QuizQuestionHasNoCorrectOptionException;
import com.nracademy.backend.exception.common.QuizQuestionNotFoundException;
import com.nracademy.backend.exception.common.QuizNotFoundException;
import com.nracademy.backend.exception.common.QuizStatusTransitionInvalidException;
import com.nracademy.backend.exception.common.RoleForbiddenException;
import com.nracademy.backend.mapper.QuizMapper;
import com.nracademy.backend.repository.QuizOptionRepository;
import com.nracademy.backend.repository.QuizQuestionRepository;
import com.nracademy.backend.repository.QuizRepository;
import com.nracademy.backend.specification.QuizSpecifications;
import com.nracademy.backend.tenant.TenantGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizOptionRepository quizOptionRepository;
    private final QuizMapper quizMapper;
    private final TenantGuard tenantGuard;

    private static final Set<Role> QUIZ_MANAGER_ROLES = Set.of(Role.COURSE_OWNER, Role.TEACHER);

    private static final java.util.Map<QuizStatus, Set<QuizStatus>> ALLOWED_TRANSITIONS = java.util.Map.of(
            QuizStatus.DRAFT, Set.of(QuizStatus.PUBLISHED),
            QuizStatus.PUBLISHED, Set.of(QuizStatus.CLOSED),
            QuizStatus.CLOSED, Set.of()
    );
    @Transactional
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
    public Page<QuizDto> listQuizzes(
            UUID groupId,
            QuizStatus status,
            String q,
            LocalDateTime availableFrom,
            LocalDateTime availableTo,
            LocalDateTime createdFrom,
            LocalDateTime createdTo,
            Pageable pageable) {

        User user = requireQuizManager();

        Specification<Quiz> spec = QuizSpecifications
                .withFilters(user.getCourseId(), groupId, status, q, availableFrom, availableTo, createdFrom, createdTo);

        if (user.getRole() == Role.TEACHER) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("teacherId"), user.getId()));
        }

        return quizRepository.findAll(spec, pageable)
                .map(quiz -> {
                    int questionCount = quizQuestionRepository
                            .findByCourseIdAndQuizIdOrderBySortOrderAsc(user.getCourseId(), quiz.getId()).size();
                    return quizMapper.toDto(quiz, questionCount);
                });
    }

    @Transactional(readOnly = true)
    public QuizDto getQuiz(UUID quizId) {
        User user = requireQuizManager();
        Quiz quiz = requireOwnedQuiz(user, quizId);
        int questionCount = quizQuestionRepository
                .findByCourseIdAndQuizIdOrderBySortOrderAsc(user.getCourseId(), quizId).size();
        return quizMapper.toDto(quiz, questionCount);
    }

    @Transactional(readOnly = true)
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
    public TeacherQuestionDto addQuestion(UUID quizId, CreateQuestionRequest request) {
        User user = requireQuizManager();
        Quiz quiz = requireOwnedQuiz(user, quizId);

        validateHasCorrectOption(request.getOptions());

        QuizQuestion question = QuizQuestion.builder()
                .courseId(quiz.getCourseId())
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
    public void deleteQuestion(UUID quizId, UUID questionId) {
        User user = requireQuizManager();
        Quiz quiz = requireOwnedQuiz(user, quizId);

        QuizQuestion question = quizQuestionRepository.findById(questionId)
                .filter(q -> q.getQuizId().equals(quizId) && q.getCourseId().equals(quiz.getCourseId()))
                .orElseThrow(() -> new QuizQuestionNotFoundException(questionId));

         quizQuestionRepository.delete(question);
    }


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