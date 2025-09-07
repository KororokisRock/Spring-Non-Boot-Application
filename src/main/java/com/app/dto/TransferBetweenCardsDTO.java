package com.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public class TransferBetweenCardsDTO {
    @NotBlank(message="Cardnumber is required")
    @Pattern(regexp="^[0-9]{16}$", message="Card number must be exactly 16 digits")
    private String firstCardNumber;
    @NotBlank(message="Cardnumber is required")
    @Pattern(regexp="^[0-9]{16}$", message="Card number must be exactly 16 digits")
    private String secondCardNumber;

    @Positive
    private double amountTransferBetweenCards;

    public String getFirstCardNumber() {
        return firstCardNumber;
    }

    public String getSecondCardNumber() {
        return secondCardNumber;
    }

    public double getAmountTransferBetweenCards() {
        return amountTransferBetweenCards;
    }

    public void setFirstCardNumber(String firstCardNumber) {
        this.firstCardNumber = firstCardNumber;
    }

    public void setSecondCardNumber(String secondCardNumber) {
        this.secondCardNumber = secondCardNumber;
    }

    public void setAmountTransferBetweenCards(double amountTransferBetweenCards) {
        this.amountTransferBetweenCards = amountTransferBetweenCards;
    }
}
