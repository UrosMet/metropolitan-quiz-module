package com.metropolitan.quiz.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SubmitQuestionAnswerRequest(

        @NotNull(message = "questionId is required")
        Long questionId,

        List<Long> optionIds
) {
}