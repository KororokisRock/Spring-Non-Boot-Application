package com.app.validator;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.dto.FilterPageCardDTO;

@ExtendWith(MockitoExtension.class)
class FilterPageCardValidatorTest {

    @InjectMocks
    private FilterPageCardValidator validator;

    private FilterPageCardDTO validFilters;

    @BeforeEach
    void setUp() {
        validFilters = new FilterPageCardDTO();
        validFilters.setDirectionSort("asc");
        validFilters.setSortBy("id");
        validFilters.setPage(0);
        validFilters.setSize(10);
        validFilters.setMinBalance(0.0);
        validFilters.setMaxBalance(10000.0);
    }

    @Test
    void isValid_WithValidData_ShouldReturnTrue() {
        assertTrue(validator.isValid(validFilters, null));
    }

    @Test
    void isValid_WithNullUsername_ShouldReturnTrue() {
        validFilters.setUsername(null);
        assertTrue(validator.isValid(validFilters, null));
    }

    @Test
    void isValid_WithLongUsername_ShouldReturnFalse() {
        validFilters.setUsername("a".repeat(51));
        assertFalse(validator.isValid(validFilters, null));
    }

    @Test
    void isValid_WithInvalidDirectionSort_ShouldReturnFalse() {
        validFilters.setDirectionSort("invalid");
        assertFalse(validator.isValid(validFilters, null));
    }

    @Test
    void isValid_WithCaseInsensitiveDirectionSort_ShouldReturnTrue() {
        validFilters.setDirectionSort("ASC");
        assertTrue(validator.isValid(validFilters, null));
        
        validFilters.setDirectionSort("DESC");
        assertTrue(validator.isValid(validFilters, null));
    }

    @Test
    void isValid_WithInvalidSortBy_ShouldReturnFalse() {
        validFilters.setSortBy("invalid_field");
        assertFalse(validator.isValid(validFilters, null));
    }

    @Test
    void isValid_WithValidSortByFields_ShouldReturnTrue() {
        String[] validFields = {"id", "card_number", "owner_id", "validity_period", "status", "balance"};
        
        for (String field : validFields) {
            validFilters.setSortBy(field);
            assertTrue(validator.isValid(validFilters, null), 
                "Should be valid for field: " + field);
        }
    }

    @Test
    void isValid_WithNegativePage_ShouldReturnFalse() {
        validFilters.setPage(-1);
        assertFalse(validator.isValid(validFilters, null));
    }

    @Test
    void isValid_WithZeroSize_ShouldReturnFalse() {
        validFilters.setSize(0);
        assertFalse(validator.isValid(validFilters, null));
    }

    @Test
    void isValid_WithNegativeSize_ShouldReturnFalse() {
        validFilters.setSize(-5);
        assertFalse(validator.isValid(validFilters, null));
    }

    @Test
    void isValid_WithValidCardNumber_ShouldReturnTrue() {
        validFilters.setCardNumber("1234567890123456");
        assertTrue(validator.isValid(validFilters, null));
    }

    @Test
    void isValid_WithInvalidCardNumber_ShouldReturnFalse() {
        validFilters.setCardNumber("123abc");
        assertFalse(validator.isValid(validFilters, null));
    }

    @Test
    void isValid_WithShortCardNumber_ShouldReturnTrue() {
        validFilters.setCardNumber("1234");
        assertTrue(validator.isValid(validFilters, null));
    }

    @Test
    void isValid_WithEmptyCardNumber_ShouldReturnTrue() {
        validFilters.setCardNumber("");
        assertTrue(validator.isValid(validFilters, null));
    }

    @Test
    void isValid_WithPastMinEndDate_ShouldReturnFalse() {
        validFilters.setMinEndDate(LocalDate.now().minusDays(1));
        assertFalse(validator.isValid(validFilters, null));
    }

    @Test
    void isValid_WithFutureMinEndDate_ShouldReturnTrue() {
        validFilters.setMinEndDate(LocalDate.now().plusDays(1));
        assertTrue(validator.isValid(validFilters, null));
    }

    @Test
    void isValid_WithPastMaxEndDate_ShouldReturnFalse() {
        validFilters.setMaxEndDate(LocalDate.now().minusDays(1));
        assertFalse(validator.isValid(validFilters, null));
    }

    @Test
    void isValid_WithMaxEndDateBeforeMinEndDate_ShouldReturnFalse() {
        validFilters.setMinEndDate(LocalDate.now().plusDays(10));
        validFilters.setMaxEndDate(LocalDate.now().plusDays(5));
        assertFalse(validator.isValid(validFilters, null));
    }

    @Test
    void isValid_WithValidDateRange_ShouldReturnTrue() {
        validFilters.setMinEndDate(LocalDate.now().plusDays(5));
        validFilters.setMaxEndDate(LocalDate.now().plusDays(10));
        assertTrue(validator.isValid(validFilters, null));
    }

    @Test
    void isValid_WithMinBalanceGreaterThanMaxBalance_ShouldReturnFalse() {
        validFilters.setMinBalance(1000.0);
        validFilters.setMaxBalance(500.0);
        assertFalse(validator.isValid(validFilters, null));
    }

    @Test
    void isValid_WithEqualMinMaxBalance_ShouldReturnTrue() {
        validFilters.setMinBalance(500.0);
        validFilters.setMaxBalance(500.0);
        assertTrue(validator.isValid(validFilters, null));
    }

    @Test
    void isValid_WithDefaultBalanceValues_ShouldReturnTrue() {
        validFilters.setMinBalance(FilterPageCardDTO.DEFAULT_MIN_BALANCE);
        validFilters.setMaxBalance(FilterPageCardDTO.DEFAULT_MAX_BALANCE);
        assertTrue(validator.isValid(validFilters, null));
    }

    @Test
    void isValid_WithNullValues_ShouldHandleGracefully() {
        FilterPageCardDTO nullFilters = new FilterPageCardDTO();
        nullFilters.setDirectionSort("asc");
        nullFilters.setSortBy("id");
        nullFilters.setPage(0);
        nullFilters.setSize(10);
        
        assertTrue(validator.isValid(nullFilters, null));
    }
}