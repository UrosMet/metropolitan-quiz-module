package com.metropolitan.quiz.dto;

public record AnswerOptionResponse(
        Long id,
        String text,
        Integer position
) {
}