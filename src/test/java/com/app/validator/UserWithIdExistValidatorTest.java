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

import com.app.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserWithIdExistValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserWithIdExistValidator validator;

    @BeforeEach
    void setUp() {
        validator.initialize(mock(UserWithIdExist.class));
    }

    @Test
    void isValid_WhenUserIdExists_ShouldReturnTrue() {
        Integer userId = 1;
        when(userRepository.existsById(userId)).thenReturn(true);

        boolean result = validator.isValid(userId, null);

        assertTrue(result);
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void isValid_WhenUserIdDoesNotExist_ShouldReturnFalse() {
        Integer userId = 999;
        when(userRepository.existsById(userId)).thenReturn(false);

        boolean result = validator.isValid(userId, null);

        assertFalse(result);
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void isValid_WhenUserIdIsNull_ShouldReturnTrue() {
        boolean result = validator.isValid(null, null);

        assertTrue(result);
        verify(userRepository, never()).existsById(any());
    }

    @Test
    void isValid_WithNegativeUserId_ShouldCheckRepository() {
        Integer userId = -1;
        when(userRepository.existsById(userId)).thenReturn(false);

        boolean result = validator.isValid(userId, null);

        assertFalse(result);
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void isValid_WithZeroUserId_ShouldCheckRepository() {
        Integer userId = 0;
        when(userRepository.existsById(userId)).thenReturn(false);

        boolean result = validator.isValid(userId, null);

        assertFalse(result);
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void isValid_WithMultipleUserIds_ShouldCheckEachOne() {
        Integer userId1 = 1;
        Integer userId2 = 2;
        
        when(userRepository.existsById(userId1)).thenReturn(true);
        when(userRepository.existsById(userId2)).thenReturn(false);

        assertTrue(validator.isValid(userId1, null));
        assertFalse(validator.isValid(userId2, null));
        
        verify(userRepository, times(1)).existsById(userId1);
        verify(userRepository, times(1)).existsById(userId2);
    }

    @Test
    void isValid_WithMaxIntegerValue_ShouldHandleCorrectly() {
        Integer userId = Integer.MAX_VALUE;
        when(userRepository.existsById(userId)).thenReturn(false);

        boolean result = validator.isValid(userId, null);

        assertFalse(result);
        verify(userRepository, times(1)).existsById(userId);
    }
}