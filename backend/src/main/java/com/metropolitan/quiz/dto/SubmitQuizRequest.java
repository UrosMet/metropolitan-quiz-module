package com.metropolitan.quiz.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SubmitQuizRequest(

        @NotNull(message = "answers are required")
        @Valid
        List<SubmitQuestionAnswerRequest> answers
) {
}