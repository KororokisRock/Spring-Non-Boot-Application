package com.app.exception;

public class BlockedAlreadyException extends AppException {
    public BlockedAlreadyException(String cardNumber) {
        super(String.format("Card with %s card number blocked already", "*".repeat(cardNumber.length() - 4) + cardNumber.substring(cardNumber.length() - 4)));
    }
}
