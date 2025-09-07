package com.app.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.app.repository.CardRepository;

@ExtendWith(MockitoExtension.class)
class NoOneHasNumberValidatorTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private NoOneHasNumberValidator validator;

    @BeforeEach
    void setUp() {
        validator.initialize(mock(NoOneHasNumber.class));
    }

    @Test
    void isValid_WhenCardNumberDoesNotExist_ShouldReturnTrue() {
        String cardNumber = "1234567890123456";
        when(cardRepository.existsByCardNumber(cardNumber)).thenReturn(false);

        boolean result = validator.isValid(cardNumber, null);

        assertTrue(result);
        verify(cardRepository, times(1)).existsByCardNumber(cardNumber);
    }

    @Test
    void isValid_WhenCardNumberExists_ShouldReturnFalse() {
        String cardNumber = "1234567890123456";
        when(cardRepository.existsByCardNumber(cardNumber)).thenReturn(true);

        boolean result = validator.isValid(cardNumber, null);

        assertFalse(result);
        verify(cardRepository, times(1)).existsByCardNumber(cardNumber);
    }

    @Test
    void isValid_WhenCardNumberIsNull_ShouldReturnTrue() {
        boolean result = validator.isValid(null, null);

        assertTrue(result);
        verify(cardRepository, never()).existsByCardNumber(any());
    }

    @Test
    void isValid_WhenCardNumberIsEmpty_ShouldCheckRepository() {
        when(cardRepository.existsByCardNumber("")).thenReturn(false);

        boolean result = validator.isValid("", null);

        assertTrue(result);
        verify(cardRepository, times(1)).existsByCardNumber("");
    }

    @Test
    void isValid_WithDifferentCardNumbers_ShouldCheckEachOne() {
        String cardNumber1 = "1111222233334444";
        String cardNumber2 = "5555666677778888";
        
        when(cardRepository.existsByCardNumber(cardNumber1)).thenReturn(false);
        when(cardRepository.existsByCardNumber(cardNumber2)).thenReturn(true);
        assertTrue(validator.isValid(cardNumber1, null));
        assertFalse(validator.isValid(cardNumber2, null));
        
        verify(cardRepository, times(1)).existsByCardNumber(cardNumber1);
        verify(cardRepository, times(1)).existsByCardNumber(cardNumber2);
    }
}