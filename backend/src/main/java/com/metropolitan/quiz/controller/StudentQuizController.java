package com.metropolitan.quiz.controller;

import com.metropolitan.quiz.dto.QuizResponse;
import com.metropolitan.quiz.dto.QuizSummaryResponse;
import com.metropolitan.quiz.service.StudentQuizService;
import org.springframework.web.bind.annotation.*;
import com.metropolitan.quiz.dto.QuizResultResponse;
import com.metropolitan.quiz.dto.QuizSubmitResponse;
import com.metropolitan.quiz.dto.SubmitQuizRequest;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/student/quizzes")
public class StudentQuizController {

    private final StudentQuizService studentQuizService;

    public StudentQuizController(StudentQuizService studentQuizService) {
        this.studentQuizService = studentQuizService;
    }

    @GetMapping("/available")
    public List<QuizSummaryResponse> getAvailableQuizzes() {
        return studentQuizService.getAvailableQuizzes();
    }

    @GetMapping("/{quizId}")
    public QuizResponse openQuiz(@PathVariable Long quizId) {
        return studentQuizService.openQuiz(quizId);
    }

    @PostMapping("/{quizId}/submit")
    public QuizSubmitResponse submitQuiz(
            @PathVariable Long quizId,
            @Valid @RequestBody SubmitQuizRequest request
    ) {
        return studentQuizService.submitQuiz(quizId, request);
    }

    @GetMapping("/{quizId}/result")
    public QuizResultResponse getResult(@PathVariable Long quizId) {
        return studentQuizService.getResult(quizId);
    }
}