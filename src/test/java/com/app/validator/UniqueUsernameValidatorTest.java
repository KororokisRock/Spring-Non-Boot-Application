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
import org.slf4j.Logger;

import com.app.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UniqueUsernameValidatorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Logger logger;

    @InjectMocks
    private UniqueUsernameValidator validator;

    @BeforeEach
    void setUp() {
        validator.initialize(mock(UniqueUsername.class));
    }

    @Test
    void isValid_WhenUsernameDoesNotExist_ShouldReturnTrue() {
        String username = "newuser";
        when(userRepository.existsByUsername(username)).thenReturn(false);

        boolean result = validator.isValid(username, null);

        assertTrue(result);
        verify(userRepository, times(1)).existsByUsername(username);
    }

    @Test
    void isValid_WhenUsernameExists_ShouldReturnFalse() {
        String username = "existinguser";
        when(userRepository.existsByUsername(username)).thenReturn(true);

        boolean result = validator.isValid(username, null);

        assertFalse(result);
        verify(userRepository, times(1)).existsByUsername(username);
    }

    @Test
    void isValid_WhenUsernameIsNull_ShouldReturnTrue() {
        boolean result = validator.isValid(null, null);

        assertTrue(result);
        verify(userRepository, never()).existsByUsername(any());
    }

    @Test
    void isValid_WhenUsernameIsEmpty_ShouldCheckRepository() {
        when(userRepository.existsByUsername("")).thenReturn(false);

        boolean result = validator.isValid("", null);

        assertTrue(result);
        verify(userRepository, times(1)).existsByUsername("");
    }

    @Test
    void isValid_WithDifferentUsernames_ShouldCheckEachOne() {
        String username1 = "user1";
        String username2 = "user2";
        
        when(userRepository.existsByUsername(username1)).thenReturn(false);
        when(userRepository.existsByUsername(username2)).thenReturn(true);
        assertTrue(validator.isValid(username1, null));
        assertFalse(validator.isValid(username2, null));
        
        verify(userRepository, times(1)).existsByUsername(username1);
        verify(userRepository, times(1)).existsByUsername(username2);
    }

    @Test
    void isValid_WithSpecialCharacters_ShouldHandleCorrectly() {
        String username = "user@test.com";
        when(userRepository.existsByUsername(username)).thenReturn(false);

        boolean result = validator.isValid(username, null);

        assertTrue(result);
        verify(userRepository, times(1)).existsByUsername(username);
    }
}