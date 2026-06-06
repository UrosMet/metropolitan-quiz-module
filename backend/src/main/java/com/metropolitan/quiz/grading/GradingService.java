package com.metropolitan.quiz.grading;

import com.metropolitan.quiz.entity.AnswerOption;
import com.metropolitan.quiz.entity.Question;
import com.metropolitan.quiz.entity.QuestionType;
import com.metropolitan.quiz.entity.Quiz;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class GradingService {

    public int calculateScore(Quiz quiz, Map<Long, Set<Long>> selectedOptionIdsByQuestionId) {
        int totalScore = 0;

        for (Question question : quiz.getQuestions()) {
            Set<Long> selectedOptionIds = selectedOptionIdsByQuestionId.getOrDefault(
                    question.getId(),
                    Set.of()
            );

            Set<Long> correctOptionIds = question.getOptions()
                    .stream()
                    .filter(AnswerOption::isCorrect)
                    .map(AnswerOption::getId)
                    .collect(Collectors.toSet());

            if (question.getType() == QuestionType.SINGLE_CHOICE) {
                if (selectedOptionIds.size() == 1 && selectedOptionIds.equals(correctOptionIds)) {
                    totalScore += question.getPoints();
                }
            }

            if (question.getType() == QuestionType.MULTIPLE_CHOICE) {
                if (selectedOptionIds.equals(correctOptionIds)) {
                    totalScore += question.getPoints();
                }
            }
        }

        return totalScore;
    }

    public int calculateMaxScore(Quiz quiz) {
        return quiz.getQuestions()
                .stream()
                .mapToInt(Question::getPoints)
                .sum();
    }
}