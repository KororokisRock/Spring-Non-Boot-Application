package com.app.exception;

import java.util.Map;

public class ValidationValueException extends AppException {
    private final Map<String, String> errors;
    public ValidationValueException(Map<String, String> errors) {
        super("Validation error.");
        this.errors = errors;
    }

    public Map<String, String> getValidationErrors() {
        return errors;
    }
}
