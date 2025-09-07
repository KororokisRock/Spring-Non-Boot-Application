package com.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CardNumberDTO {
    @NotBlank(message="Cardnumber is required")
    @Pattern(regexp="^[0-9]{16}$", message="Card number must be exactly 16 digits")
    private String cardNumber;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}
