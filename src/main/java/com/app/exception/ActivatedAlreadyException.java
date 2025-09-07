package com.app.exception;

public class ActivatedAlreadyException extends AppException {
    public ActivatedAlreadyException(String cardNumber) {
        super(String.format("Card with %s card number activated already", "*".repeat(cardNumber.length() - 4) + cardNumber.substring(cardNumber.length() - 4)));
    }
}
