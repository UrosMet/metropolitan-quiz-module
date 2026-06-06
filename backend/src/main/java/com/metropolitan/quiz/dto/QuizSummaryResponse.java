package com.metropolitan.quiz.dto;

import java.time.OffsetDateTime;

public record QuizSummaryResponse(
        Long id,
        String title,
        String description,
        OffsetDateTime opensAt,
        OffsetDateTime closesAt
) {
}