package com.metropolitan.quiz.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateAnswerOptionRequest(

        @NotBlank(message = "Option text is required")
        String text,

        boolean correct
) {
}