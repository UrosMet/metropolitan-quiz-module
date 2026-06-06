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
@Table(name = "answer_options")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnswerOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @NotNull
    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(name = "is_correct", nullable = false)
    private boolean correct = false;

    @NotNull
    @Column(name = "position", nullable = false)
    private Integer position;

    public AnswerOption(String text, boolean correct, Integer position) {
        this.text = text;
        this.correct = correct;
        this.position = position;
    }
}