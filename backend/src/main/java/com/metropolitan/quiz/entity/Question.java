package com.metropolitan.quiz.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "questions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @NotNull
    @Column(name = "text", nullable = false, columnDefinition = "TEXT")
    private String text;

    @NotNull
    @Positive
    @Column(name = "points", nullable = false)
    private Integer points;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private QuestionType type;

    @NotNull
    @Column(name = "position", nullable = false)
    private Integer position;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<AnswerOption> options = new ArrayList<>();

    public Question(String text, Integer points, QuestionType type, Integer position) {
        this.text = text;
        this.points = points;
        this.type = type;
        this.position = position;
    }

    public void addOption(AnswerOption option) {
        options.add(option);
        option.setQuestion(this);
    }
}