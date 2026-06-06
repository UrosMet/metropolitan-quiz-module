package com.metropolitan.quiz.dto;

import java.time.OffsetDateTime;

public record QuizResultResponse(
        Long quizId,
        String quizTitle,
        Long studentId,
        Integer score,
        Integer maxScore,
        OffsetDateTime submittedAt
) {
}