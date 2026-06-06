package com.metropolitan.quiz.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "submission_answers",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_submission_answer",
                        columnNames = {"submission_id", "question_id", "option_id"}
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubmissionAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "option_id", nullable = false)
    private AnswerOption option;

    public SubmissionAnswer(Question question, AnswerOption option) {
        this.question = question;
        this.option = option;
    }

    void setSubmission(Submission submission) {
        this.submission = submission;
    }
}