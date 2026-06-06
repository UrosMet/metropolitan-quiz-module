package com.metropolitan.quiz.repository;

import com.metropolitan.quiz.entity.AnswerOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerOptionRepository extends JpaRepository<AnswerOption, Long> {
}