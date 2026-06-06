package com.metropolitan.quiz.repository;

import com.metropolitan.quiz.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}