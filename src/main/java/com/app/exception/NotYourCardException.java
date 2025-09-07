package com.app.exception;

public class NotYourCardException extends AppException {
    public NotYourCardException() {
        super("The specified card is not yours");
    }
}