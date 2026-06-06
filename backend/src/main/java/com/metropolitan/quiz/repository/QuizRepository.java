package com.metropolitan.quiz.repository;

import com.metropolitan.quiz.entity.Quiz;
import com.metropolitan.quiz.entity.QuizStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

  List<Quiz> findByTeacherIdOrderByCreatedAtDesc(Long teacherId);

  List<Quiz> findByStatusAndOpensAtLessThanEqualAndClosesAtGreaterThanEqualOrderByClosesAtAsc(
          QuizStatus status,
          OffsetDateTime opensAt,
          OffsetDateTime closesAt
  );

  @EntityGraph(attributePaths = {"questions"})
  Optional<Quiz> findWithQuestionsById(Long id);
}