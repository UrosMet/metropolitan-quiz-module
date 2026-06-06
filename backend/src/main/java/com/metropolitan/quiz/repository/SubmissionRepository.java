package com.metropolitan.quiz.repository;

import com.metropolitan.quiz.entity.Submission;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

  boolean existsByQuizIdAndStudentId(Long quizId, Long studentId);

  @EntityGraph(attributePaths = {"answers", "answers.question", "answers.option"})
  Optional<Submission> findByQuizIdAndStudentId(Long quizId, Long studentId);
}