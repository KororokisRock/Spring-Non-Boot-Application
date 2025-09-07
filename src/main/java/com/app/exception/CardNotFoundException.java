package com.app.exception;

public class CardNotFoundException extends AppException {
    
    public CardNotFoundException(String cardNumber) {
        super(String.format("Card with %s card number not found", "*".repeat(cardNumber.length() - 4) + cardNumber.substring(cardNumber.length() - 4)));
    }
}
