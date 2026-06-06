package com.metropolitan.quiz.dto;

import com.metropolitan.quiz.entity.QuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CreateQuestionRequest(

        @NotBlank(message = "Question text is required")
        String text,

        @NotNull(message = "Question points are required")
        @Positive(message = "Question points must be greater than zero")
        Integer points,

        @NotNull(message = "Question type is required")
        QuestionType type,

        @Valid
        List<CreateAnswerOptionRequest> options
) {
}