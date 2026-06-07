package com.metropolitan.quiz.service;

import com.metropolitan.quiz.dto.QuizResponse;
import com.metropolitan.quiz.dto.QuizResultResponse;
import com.metropolitan.quiz.dto.QuizSubmitResponse;
import com.metropolitan.quiz.dto.QuizSummaryResponse;
import com.metropolitan.quiz.dto.SubmitQuizRequest;

import java.util.List;

public interface StudentQuizService {

    List<QuizSummaryResponse> getAvailableQuizzes();

    QuizResponse openQuiz(Long quizId);

    QuizSubmitResponse submitQuiz(Long quizId, SubmitQuizRequest request);

    QuizResultResponse getResult(Long quizId);
}