package com.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import com.app.aspect.ValidationAspect;
import com.app.dto.RegistrationDTO;
import com.app.exception.AuthenticationFailedException;
import com.app.service.UserService;

@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private BindingResult bindingResult;

    @Spy
    private ValidationAspect aspect;

    @InjectMocks
    private RegistrationController registrationController;

    @Test
    void setRegistration_ValidRegistration_ReturnsSuccessResponse() throws Exception {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setUsername("testuser");
        registrationDTO.setPassword("password123");
        registrationDTO.setPasswordConfirm("password123");

        ResponseEntity<?> response = registrationController.setRegistration(registrationDTO, bindingResult);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Registration complete", response.getBody());
        verify(userService).registerUser(registrationDTO);
    }

    @Test
    void setRegistration_PasswordMismatch_ThrowsAuthenticationException() throws Exception {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setUsername("testuser");
        registrationDTO.setPassword("password123");
        registrationDTO.setPasswordConfirm("differentpassword");

        doThrow(new AuthenticationFailedException("Password not confirm"))
            .when(userService).registerUser(any(RegistrationDTO.class));

        assertThrows(AuthenticationFailedException.class, () -> 
            registrationController.setRegistration(registrationDTO, bindingResult));
        
        verify(userService).registerUser(registrationDTO);
    }
}
