package com.metropolitan.quiz.repository;

import com.metropolitan.quiz.entity.AppUser;
import com.metropolitan.quiz.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

  Optional<AppUser> findByIdAndRole(Long id, UserRole role);
}