package com.metropolitan.quiz.dto;

import com.metropolitan.quiz.entity.QuizStatus;

import java.time.OffsetDateTime;

public record TeacherQuizSummaryResponse(
        Long id,
        String title,
        String description,
        OffsetDateTime opensAt,
        OffsetDateTime closesAt,
        QuizStatus status,
        int questionCount
) {
}