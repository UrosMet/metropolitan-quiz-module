package com.metropolitan.quiz.controller;

import com.metropolitan.quiz.dto.CreateQuizRequest;
import com.metropolitan.quiz.dto.QuizResponse;
import com.metropolitan.quiz.service.TeacherQuizService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.metropolitan.quiz.dto.TeacherQuizSummaryResponse;
import java.util.List;

@RestController
@RequestMapping("/api/teacher/quizzes")
public class TeacherQuizController {

    private final TeacherQuizService teacherQuizService;

    public TeacherQuizController(TeacherQuizService teacherQuizService) {
        this.teacherQuizService = teacherQuizService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuizResponse createQuiz(@Valid @RequestBody CreateQuizRequest request) {
        return teacherQuizService.createQuiz(request);
    }

    @PostMapping("/{quizId}/publish")
    public QuizResponse publishQuiz(@PathVariable Long quizId) {
        return teacherQuizService.publishQuiz(quizId);
    }

    @GetMapping
    public List<TeacherQuizSummaryResponse> getMyQuizzes() {
        return teacherQuizService.getMyQuizzes();
    }
}