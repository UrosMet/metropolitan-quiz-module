package com.metropolitan.quiz.dto;

import com.metropolitan.quiz.entity.QuizStatus;

import java.time.OffsetDateTime;
import java.util.List;

public record QuizResponse(
        Long id,
        String title,
        String description,
        OffsetDateTime opensAt,
        OffsetDateTime closesAt,
        QuizStatus status,
        List<QuestionResponse> questions
) {
}