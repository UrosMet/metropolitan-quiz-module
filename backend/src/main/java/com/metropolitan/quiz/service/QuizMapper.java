package com.metropolitan.quiz.service;

import com.metropolitan.quiz.dto.AnswerOptionResponse;
import com.metropolitan.quiz.dto.QuestionResponse;
import com.metropolitan.quiz.dto.QuizResponse;
import com.metropolitan.quiz.dto.QuizSummaryResponse;
import com.metropolitan.quiz.entity.AnswerOption;
import com.metropolitan.quiz.entity.Question;
import com.metropolitan.quiz.entity.Quiz;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
public class QuizMapper {

    public QuizResponse toResponse(Quiz quiz) {
        return new QuizResponse(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getOpensAt(),
                quiz.getClosesAt(),
                quiz.getStatus(),
                quiz.getQuestions()
                        .stream()
                        .sorted(Comparator.comparing(Question::getPosition))
                        .map(this::toQuestionResponse)
                        .toList()
        );
    }

    private QuestionResponse toQuestionResponse(Question question) {
        return new QuestionResponse(
                question.getId(),
                question.getText(),
                question.getPoints(),
                question.getType(),
                question.getPosition(),
                question.getOptions()
                        .stream()
                        .sorted(Comparator.comparing(AnswerOption::getPosition))
                        .map(this::toAnswerOptionResponse)
                        .toList()
        );
    }

    private AnswerOptionResponse toAnswerOptionResponse(AnswerOption option) {
        return new AnswerOptionResponse(
                option.getId(),
                option.getText(),
                option.getPosition()
        );
    }

    public QuizSummaryResponse toSummaryResponse(Quiz quiz) {
        return new QuizSummaryResponse(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getOpensAt(),
                quiz.getClosesAt()
        );
    }
}