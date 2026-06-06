package com.metropolitan.quiz.common;

import java.util.List;

public class PublishValidationException extends RuntimeException {

    private final List<String> errors;

    public PublishValidationException(List<String> errors) {
        super("Quiz cannot be published");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}