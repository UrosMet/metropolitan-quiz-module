package com.metropolitan.quiz.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(
        name = "submissions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_submission_quiz_student",
                        columnNames = {"quiz_id", "student_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private AppUser student;

    @NotNull
    @PositiveOrZero
    @Column(name = "score", nullable = false, precision = 10, scale = 2)
    private BigDecimal score;

    @NotNull
    @Column(name = "submitted_at", nullable = false)
    private OffsetDateTime submittedAt;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubmissionAnswer> answers = new ArrayList<>();

    public Submission(Quiz quiz, AppUser student, BigDecimal score) {
        this.quiz = quiz;
        this.student = student;
        this.score = score;
    }

    @PrePersist
    void onCreate() {
        if (submittedAt == null) {
            submittedAt = OffsetDateTime.now();
        }
    }

    public void addAnswer(SubmissionAnswer answer) {
        answers.add(answer);
        answer.setSubmission(this);
    }
}