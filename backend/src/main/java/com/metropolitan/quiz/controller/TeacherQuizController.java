package com.metropolitan.quiz.controller;

import com.metropolitan.quiz.dto.CreateQuizRequest;
import com.metropolitan.quiz.dto.QuizResponse;
import com.metropolitan.quiz.service.TeacherQuizService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.metropolitan.quiz.dto.TeacherQuizSummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@Tag(
        name = "Nastavnik - kvizovi",
        description = "Endpoint-i za kreiranje, pregled i objavljivanje kvizova od strane nastavnika."
)@RestController
@RequestMapping("/api/teacher/quizzes")
public class TeacherQuizController {

    private final TeacherQuizService teacherQuizService;

    public TeacherQuizController(TeacherQuizService teacherQuizService) {
        this.teacherQuizService = teacherQuizService;
    }

    @Operation(
            summary = "Kreiranje kviza",
            description = "Kreira novi kviz u DRAFT statusu, zajedno sa pitanjima i ponuđenim odgovorima."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuizResponse createQuiz(@Valid @RequestBody CreateQuizRequest request) {
        return teacherQuizService.createQuiz(request);
    }


    @Operation(
            summary = "Objavljivanje kviza",
            description = "Objavljuje kviz ako su ispunjena sva poslovna pravila za objavljivanje."
    )
    @PostMapping("/{quizId}/publish")
    public QuizResponse publishQuiz(@PathVariable Long quizId) {
        return teacherQuizService.publishQuiz(quizId);
    }


    @Operation(
            summary = "Pregled kvizova nastavnika",
            description = "Vraća listu kvizova koje je kreirao trenutno simulirani nastavnik."
    )
    @GetMapping
    public List<TeacherQuizSummaryResponse> getMyQuizzes() {
        return teacherQuizService.getMyQuizzes();
    }

    @Operation(
            summary = "Brisanje nacrta kviza",
            description = "Briše kviz samo ako pripada trenutno simuliranom nastavniku i ako je u DRAFT statusu."
    )
    @DeleteMapping("/{quizId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDraftQuiz(@PathVariable Long quizId) {
        teacherQuizService.deleteDraftQuiz(quizId);
    }
}