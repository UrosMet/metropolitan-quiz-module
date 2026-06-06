package com.metropolitan.quiz.dto;

import com.metropolitan.quiz.entity.QuestionType;

import java.util.List;

public record QuestionResponse(
        Long id,
        String text,
        Integer points,
        QuestionType type,
        Integer position,
        List<AnswerOptionResponse> options
) {
}