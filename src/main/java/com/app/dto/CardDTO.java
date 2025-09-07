package com.app.dto;

import java.time.LocalDate;

import com.app.model.Card;
import com.app.model.STATUS;
import com.fasterxml.jackson.annotation.JsonFormat;


public class CardDTO {
    private Integer id;
    private String cardNumber;
    private Integer ownerId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate validityPeriod;
    private STATUS status;
    private double balance;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardnumber) {
        this.cardNumber = cardnumber;
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

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public double getBalance() {
        return  balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    public CardDTO(Card card, boolean maskedCardNumber) {
        this.id = card.getId();
        this.cardNumber = setCardNumber(card.getCardNumber(), maskedCardNumber);
        this.ownerId = card.getOwnerId();
        this.validityPeriod = card.getValidityPeriod();
        this.status = card.getStatus();
        this.balance = card.getBalance();
    }

    public CardDTO() {};

    static public CardDTO newCardDTOWithMaksedNumber(Card card) {
        return new CardDTO(card, true);
    }

    static public CardDTO newCardDTOWithFullNumber(Card card) {
        return new CardDTO(card, false);
    }

    static private String setCardNumber(String cardNumber, boolean maskedCardNumber) {
        if (maskedCardNumber) {
            return "*".repeat(cardNumber.length() - 4) + cardNumber.substring(cardNumber.length() - 4);
        }
        return cardNumber;
    }
}
