package com.metropolitan.quiz.service;

import com.metropolitan.quiz.dto.CreateQuizRequest;
import com.metropolitan.quiz.dto.QuizResponse;
import com.metropolitan.quiz.dto.TeacherQuizSummaryResponse;

import java.util.List;

public interface TeacherQuizService {

    QuizResponse createQuiz(CreateQuizRequest request);

    List<TeacherQuizSummaryResponse> getMyQuizzes();

    QuizResponse publishQuiz(Long quizId);

    void deleteDraftQuiz(Long quizId);
}