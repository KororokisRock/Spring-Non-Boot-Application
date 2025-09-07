package com.app.exception;

public class NotEnoughBalanceException extends AppException {
    public NotEnoughBalanceException() {
        super("Not enough balance on the card");
    }
}
