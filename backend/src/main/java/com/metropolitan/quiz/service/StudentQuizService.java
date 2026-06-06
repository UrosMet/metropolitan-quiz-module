package com.metropolitan.quiz.service;

import com.metropolitan.quiz.auth.UserContext;
import com.metropolitan.quiz.dto.*;
import com.metropolitan.quiz.entity.*;
import com.metropolitan.quiz.grading.GradingService;
import com.metropolitan.quiz.repository.AppUserRepository;
import com.metropolitan.quiz.repository.QuizRepository;
import com.metropolitan.quiz.repository.SubmissionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StudentQuizService {

    private final UserContext userContext;
    private final AppUserRepository appUserRepository;
    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;
    private final SubmissionRepository submissionRepository;
    private final GradingService gradingService;

    public StudentQuizService(
            UserContext userContext,
            AppUserRepository appUserRepository,
            QuizRepository quizRepository,
            QuizMapper quizMapper,
            SubmissionRepository submissionRepository,
            GradingService gradingService
    ) {
        this.userContext = userContext;
        this.appUserRepository = appUserRepository;
        this.quizRepository = quizRepository;
        this.quizMapper = quizMapper;
        this.submissionRepository = submissionRepository;
        this.gradingService = gradingService;
    }

    @Transactional(readOnly = true)
    public List<QuizSummaryResponse> getAvailableQuizzes() {
        userContext.requireStudent();

        Long studentId = userContext.currentUserId();

        AppUser student = appUserRepository.findByIdAndRole(studentId, UserRole.STUDENT)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Student user does not exist or does not have STUDENT role"
                ));

        OffsetDateTime now = OffsetDateTime.now();

        return quizRepository
                .findByStatusAndOpensAtLessThanEqualAndClosesAtGreaterThanEqualOrderByClosesAtAsc(
                        QuizStatus.PUBLISHED,
                        now,
                        now
                )
                .stream()
                .map(quizMapper::toSummaryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public QuizResponse openQuiz(Long quizId) {
        userContext.requireStudent();

        Long studentId = userContext.currentUserId();

        appUserRepository.findByIdAndRole(studentId, UserRole.STUDENT)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Student user does not exist or does not have STUDENT role"
                ));

        Quiz quiz = quizRepository.findWithQuestionsById(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Quiz not found"
                ));

        validateQuizIsAvailableForStudent(quiz);

        return quizMapper.toResponse(quiz);
    }

    private void validateQuizIsAvailableForStudent(Quiz quiz) {
        if (!quiz.isPublished()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Quiz is not available"
            );
        }

        OffsetDateTime now = OffsetDateTime.now();

        if (now.isBefore(quiz.getOpensAt())) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Quiz is not open yet"
            );
        }

        if (now.isAfter(quiz.getClosesAt())) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Quiz is already closed"
            );
        }
    }

    @Transactional
    public QuizSubmitResponse submitQuiz(Long quizId, SubmitQuizRequest request) {
        userContext.requireStudent();

        Long studentId = userContext.currentUserId();

        AppUser student = appUserRepository.findByIdAndRole(studentId, UserRole.STUDENT)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Student user does not exist or does not have STUDENT role"
                ));

        Quiz quiz = quizRepository.findWithQuestionsById(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Quiz not found"
                ));

        validateQuizIsAvailableForStudent(quiz);

        if (submissionRepository.existsByQuizIdAndStudentId(quizId, studentId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Student has already submitted this quiz"
            );
        }

        Map<Long, Set<Long>> selectedOptionIdsByQuestionId = normalizeAnswers(request);
        validateSubmittedAnswers(quiz, selectedOptionIdsByQuestionId);

        int score = gradingService.calculateScore(quiz, selectedOptionIdsByQuestionId);
        int maxScore = gradingService.calculateMaxScore(quiz);

        Submission submission = new Submission(quiz, student, score);

        for (Question question : quiz.getQuestions()) {
            Set<Long> selectedOptionIds = selectedOptionIdsByQuestionId.getOrDefault(question.getId(), Set.of());

            for (AnswerOption option : question.getOptions()) {
                if (selectedOptionIds.contains(option.getId())) {
                    submission.addAnswer(new SubmissionAnswer(question, option));
                }
            }
        }

        try {
            Submission savedSubmission = submissionRepository.save(submission);

            return new QuizSubmitResponse(
                    quiz.getId(),
                    student.getId(),
                    savedSubmission.getScore(),
                    maxScore,
                    savedSubmission.getSubmittedAt()
            );
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Student has already submitted this quiz"
            );
        }
    }

    @Transactional(readOnly = true)
    public QuizResultResponse getResult(Long quizId) {
        userContext.requireStudent();

        Long studentId = userContext.currentUserId();

        appUserRepository.findByIdAndRole(studentId, UserRole.STUDENT)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Student user does not exist or does not have STUDENT role"
                ));

        Quiz quiz = quizRepository.findWithQuestionsById(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Quiz not found"
                ));

        Submission submission = submissionRepository.findByQuizIdAndStudentId(quizId, studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Result not found for this student and quiz"
                ));

        return new QuizResultResponse(
                quiz.getId(),
                quiz.getTitle(),
                studentId,
                submission.getScore(),
                gradingService.calculateMaxScore(quiz),
                submission.getSubmittedAt()
        );
    }

    private Map<Long, Set<Long>> normalizeAnswers(SubmitQuizRequest request) {
        if (request.answers() == null) {
            return Map.of();
        }

        return request.answers()
                .stream()
                .collect(Collectors.toMap(
                        SubmitQuestionAnswerRequest::questionId,
                        answer -> answer.optionIds() == null
                                ? Set.of()
                                : new HashSet<>(answer.optionIds()),
                        (existing, duplicate) -> {
                            throw new ResponseStatusException(
                                    HttpStatus.BAD_REQUEST,
                                    "Duplicate answer for the same question is not allowed"
                            );
                        }
                ));
    }

    private void validateSubmittedAnswers(Quiz quiz, Map<Long, Set<Long>> selectedOptionIdsByQuestionId) {
        Set<Long> quizQuestionIds = quiz.getQuestions()
                .stream()
                .map(Question::getId)
                .collect(Collectors.toSet());

        for (Long submittedQuestionId : selectedOptionIdsByQuestionId.keySet()) {
            if (!quizQuestionIds.contains(submittedQuestionId)) {
                throw new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "Submitted question does not belong to this quiz: " + submittedQuestionId
                );
            }
        }

        for (Question question : quiz.getQuestions()) {
            Set<Long> selectedOptionIds = selectedOptionIdsByQuestionId.getOrDefault(question.getId(), Set.of());

            Set<Long> validOptionIds = question.getOptions()
                    .stream()
                    .map(AnswerOption::getId)
                    .collect(Collectors.toSet());

            for (Long selectedOptionId : selectedOptionIds) {
                if (!validOptionIds.contains(selectedOptionId)) {
                    throw new ResponseStatusException(
                            HttpStatus.UNPROCESSABLE_ENTITY,
                            "Selected option does not belong to question " + question.getId()
                    );
                }
            }

            if (question.getType() == QuestionType.SINGLE_CHOICE && selectedOptionIds.size() > 1) {
                throw new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "SINGLE_CHOICE question " + question.getId() + " accepts only one selected option"
                );
            }
        }
    }
}