package com.app.exception;

public class InvalidRefreshTokenException extends AppException {
    public InvalidRefreshTokenException() {
        super("Invalid refresh token");
    }
}
