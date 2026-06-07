package com.metropolitan.quiz.integration;

import com.metropolitan.quiz.auth.UserContext;
import com.metropolitan.quiz.common.GlobalExceptionHandler;
import com.metropolitan.quiz.auth.UserHeaderFilter;
import com.metropolitan.quiz.controller.StudentQuizController;
import com.metropolitan.quiz.dto.QuizSummaryResponse;
import com.metropolitan.quiz.service.impl.StudentQuizServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentQuizController.class)
@Import({
        UserHeaderFilter.class,
        GlobalExceptionHandler.class
})
class StudentQuizControllerMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentQuizServiceImpl studentQuizService;

    @MockitoBean
    private UserContext userContext;

    @Test
    void shouldReturnAvailableQuizzesWhenStudentHeadersAreProvided() throws Exception {
        QuizSummaryResponse quiz = new QuizSummaryResponse(
                1L,
                "Demo Java kviz",
                "Osnovni demo kviz za lokalno testiranje.",
                OffsetDateTime.parse("2026-06-01T10:00:00Z"),
                OffsetDateTime.parse("2026-06-09T23:59:00Z")
        );

        when(studentQuizService.getAvailableQuizzes())
                .thenReturn(List.of(quiz));

        mockMvc.perform(
                        get("/api/student/quizzes/available")
                                .header("X-User-Id", "2")
                                .header("X-User-Role", "STUDENT")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Demo Java kviz"))
                .andExpect(jsonPath("$[0].description").value("Osnovni demo kviz za lokalno testiranje."));
    }

    @Test
    void shouldReturnUnauthorizedWhenUserHeadersAreMissing() throws Exception {
        mockMvc.perform(get("/api/student/quizzes/available"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Missing X-User-Id header"));
    }
}