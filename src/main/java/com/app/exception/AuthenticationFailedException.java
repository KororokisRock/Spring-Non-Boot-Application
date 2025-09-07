package com.app.exception;

public class AuthenticationFailedException extends AppException {
    public AuthenticationFailedException() {
        super("");
    }

    public AuthenticationFailedException(String message) {
        super("Authentication failed. " + message);
    }
}
