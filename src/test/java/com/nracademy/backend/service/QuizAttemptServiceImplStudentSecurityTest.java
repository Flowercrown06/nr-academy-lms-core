package com.nracademy.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nracademy.backend.dto.response.StartQuizAttemptResponse;
import com.nracademy.backend.entity.enums.QuizStatus;
import com.nracademy.backend.entity.enums.Role;
import com.nracademy.backend.entity.quiz.Quiz;
import com.nracademy.backend.entity.quiz.QuizOption;
import com.nracademy.backend.entity.quiz.QuizQuestion;
import com.nracademy.backend.entity.user.User;
import com.nracademy.backend.mapper.QuizMapper;
import com.nracademy.backend.repository.GroupStudentRepository;
import com.nracademy.backend.repository.QuizAnswerRepository;
import com.nracademy.backend.repository.QuizAttemptRepository;
import com.nracademy.backend.repository.QuizOptionRepository;
import com.nracademy.backend.repository.QuizQuestionRepository;
import com.nracademy.backend.repository.QuizRepository;
import com.nracademy.backend.service.impl.QuizAttemptServiceImpl;
import com.nracademy.backend.service.impl.QuizGradingService;
import com.nracademy.backend.tenant.TenantGuard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Doc 14.4 - "Never expose QuizOption.correct through student endpoints" and
 * "Add tests that verify student response JSON does not contain correct."
 *
 * Unlike QuizMapperTest (which tests the mapper in isolation), this test
 * drives the real QuizAttemptService.startAttempt() method end to end with
 * mocked repositories, then serializes its ACTUAL return type
 * (StartQuizAttemptResponse) to JSON exactly as a controller response would
 * be - proving the whole call path, not just the mapper step, keeps
 * `correct` out of what a student receives over HTTP.
 */
@ExtendWith(MockitoExtension.class)
class QuizAttemptServiceImplStudentSecurityTest {

    @Mock private QuizRepository quizRepository;
    @Mock private QuizQuestionRepository quizQuestionRepository;
    @Mock private QuizOptionRepository quizOptionRepository;
    @Mock private QuizAttemptRepository quizAttemptRepository;
    @Mock private QuizAnswerRepository quizAnswerRepository;
    @Mock private GroupStudentRepository groupStudentRepository;
    @Mock private QuizGradingService quizGradingService;
    @Mock private TenantGuard tenantGuard;

    private final QuizMapper quizMapper = new QuizMapper();
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private QuizAttemptServiceImpl quizAttemptServiceImpl;

    private UUID courseId;
    private UUID groupId;
    private UUID quizId;
    private UUID studentId;
    private UUID questionId;

    @BeforeEach
    void setUp() {
        quizAttemptServiceImpl = new QuizAttemptServiceImpl(
                quizRepository, quizQuestionRepository, quizOptionRepository,
                quizAttemptRepository, quizAnswerRepository, groupStudentRepository,
                quizGradingService, quizMapper, tenantGuard);

        courseId = UUID.randomUUID();
        groupId = UUID.randomUUID();
        quizId = UUID.randomUUID();
        studentId = UUID.randomUUID();
        questionId = UUID.randomUUID();
    }

    @Test
    void startAttempt_responseJson_neverContainsCorrectField() throws Exception {
        User student = User.builder()
                .id(studentId)
                .courseId(courseId)
                .role(Role.STUDENT)
                .build();

        Quiz quiz = Quiz.builder()
                .id(quizId)
                .courseId(courseId)
                .groupId(groupId)
                .teacherId(UUID.randomUUID())
                .title("Java Threads Exam")
                .durationMinutes(45)
                .status(QuizStatus.PUBLISHED)
                .build();

        QuizQuestion question = QuizQuestion.builder()
                .id(questionId)
                .courseId(courseId)
                .quizId(quizId)
                .questionText("Which method starts a new Java thread?")
                .points(new BigDecimal("1.00"))
                .sortOrder(1)
                .build();

        List<QuizOption> options = List.of(
                QuizOption.builder().id(UUID.randomUUID()).courseId(courseId).questionId(questionId)
                        .optionText("run()").correct(false).sortOrder(1).build(),
                QuizOption.builder().id(UUID.randomUUID()).courseId(courseId).questionId(questionId)
                        .optionText("start()").correct(true).sortOrder(2).build()
        );

        when(tenantGuard.requireAuthenticatedUser()).thenReturn(student);
        when(quizRepository.findByCourseIdAndId(courseId, quizId)).thenReturn(Optional.of(quiz));
        when(groupStudentRepository.existsByCourseIdAndGroupIdAndStudentId(courseId, groupId, studentId))
                .thenReturn(true);
        when(quizAttemptRepository.findByCourseIdAndQuizIdAndStudentIdAndStatus(
                eq(courseId), eq(quizId), eq(studentId), any()))
                .thenReturn(Optional.empty());
        when(quizQuestionRepository.findByCourseIdAndQuizIdOrderBySortOrderAsc(courseId, quizId))
                .thenReturn(List.of(question));
        when(quizOptionRepository.findByCourseIdAndQuestionIdOrderBySortOrderAsc(courseId, questionId))
                .thenReturn(options);
        // save() just echoes back what's passed in, like a real repository would on insert.
        when(quizAttemptRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(quizAttemptRepository.findByCourseIdAndQuizIdAndStudentIdOrderByAttemptNoAsc(courseId, quizId, studentId))
                .thenReturn(List.of());

        StartQuizAttemptResponse response = quizAttemptServiceImpl.startAttempt(quizId);

        // 1) Structural: the DTO tree only contains StudentQuestionDto/StudentOptionDto.
        assertThat(response.getQuestions()).hasSize(1);
        assertThat(response.getQuestions().get(0).getOptions()).hasSize(2);

        // 2) Behavioral: serialize exactly what the controller would send back
        // to the student over HTTP, and assert `correct` is nowhere in it -
        // even though the underlying entities DO have a correct option.
        String json = objectMapper.writeValueAsString(response);
        assertThat(json).doesNotContainIgnoringCase("\"correct\"");
        assertThat(json).contains("run()").contains("start()");
    }
}
