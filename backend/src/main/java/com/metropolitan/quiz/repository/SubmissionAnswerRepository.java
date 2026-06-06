package com.metropolitan.quiz.repository;

import com.metropolitan.quiz.entity.SubmissionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionAnswerRepository extends JpaRepository<SubmissionAnswer, Long> {

    List<SubmissionAnswer> findBySubmissionId(Long submissionId);
}