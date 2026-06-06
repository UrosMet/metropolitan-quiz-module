package com.metropolitan.quiz.auth;

import com.metropolitan.quiz.entity.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class UserContext {

    private static final ThreadLocal<CurrentUser> CURRENT_USER = new ThreadLocal<>();

    public void set(CurrentUser currentUser) {
        CURRENT_USER.set(currentUser);
    }

    public CurrentUser get() {
        CurrentUser currentUser = CURRENT_USER.get();

        if (currentUser == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Missing user context. Provide X-User-Id and X-User-Role headers."
            );
        }

        return currentUser;
    }

    public Long currentUserId() {
        return get().id();
    }

    public UserRole currentUserRole() {
        return get().role();
    }

    public void requireRole(UserRole requiredRole) {
        CurrentUser currentUser = get();

        if (currentUser.role() != requiredRole) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "This endpoint requires role " + requiredRole
            );
        }
    }

    public void requireTeacher() {
        requireRole(UserRole.TEACHER);
    }

    public void requireStudent() {
        requireRole(UserRole.STUDENT);
    }

    public void clear() {
        CURRENT_USER.remove();
    }
}