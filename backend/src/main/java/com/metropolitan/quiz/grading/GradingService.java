package com.metropolitan.quiz.grading;

import com.metropolitan.quiz.entity.AnswerOption;
import com.metropolitan.quiz.entity.Question;
import com.metropolitan.quiz.entity.QuestionType;
import com.metropolitan.quiz.entity.Quiz;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GradingService {

    public BigDecimal calculateScore(Quiz quiz, Map<Long, Set<Long>> selectedOptionIdsByQuestionId) {
        BigDecimal totalScore = BigDecimal.ZERO;

        for (Question question : quiz.getQuestions()) {
            Set<Long> selectedOptionIds = selectedOptionIdsByQuestionId.getOrDefault(
                    question.getId(),
                    Set.of()
            );

            BigDecimal questionScore = calculateQuestionScore(question, selectedOptionIds);
            totalScore = totalScore.add(questionScore);
        }

        return totalScore.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateMaxScore(Quiz quiz) {
        int maxScore = quiz.getQuestions()
                .stream()
                .mapToInt(Question::getPoints)
                .sum();

        return BigDecimal.valueOf(maxScore).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateQuestionScore(Question question, Set<Long> selectedOptionIds) {
        if (selectedOptionIds == null || selectedOptionIds.isEmpty()) {
            return BigDecimal.ZERO;
        }

        Set<Long> correctOptionIds = question.getOptions()
                .stream()
                .filter(AnswerOption::isCorrect)
                .map(AnswerOption::getId)
                .collect(Collectors.toSet());

        Set<Long> wrongOptionIds = question.getOptions()
                .stream()
                .filter(option -> !option.isCorrect())
                .map(AnswerOption::getId)
                .collect(Collectors.toSet());

        boolean selectedWrongOption = selectedOptionIds
                .stream()
                .anyMatch(wrongOptionIds::contains);

        if (selectedWrongOption) {
            return BigDecimal.ZERO;
        }

        if (question.getType() == QuestionType.SINGLE_CHOICE) {
            if (selectedOptionIds.size() == 1 && selectedOptionIds.equals(correctOptionIds)) {
                return BigDecimal.valueOf(question.getPoints());
            }

            return BigDecimal.ZERO;
        }

        if (question.getType() == QuestionType.MULTIPLE_CHOICE) {
            long selectedCorrectCount = selectedOptionIds
                    .stream()
                    .filter(correctOptionIds::contains)
                    .count();

            if (selectedCorrectCount == 0) {
                return BigDecimal.ZERO;
            }

            return BigDecimal.valueOf(question.getPoints())
                    .multiply(BigDecimal.valueOf(selectedCorrectCount))
                    .divide(BigDecimal.valueOf(correctOptionIds.size()), 2, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }
}