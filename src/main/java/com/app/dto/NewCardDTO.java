package com.app.dto;

import java.time.LocalDate;

import com.app.validator.NoOneHasNumber;
import com.app.validator.UserWithIdExist;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public class NewCardDTO {
    
    @NotBlank(message="Cardnumber is required")
    @Pattern(regexp="^[0-9]{16}$", message="Card number must be exactly 16 digits")
    @NoOneHasNumber
    private String cardNumber;

    @NotNull(message="Owner id is required")
    @Positive
    @UserWithIdExist
    private Integer ownerId;

    @NotNull(message="Validity period is required")
    @Future
    private LocalDate validityPeriod;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public LocalDate getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(LocalDate validityPeriod) {
        this.validityPeriod = validityPeriod;
    }
}
