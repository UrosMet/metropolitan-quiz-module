package com.metropolitan.quiz.controller;

import com.metropolitan.quiz.dto.QuizResponse;
import com.metropolitan.quiz.dto.QuizSummaryResponse;
import com.metropolitan.quiz.service.StudentQuizService;
import org.springframework.web.bind.annotation.*;
import com.metropolitan.quiz.dto.QuizResultResponse;
import com.metropolitan.quiz.dto.QuizSubmitResponse;
import com.metropolitan.quiz.dto.SubmitQuizRequest;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(
        name = "Student - kvizovi",
        description = "Endpoint-i za pregled dostupnih kvizova, otvaranje kviza, predaju odgovora i pregled rezultata."
)
@RestController
@RequestMapping("/api/student/quizzes")
public class StudentQuizController {

    private final StudentQuizService studentQuizService;

    public StudentQuizController(StudentQuizService studentQuizService) {
        this.studentQuizService = studentQuizService;
    }


    @Operation(
            summary = "Lista dostupnih kvizova",
            description = "Vraća objavljene kvizove koji su trenutno otvoreni i dostupni studentu."
    )
    @GetMapping("/available")
    public List<QuizSummaryResponse> getAvailableQuizzes() {
        return studentQuizService.getAvailableQuizzes();
    }


    @Operation(
            summary = "Otvaranje kviza",
            description = "Vraća pitanja i ponuđene odgovore za izabrani kviz, bez otkrivanja tačnih odgovora."
    )
    @GetMapping("/{quizId}")
    public QuizResponse openQuiz(@PathVariable Long quizId) {
        return studentQuizService.openQuiz(quizId);
    }


    @Operation(
            summary = "Predaja kviza",
            description = "Prima studentske odgovore, automatski računa rezultat i čuva predaju kviza."
    )
    @PostMapping("/{quizId}/submit")
    public QuizSubmitResponse submitQuiz(
            @PathVariable Long quizId,
            @Valid @RequestBody SubmitQuizRequest request
    ) {
        return studentQuizService.submitQuiz(quizId, request);
    }


    @Operation(
            summary = "Pregled rezultata",
            description = "Vraća sačuvan rezultat trenutno simuliranog studenta za kviz koji je već predao."
    )
    @GetMapping("/{quizId}/result")
    public QuizResultResponse getResult(@PathVariable Long quizId) {
        return studentQuizService.getResult(quizId);
    }
}