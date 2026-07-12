package com.nracademy.backend.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nracademy.backend.dto.response.StudentOptionDto;
import com.nracademy.backend.dto.response.StudentQuestionDto;
import com.nracademy.backend.dto.response.TeacherOptionDto;
import com.nracademy.backend.dto.response.TeacherQuestionDto;
import com.nracademy.backend.entity.quiz.QuizOption;
import com.nracademy.backend.entity.quiz.QuizQuestion;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Doc 14.4 - "Never expose QuizOption.correct through student endpoints" and
 * "Add tests that verify student response JSON does not contain correct."
 *
 * This is a plain unit test (no Spring context, no mocks) because QuizMapper
 * has no dependencies - it's pure data transformation, which is exactly what
 * makes it possible to prove the security property directly and cheaply,
 * without spinning up a web layer.
 */
class QuizMapperTest {

    private final QuizMapper mapper = new QuizMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void toStudentOption_neverIncludesCorrectField_inFieldsOrJson() throws Exception {
        QuizOption correctOption = QuizOption.builder()
                .id(UUID.randomUUID())
                .questionId(UUID.randomUUID())
                .optionText("start()")
                .correct(true)
                .sortOrder(1)
                .build();

        StudentOptionDto dto = mapper.toStudentOption(correctOption);

        // 1) Structural check: StudentOptionDto simply has no such field to hold it.
        assertThat(fieldNames(StudentOptionDto.class)).doesNotContain("correct");

        // 2) Behavioral check: the actual serialized JSON a controller would
        // send to a student never contains the word "correct", regardless of
        // whether the source option was the correct one or not.
        String json = objectMapper.writeValueAsString(dto);
        assertThat(json).doesNotContainIgnoringCase("correct");
        assertThat(json).contains("start()");
    }

    @Test
    void toStudentQuestion_optionsList_neverLeaksCorrectFlag_evenWhenSomeOptionsAreCorrect() throws Exception {
        UUID questionId = UUID.randomUUID();

        QuizQuestion question = QuizQuestion.builder()
                .id(questionId)
                .questionText("Which method starts a new Java thread?")
                .points(new BigDecimal("1.00"))
                .sortOrder(1)
                .build();

        List<QuizOption> options = List.of(
                QuizOption.builder().id(UUID.randomUUID()).questionId(questionId)
                        .optionText("run()").correct(false).sortOrder(1).build(),
                QuizOption.builder().id(UUID.randomUUID()).questionId(questionId)
                        .optionText("start()").correct(true).sortOrder(2).build()
        );

        StudentQuestionDto dto = mapper.toStudentQuestion(question, options);
        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).doesNotContainIgnoringCase("\"correct\"");
        assertThat(json).contains("run()").contains("start()");
    }

    @Test
    void toTeacherOption_stillIncludesCorrectFlag_becauseTeacherSideIsAllowedToSeeIt() {
        // Sanity check the boundary the other way: teacher DTOs are SUPPOSED
        // to carry `correct` - this test fails loudly if that ever
        // accidentally gets stripped, which would break quiz authoring.
        QuizOption option = QuizOption.builder()
                .id(UUID.randomUUID())
                .questionId(UUID.randomUUID())
                .optionText("start()")
                .correct(true)
                .sortOrder(1)
                .build();

        TeacherOptionDto dto = mapper.toTeacherOption(option);

        assertThat(dto.getCorrect()).isTrue();
        assertThat(fieldNames(TeacherOptionDto.class)).contains("correct");
    }

    @Test
    void toTeacherQuestion_optionsCarryCorrectFlag() {
        UUID questionId = UUID.randomUUID();
        QuizQuestion question = QuizQuestion.builder()
                .id(questionId).questionText("Q").points(BigDecimal.ONE).sortOrder(1).build();

        List<QuizOption> options = List.of(
                QuizOption.builder().id(UUID.randomUUID()).questionId(questionId)
                        .optionText("A").correct(false).sortOrder(1).build(),
                QuizOption.builder().id(UUID.randomUUID()).questionId(questionId)
                        .optionText("B").correct(true).sortOrder(2).build()
        );

        TeacherQuestionDto dto = mapper.toTeacherQuestion(question, options);

        assertThat(dto.getOptions()).hasSize(2);
        assertThat(dto.getOptions().stream().filter(TeacherOptionDto::getCorrect)).hasSize(1);
    }

    private static List<String> fieldNames(Class<?> clazz) {
        return java.util.Arrays.stream(clazz.getDeclaredFields())
                .map(java.lang.reflect.Field::getName)
                .toList();
    }
}
