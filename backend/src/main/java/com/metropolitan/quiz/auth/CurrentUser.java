package com.metropolitan.quiz.auth;

import com.metropolitan.quiz.entity.UserRole;

public record CurrentUser(
        Long id,
        UserRole role
) {
}