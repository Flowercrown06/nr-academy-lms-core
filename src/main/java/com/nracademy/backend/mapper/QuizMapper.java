package com.nracademy.backend.mapper;

import com.nracademy.backend.dto.response.QuizDto;
import com.nracademy.backend.dto.response.StudentOptionDto;
import com.nracademy.backend.dto.response.StudentQuestionDto;
import com.nracademy.backend.dto.response.TeacherOptionDto;
import com.nracademy.backend.dto.response.TeacherQuestionDto;
import com.nracademy.backend.entity.quiz.Quiz;
import com.nracademy.backend.entity.quiz.QuizOption;
import com.nracademy.backend.entity.quiz.QuizQuestion;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Manual mapper (not MapStruct) for the quiz question/option DTOs.
 *
 * Why manual: the teacher/student split is a security boundary, not just a
 * convenience. Writing toTeacherX / toStudentX explicitly means anyone
 * reading this file can see exactly which fields cross into the student
 * response, instead of trusting an auto-generated mapping to "do the right
 * thing" implicitly. Keep this file boring and obvious on purpose.
 */
@Component
public class QuizMapper {

    public QuizDto toDto(Quiz quiz, int questionCount) {
        return QuizDto.builder()
                .id(quiz.getId())
                .courseId(quiz.getCourseId())
                .groupId(quiz.getGroupId())
                .teacherId(quiz.getTeacherId())
                .title(quiz.getTitle())
                .durationMinutes(quiz.getDurationMinutes())
                .availableFrom(quiz.getAvailableFrom())
                .availableUntil(quiz.getAvailableUntil())
                .status(quiz.getStatus())
                .questionCount(questionCount)
                .build();
    }

    // ---- Teacher / Course Owner side: includes `correct` ----

    public TeacherOptionDto toTeacherOption(QuizOption option) {
        return TeacherOptionDto.builder()
                .id(option.getId())
                .optionText(option.getOptionText())
                .correct(option.getCorrect())
                .sortOrder(option.getSortOrder())
                .build();
    }

    public TeacherQuestionDto toTeacherQuestion(QuizQuestion question, List<QuizOption> options) {
        return TeacherQuestionDto.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .points(question.getPoints())
                .sortOrder(question.getSortOrder())
                .options(options.stream().map(this::toTeacherOption).toList())
                .build();
    }

    // ---- Student side: `correct` is never read, never copied ----

    public StudentOptionDto toStudentOption(QuizOption option) {
        return StudentOptionDto.builder()
                .id(option.getId())
                .optionText(option.getOptionText())
                .build();
    }

    public StudentQuestionDto toStudentQuestion(QuizQuestion question, List<QuizOption> options) {
        return StudentQuestionDto.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .points(question.getPoints())
                .sortOrder(question.getSortOrder())
                .options(options.stream().map(this::toStudentOption).toList())
                .build();
    }
}