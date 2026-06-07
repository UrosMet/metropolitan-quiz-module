package com.metropolitan.quiz.service;

import com.metropolitan.quiz.auth.UserContext;
import com.metropolitan.quiz.entity.*;
import com.metropolitan.quiz.repository.AppUserRepository;
import com.metropolitan.quiz.repository.QuizRepository;
import com.metropolitan.quiz.service.impl.TeacherQuizServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherQuizServiceTest {

    @Mock
    private UserContext userContext;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuizMapper quizMapper;

    private TeacherQuizServiceImpl teacherQuizService;

    @BeforeEach
    void setUp() {
        teacherQuizService = new TeacherQuizServiceImpl(
                userContext,
                appUserRepository,
                quizRepository,
                quizMapper
        );
    }

    @Test
    void shouldRejectPublishingWhenOpensAtIsAfterClosesAt() {
        AppUser teacher = createTeacher();

        Quiz quiz = createDraftQuiz(
                teacher,
                OffsetDateTime.now().plusDays(2),
                OffsetDateTime.now().plusDays(1)
        );

        quiz.addQuestion(createValidSingleChoiceQuestion());

        when(userContext.currentUserId()).thenReturn(1L);
        when(quizRepository.findWithQuestionsById(100L))
                .thenReturn(Optional.of(quiz));

        assertThatThrownBy(() -> teacherQuizService.publishQuiz(100L))
                .hasMessageContaining("Quiz cannot be published");

        verify(quizRepository, never()).save(any());
    }

    @Test
    void shouldRejectPublishingQuizWithoutQuestions() {
        AppUser teacher = createTeacher();

        Quiz quiz = createDraftQuiz(
                teacher,
                OffsetDateTime.now().minusHours(1),
                OffsetDateTime.now().plusHours(1)
        );

        when(userContext.currentUserId()).thenReturn(1L);
        when(quizRepository.findWithQuestionsById(100L))
                .thenReturn(Optional.of(quiz));

        assertThatThrownBy(() -> teacherQuizService.publishQuiz(100L))
                .hasMessageContaining("Quiz cannot be published");

        verify(quizRepository, never()).save(any());
    }

    @Test
    void shouldRejectPublishingSingleChoiceQuestionWithMultipleCorrectOptions() {
        AppUser teacher = createTeacher();

        Quiz quiz = createDraftQuiz(
                teacher,
                OffsetDateTime.now().minusHours(1),
                OffsetDateTime.now().plusHours(1)
        );

        Question question = new Question(
                "Koja anotacija pokrece Spring Boot aplikaciju?",
                5,
                QuestionType.SINGLE_CHOICE,
                1
        );

        question.setId(10L);
        question.addOption(new AnswerOption("@SpringBootApplication", true, 1));
        question.addOption(new AnswerOption("@Entity", true, 2));

        quiz.addQuestion(question);

        when(userContext.currentUserId()).thenReturn(1L);
        when(quizRepository.findWithQuestionsById(100L))
                .thenReturn(Optional.of(quiz));

        assertThatThrownBy(() -> teacherQuizService.publishQuiz(100L))
                .hasMessageContaining("Quiz cannot be published");

        verify(quizRepository, never()).save(any());
    }

    private AppUser createTeacher() {
        AppUser teacher = new AppUser("Marko Markovic", UserRole.TEACHER);
        teacher.setId(1L);
        return teacher;
    }

    private Quiz createDraftQuiz(AppUser teacher, OffsetDateTime opensAt, OffsetDateTime closesAt) {
        Quiz quiz = new Quiz(
                teacher,
                "Test kviz",
                "Opis test kviza",
                opensAt,
                closesAt
        );

        quiz.setId(100L);
        quiz.setStatus(QuizStatus.DRAFT);

        return quiz;
    }

    private Question createValidSingleChoiceQuestion() {
        Question question = new Question(
                "Koja kljucna rec se koristi za kreiranje klase u Javi?",
                5,
                QuestionType.SINGLE_CHOICE,
                1
        );

        question.setId(10L);
        question.addOption(new AnswerOption("class", true, 1));
        question.addOption(new AnswerOption("function", false, 2));

        return question;
    }
}