package com.metropolitan.quiz.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record QuizResultResponse(
        Long quizId,
        String quizTitle,
        Long studentId,
        BigDecimal score,
        BigDecimal maxScore,
        OffsetDateTime submittedAt
) {
}