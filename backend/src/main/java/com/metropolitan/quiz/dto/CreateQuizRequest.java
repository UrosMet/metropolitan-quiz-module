package com.metropolitan.quiz.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.List;

public record CreateQuizRequest(

        @NotBlank(message = "Quiz title is required")
        @Size(max = 200, message = "Quiz title must be at most 200 characters")
        String title,

        String description,

        @NotNull(message = "opensAt is required")
        OffsetDateTime opensAt,

        @NotNull(message = "closesAt is required")
        OffsetDateTime closesAt,

        @Valid
        List<CreateQuestionRequest> questions
) {
}