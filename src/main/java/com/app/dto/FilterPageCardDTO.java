package com.app.dto;

import java.time.LocalDate;

import com.app.model.STATUS;
import com.app.validator.FilterPageCardValid;

@FilterPageCardValid
public class FilterPageCardDTO {
    
    public static final Double DEFAULT_MIN_BALANCE = -1.7976931348623157E+308;
    public static final Double DEFAULT_MAX_BALANCE = 1.7976931348623157E308;

    private String username = null;
    private String directionSort = "asc";
    private String sortBy = "id";
    private int page = 0;
    private int size = 10;
    private String cardNumber = null;
    private LocalDate minEndDate = null;
    private LocalDate maxEndDate = null;
    private STATUS status = null;
    private Double minBalance = DEFAULT_MIN_BALANCE;
    private Double maxBalance = DEFAULT_MAX_BALANCE;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDirectionSort() {
        return directionSort;
    }

    public void setDirectionSort(String directionSort) {
        this.directionSort = directionSort;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
    
    public String getCardNumber() {
        return cardNumber;
    }
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public LocalDate getMinEndDate() {
        return minEndDate;
    }
    public void setMinEndDate(LocalDate minEndDate)  {
        this.minEndDate = minEndDate;
    }

    public LocalDate getMaxEndDate() {
        return maxEndDate;
    }
    public void setMaxEndDate(LocalDate maxEndDate)  {
        this.maxEndDate = maxEndDate;
    }

    public STATUS getStatus() {
        return status;
    }
    public void setStatus(STATUS status)  {
        this.status = status;
    }

    public double getMinBalance() {
        return minBalance;
    }
    public void setMinBalance(double minBalance)  {
        this.minBalance = minBalance;
    }

    public double getMaxBalance() {
        return maxBalance;
    }
    public void setMaxBalance(double maxBalance)  {
        this.maxBalance = maxBalance;
    }
}
