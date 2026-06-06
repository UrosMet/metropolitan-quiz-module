package com.metropolitan.quiz.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metropolitan.quiz.entity.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class UserHeaderFilter extends OncePerRequestFilter {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_ROLE_HEADER = "X-User-Role";

    private final UserContext userContext;
    private final ObjectMapper objectMapper;

    public UserHeaderFilter(UserContext userContext, ObjectMapper objectMapper) {
        this.userContext = userContext;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // User headers are required only for application API endpoints.
        return !path.startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String userIdHeader = request.getHeader(USER_ID_HEADER);
            String userRoleHeader = request.getHeader(USER_ROLE_HEADER);

            if (userIdHeader == null || userIdHeader.isBlank()) {
                writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing X-User-Id header");
                return;
            }

            if (userRoleHeader == null || userRoleHeader.isBlank()) {
                writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing X-User-Role header");
                return;
            }

            Long userId = parseUserId(userIdHeader, response);
            if (userId == null) {
                return;
            }

            UserRole role = parseUserRole(userRoleHeader, response);
            if (role == null) {
                return;
            }

            userContext.set(new CurrentUser(userId, role));

            filterChain.doFilter(request, response);
        } finally {
            userContext.clear();
        }
    }

    private Long parseUserId(String value, HttpServletResponse response) throws IOException {
        try {
            long userId = Long.parseLong(value);

            if (userId <= 0) {
                writeError(response, HttpServletResponse.SC_BAD_REQUEST, "X-User-Id must be a positive number");
                return null;
            }

            return userId;
        } catch (NumberFormatException ex) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "X-User-Id must be a valid number");
            return null;
        }
    }

    private UserRole parseUserRole(String value, HttpServletResponse response) throws IOException {
        try {
            return UserRole.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST, "X-User-Role must be TEACHER or STUDENT");
            return null;
        }
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", status);
        body.put("message", message);

        objectMapper.writeValue(response.getWriter(), body);
    }
}