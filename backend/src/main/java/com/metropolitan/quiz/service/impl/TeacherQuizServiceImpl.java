package com.metropolitan.quiz.service.impl;

import com.metropolitan.quiz.auth.UserContext;
import com.metropolitan.quiz.dto.CreateAnswerOptionRequest;
import com.metropolitan.quiz.dto.CreateQuestionRequest;
import com.metropolitan.quiz.dto.CreateQuizRequest;
import com.metropolitan.quiz.dto.QuizResponse;
import com.metropolitan.quiz.dto.TeacherQuizSummaryResponse;
import java.util.Comparator;
import com.metropolitan.quiz.entity.*;
import com.metropolitan.quiz.repository.AppUserRepository;
import com.metropolitan.quiz.repository.QuizRepository;
import com.metropolitan.quiz.service.QuizMapper;
import com.metropolitan.quiz.service.TeacherQuizService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.metropolitan.quiz.common.PublishValidationException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TeacherQuizServiceImpl implements TeacherQuizService {

    private final UserContext userContext;
    private final AppUserRepository appUserRepository;
    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;

    public TeacherQuizServiceImpl(
            UserContext userContext,
            AppUserRepository appUserRepository,
            QuizRepository quizRepository,
            QuizMapper quizMapper
    ) {
        this.userContext = userContext;
        this.appUserRepository = appUserRepository;
        this.quizRepository = quizRepository;
        this.quizMapper = quizMapper;
    }

    @Override
    @Transactional
    public QuizResponse createQuiz(CreateQuizRequest request) {
        userContext.requireTeacher();

        Long teacherId = userContext.currentUserId();

        AppUser teacher = appUserRepository.findByIdAndRole(teacherId, UserRole.TEACHER)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Teacher user does not exist or does not have TEACHER role"
                ));

        Quiz quiz = new Quiz(
                teacher,
                request.title(),
                request.description(),
                request.opensAt(),
                request.closesAt()
        );

        if (request.questions() != null) {
            int questionPosition = 1;

            for (CreateQuestionRequest questionRequest : request.questions()) {
                Question question = new Question(
                        questionRequest.text(),
                        questionRequest.points(),
                        questionRequest.type(),
                        questionPosition++
                );

                if (questionRequest.options() != null) {
                    int optionPosition = 1;

                    for (CreateAnswerOptionRequest optionRequest : questionRequest.options()) {
                        AnswerOption option = new AnswerOption(
                                optionRequest.text(),
                                optionRequest.correct(),
                                optionPosition++
                        );

                        question.addOption(option);
                    }
                }

                quiz.addQuestion(question);
            }
        }

        Quiz savedQuiz = quizRepository.save(quiz);

        return quizMapper.toResponse(savedQuiz);
    }


    @Override
    @Transactional
    public QuizResponse publishQuiz(Long quizId) {
        userContext.requireTeacher();

        Long teacherId = userContext.currentUserId();

        Quiz quiz = quizRepository.findWithQuestionsById(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Quiz not found"
                ));

        if (!quiz.getTeacher().getId().equals(teacherId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can publish only your own quizzes"
            );
        }

        List<String> errors = validateQuizForPublishing(quiz);

        if (!errors.isEmpty()) {
            throw new PublishValidationException(errors);
        }

        quiz.publish();

        Quiz savedQuiz = quizRepository.save(quiz);

        return quizMapper.toResponse(savedQuiz);
    }

    private List<String> validateQuizForPublishing(Quiz quiz) {
        List<String> errors = new ArrayList<>();

        if (quiz.getOpensAt() == null) {
            errors.add("opensAt is required");
        }

        if (quiz.getClosesAt() == null) {
            errors.add("closesAt is required");
        }

        if (quiz.getOpensAt() != null
                && quiz.getClosesAt() != null
                && !quiz.getOpensAt().isBefore(quiz.getClosesAt())) {
            errors.add("opensAt must be before closesAt");
        }

        if (quiz.getQuestions() == null || quiz.getQuestions().isEmpty()) {
            errors.add("Quiz must contain at least one question");
            return errors;
        }

        for (Question question : quiz.getQuestions()) {
            String questionLabel = "Question " + question.getPosition();

            if (question.getOptions() == null || question.getOptions().isEmpty()) {
                errors.add(questionLabel + " must contain at least one option");
                continue;
            }

            if (question.getOptions().size() < 2) {
                errors.add(questionLabel + " must contain at least two options");
            }

            long correctOptionsCount = question.getOptions()
                    .stream()
                    .filter(AnswerOption::isCorrect)
                    .count();

            if (correctOptionsCount == 0) {
                errors.add(questionLabel + " must have at least one correct option");
            }

            if (question.getType() == QuestionType.SINGLE_CHOICE && correctOptionsCount != 1) {
                errors.add(questionLabel + " of type SINGLE_CHOICE must have exactly one correct option");
            }
        }

        return errors;
    }

    @Override
    @Transactional
    public List<TeacherQuizSummaryResponse> getMyQuizzes() {
        userContext.requireTeacher();

        Long teacherId = userContext.currentUserId();

        appUserRepository.findByIdAndRole(teacherId, UserRole.TEACHER)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Teacher user does not exist or does not have TEACHER role"
                ));

        return quizRepository.findByTeacherIdOrderByCreatedAtDesc(teacherId)
                .stream()
                .sorted(Comparator.comparing(Quiz::getCreatedAt).reversed())
                .map(quizMapper::toTeacherSummaryResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteDraftQuiz(Long quizId) {
        userContext.requireTeacher();

        Long teacherId = userContext.currentUserId();

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Quiz not found"
                ));

        if (!quiz.getTeacher().getId().equals(teacherId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can delete only your own quizzes"
            );
        }

        if (quiz.getStatus() != QuizStatus.DRAFT) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only draft quizzes can be deleted"
            );
        }

        quizRepository.delete(quiz);
    }
}