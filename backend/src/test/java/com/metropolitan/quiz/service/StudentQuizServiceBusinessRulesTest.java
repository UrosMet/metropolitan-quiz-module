package com.metropolitan.quiz.service;

import com.metropolitan.quiz.auth.UserContext;
import com.metropolitan.quiz.dto.SubmitQuizRequest;
import com.metropolitan.quiz.entity.*;
import com.metropolitan.quiz.grading.GradingService;
import com.metropolitan.quiz.repository.AppUserRepository;
import com.metropolitan.quiz.repository.QuizRepository;
import com.metropolitan.quiz.repository.SubmissionRepository;
import com.metropolitan.quiz.service.impl.StudentQuizServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentQuizServiceBusinessRulesTest {

    @Mock
    private UserContext userContext;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuizMapper quizMapper;

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private GradingService gradingService;

    private StudentQuizServiceImpl studentQuizService;

    @BeforeEach
    void setUp() {
        studentQuizService = new StudentQuizServiceImpl(
                userContext,
                appUserRepository,
                quizRepository,
                quizMapper,
                submissionRepository,
                gradingService
        );
    }

    @Test
    void shouldRejectSubmissionBeforeQuizOpens() {
        AppUser student = createStudent();

        Quiz quiz = createPublishedQuiz(
                OffsetDateTime.now().plusHours(1),
                OffsetDateTime.now().plusHours(2)
        );

        when(userContext.currentUserId()).thenReturn(2L);
        when(appUserRepository.findByIdAndRole(2L, UserRole.STUDENT))
                .thenReturn(Optional.of(student));
        when(quizRepository.findWithQuestionsById(100L))
                .thenReturn(Optional.of(quiz));

        Throwable thrown = catchThrowable(() ->
                studentQuizService.submitQuiz(100L, new SubmitQuizRequest(List.of()))
        );

        assertThat(thrown)
                .isInstanceOf(ResponseStatusException.class);

        ResponseStatusException exception = (ResponseStatusException) thrown;

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(exception.getReason()).isEqualTo("Quiz is not open yet");

        verify(submissionRepository, never()).save(any());
    }

    @Test
    void shouldRejectSubmissionAfterQuizCloses() {
        AppUser student = createStudent();

        Quiz quiz = createPublishedQuiz(
                OffsetDateTime.now().minusHours(2),
                OffsetDateTime.now().minusHours(1)
        );

        when(userContext.currentUserId()).thenReturn(2L);
        when(appUserRepository.findByIdAndRole(2L, UserRole.STUDENT))
                .thenReturn(Optional.of(student));
        when(quizRepository.findWithQuestionsById(100L))
                .thenReturn(Optional.of(quiz));

        Throwable thrown = catchThrowable(() ->
                studentQuizService.submitQuiz(100L, new SubmitQuizRequest(List.of()))
        );

        assertThat(thrown)
                .isInstanceOf(ResponseStatusException.class);

        ResponseStatusException exception = (ResponseStatusException) thrown;

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(exception.getReason()).isEqualTo("Quiz is already closed");

        verify(submissionRepository, never()).save(any());
    }

    @Test
    void shouldRejectDuplicateSubmission() {
        AppUser student = createStudent();

        Quiz quiz = createPublishedQuiz(
                OffsetDateTime.now().minusHours(1),
                OffsetDateTime.now().plusHours(1)
        );

        when(userContext.currentUserId()).thenReturn(2L);
        when(appUserRepository.findByIdAndRole(2L, UserRole.STUDENT))
                .thenReturn(Optional.of(student));
        when(quizRepository.findWithQuestionsById(100L))
                .thenReturn(Optional.of(quiz));
        when(submissionRepository.existsByQuizIdAndStudentId(100L, 2L))
                .thenReturn(true);

        Throwable thrown = catchThrowable(() ->
                studentQuizService.submitQuiz(100L, new SubmitQuizRequest(List.of()))
        );

        assertThat(thrown)
                .isInstanceOf(ResponseStatusException.class);

        ResponseStatusException exception = (ResponseStatusException) thrown;

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(exception.getReason()).isEqualTo("Student has already submitted this quiz");

        verify(submissionRepository, never()).save(any());
    }

    private AppUser createStudent() {
        AppUser student = new AppUser("Jovan Jovanovic", UserRole.STUDENT);
        student.setId(2L);
        return student;
    }

    private AppUser createTeacher() {
        AppUser teacher = new AppUser("Marko Markovic", UserRole.TEACHER);
        teacher.setId(1L);
        return teacher;
    }

    private Quiz createPublishedQuiz(OffsetDateTime opensAt, OffsetDateTime closesAt) {
        Quiz quiz = new Quiz(
                createTeacher(),
                "Test kviz",
                "Opis test kviza",
                opensAt,
                closesAt
        );

        quiz.setId(100L);
        quiz.setStatus(QuizStatus.PUBLISHED);
        quiz.addQuestion(createValidSingleChoiceQuestion());

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