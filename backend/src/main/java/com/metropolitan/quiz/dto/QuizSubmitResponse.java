package com.metropolitan.quiz.dto;

import java.time.OffsetDateTime;

public record QuizSubmitResponse(
        Long quizId,
        Long studentId,
        Integer score,
        Integer maxScore,
        OffsetDateTime submittedAt
) {
}