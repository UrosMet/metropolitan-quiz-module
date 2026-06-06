package com.metropolitan.quiz.common;

import java.time.OffsetDateTime;
import java.util.List;

public record ApiError(
        OffsetDateTime timestamp,
        int status,
        String message,
        List<String> errors
) {
}