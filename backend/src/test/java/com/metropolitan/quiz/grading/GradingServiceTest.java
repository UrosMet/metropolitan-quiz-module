package com.metropolitan.quiz.grading;

import com.metropolitan.quiz.entity.*;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GradingServiceTest {

    private final GradingService gradingService = new GradingService();

    @Test
    void shouldGiveFullPointsForCorrectSingleChoiceAnswer() {
        Quiz quiz = createQuiz();

        Question question = createQuestion(1L, "Koja anotacija pokreće Spring Boot aplikaciju?", 5, QuestionType.SINGLE_CHOICE, 1);
        question.addOption(createOption(10L, "@SpringBootApplication", true, 1));
        question.addOption(createOption(11L, "@Entity", false, 2));

        quiz.addQuestion(question);

        var score = gradingService.calculateScore(
                quiz,
                Map.of(1L, Set.of(10L))
        );

        assertThat(score).isEqualByComparingTo("5.00");
    }

    @Test
    void shouldGiveZeroPointsForWrongSingleChoiceAnswer() {
        Quiz quiz = createQuiz();

        Question question = createQuestion(1L, "Koja anotacija pokreće Spring Boot aplikaciju?", 5, QuestionType.SINGLE_CHOICE, 1);
        question.addOption(createOption(10L, "@SpringBootApplication", true, 1));
        question.addOption(createOption(11L, "@Entity", false, 2));

        quiz.addQuestion(question);

        var score = gradingService.calculateScore(
                quiz,
                Map.of(1L, Set.of(11L))
        );

        assertThat(score).isEqualByComparingTo("0.00");
    }

    @Test
    void shouldGiveZeroPointsForUnansweredSingleChoiceQuestion() {
        Quiz quiz = createQuiz();

        Question question = createQuestion(1L, "Koja anotacija pokreće Spring Boot aplikaciju?", 5, QuestionType.SINGLE_CHOICE, 1);
        question.addOption(createOption(10L, "@SpringBootApplication", true, 1));
        question.addOption(createOption(11L, "@Entity", false, 2));

        quiz.addQuestion(question);

        var score = gradingService.calculateScore(
                quiz,
                Map.of()
        );

        assertThat(score).isEqualByComparingTo("0.00");
    }

    @Test
    void shouldGiveFullPointsForCompletelyCorrectMultipleChoiceAnswer() {
        Quiz quiz = createQuiz();

        Question question = createQuestion(1L, "Koje tehnologije se često koriste uz Spring Boot?", 10, QuestionType.MULTIPLE_CHOICE, 1);
        question.addOption(createOption(10L, "Spring Data JPA", true, 1));
        question.addOption(createOption(11L, "Hibernate", true, 2));
        question.addOption(createOption(12L, "Photoshop", false, 3));

        quiz.addQuestion(question);

        var score = gradingService.calculateScore(
                quiz,
                Map.of(1L, Set.of(10L, 11L))
        );

        assertThat(score).isEqualByComparingTo("10.00");
    }

    @Test
    void shouldGivePartialPointsForPartiallyCorrectMultipleChoiceAnswer() {
        Quiz quiz = createQuiz();

        Question question = createQuestion(1L, "Koje tehnologije se često koriste uz Spring Boot?", 10, QuestionType.MULTIPLE_CHOICE, 1);
        question.addOption(createOption(10L, "Spring Data JPA", true, 1));
        question.addOption(createOption(11L, "Hibernate", true, 2));
        question.addOption(createOption(12L, "Photoshop", false, 3));

        quiz.addQuestion(question);

        var score = gradingService.calculateScore(
                quiz,
                Map.of(1L, Set.of(10L))
        );

        assertThat(score).isEqualByComparingTo("5.00");
    }

    @Test
    void shouldGiveZeroPointsWhenMultipleChoiceContainsWrongOption() {
        Quiz quiz = createQuiz();

        Question question = createQuestion(1L, "Koje tehnologije se često koriste uz Spring Boot?", 10, QuestionType.MULTIPLE_CHOICE, 1);
        question.addOption(createOption(10L, "Spring Data JPA", true, 1));
        question.addOption(createOption(11L, "Hibernate", true, 2));
        question.addOption(createOption(12L, "Photoshop", false, 3));

        quiz.addQuestion(question);

        var score = gradingService.calculateScore(
                quiz,
                Map.of(1L, Set.of(10L, 12L))
        );

        assertThat(score).isEqualByComparingTo("0.00");
    }

    @Test
    void shouldGiveZeroPointsForUnansweredMultipleChoiceQuestion() {
        Quiz quiz = createQuiz();

        Question question = createQuestion(1L, "Koje tehnologije se često koriste uz Spring Boot?", 10, QuestionType.MULTIPLE_CHOICE, 1);
        question.addOption(createOption(10L, "Spring Data JPA", true, 1));
        question.addOption(createOption(11L, "Hibernate", true, 2));
        question.addOption(createOption(12L, "Photoshop", false, 3));

        quiz.addQuestion(question);

        var score = gradingService.calculateScore(
                quiz,
                Map.of()
        );

        assertThat(score).isEqualByComparingTo("0.00");
    }

    @Test
    void shouldCalculateMixedQuizScoreWithPartialMultipleChoice() {
        Quiz quiz = createQuiz();

        Question singleChoice = createQuestion(1L, "Koja anotacija pokreće Spring Boot aplikaciju?", 5, QuestionType.SINGLE_CHOICE, 1);
        singleChoice.addOption(createOption(10L, "@SpringBootApplication", true, 1));
        singleChoice.addOption(createOption(11L, "@Entity", false, 2));

        Question multipleChoice = createQuestion(2L, "Koje tehnologije se često koriste uz Spring Boot?", 10, QuestionType.MULTIPLE_CHOICE, 2);
        multipleChoice.addOption(createOption(20L, "Spring Data JPA", true, 1));
        multipleChoice.addOption(createOption(21L, "Hibernate", true, 2));
        multipleChoice.addOption(createOption(22L, "Photoshop", false, 3));

        quiz.addQuestion(singleChoice);
        quiz.addQuestion(multipleChoice);

        var score = gradingService.calculateScore(
                quiz,
                Map.of(
                        1L, Set.of(10L),
                        2L, Set.of(20L)
                )
        );

        assertThat(score).isEqualByComparingTo("10.00");
    }

    @Test
    void shouldCalculateMaxScore() {
        Quiz quiz = createQuiz();

        quiz.addQuestion(createQuestion(1L, "Pitanje 1", 5, QuestionType.SINGLE_CHOICE, 1));
        quiz.addQuestion(createQuestion(2L, "Pitanje 2", 10, QuestionType.MULTIPLE_CHOICE, 2));

        var maxScore = gradingService.calculateMaxScore(quiz);

        assertThat(maxScore).isEqualByComparingTo("15.00");
    }

    private Quiz createQuiz() {
        AppUser teacher = new AppUser("Marko Markovic", UserRole.TEACHER);

        return new Quiz(
                teacher,
                "Test kviz",
                "Opis test kviza",
                OffsetDateTime.now().minusHours(1),
                OffsetDateTime.now().plusHours(1)
        );
    }

    private Question createQuestion(Long id, String text, Integer points, QuestionType type, Integer position) {
        Question question = new Question(text, points, type, position);
        question.setId(id);
        return question;
    }

    private AnswerOption createOption(Long id, String text, boolean correct, Integer position) {
        AnswerOption option = new AnswerOption(text, correct, position);
        option.setId(id);
        return option;
    }
}