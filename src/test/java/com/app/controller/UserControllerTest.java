package com.app.controller;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.app.annotation.ValidateBindingResult;
import com.app.aspect.ValidationAspect;
import com.app.dto.UserDTO;
import com.app.dto.UsernameDTO;
import com.app.exception.ValidationValueException;
import com.app.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserController proxiedController;
    private ValidationAspect validationAspect;

    @BeforeEach
    void setUp() {
        validationAspect = new ValidationAspect();
        
        AspectJProxyFactory factory = new AspectJProxyFactory(userController);
        factory.addAspect(validationAspect);
        proxiedController = factory.getProxy();
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        List<UserDTO> expectedUsers = Arrays.asList(
            createUserDTO(1, "user1", "ROLE_USER"),
            createUserDTO(2, "user2", "ROLE_ADMIN")
        );
        
        when(userService.getAllUsersAsDTO()).thenReturn(expectedUsers);

        List<UserDTO> result = proxiedController.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("ROLE_ADMIN", result.get(1).getRole());
        
        verify(userService, times(1)).getAllUsersAsDTO();
    }

    @Test
    void deleteUser_WithValidUsername_ShouldReturnOkResponse() {
        UsernameDTO usernameDTO = new UsernameDTO();
        usernameDTO.setUsername("testuser");
        
        BindingResult bindingResult = new BeanPropertyBindingResult(usernameDTO, "usernameDTO");

        ResponseEntity<?> response = proxiedController.deleteUser(usernameDTO, bindingResult);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User delete successfully", response.getBody());
        
        verify(userService, times(1)).deleteUserByUsername(usernameDTO);
    }

    @Test
    void deleteUser_WithValidationErrors_ShouldThrowValidationValueException() {
        UsernameDTO usernameDTO = new UsernameDTO();
        usernameDTO.setUsername("");
        
        BindingResult bindingResult = new BeanPropertyBindingResult(usernameDTO, "usernameDTO");
        bindingResult.addError(new FieldError("usernameDTO", "username", "Username is required"));

        ValidationValueException exception = assertThrows(
            ValidationValueException.class,
            () -> proxiedController.deleteUser(usernameDTO, bindingResult)
        );

        assertNotNull(exception.getValidationErrors());
        assertTrue(exception.getValidationErrors().containsKey("username"));
        assertEquals("Username is required", exception.getValidationErrors().get("username"));
        
        verify(userService, never()).deleteUserByUsername(any());
    }

    @Test
    void deleteUser_WithNullUsername_ShouldThrowValidationValueException() {
        UsernameDTO usernameDTO = new UsernameDTO();
        
        BindingResult bindingResult = new BeanPropertyBindingResult(usernameDTO, "usernameDTO");
        bindingResult.addError(new FieldError("usernameDTO", "username", "Username is required"));

        ValidationValueException exception = assertThrows(
            ValidationValueException.class,
            () -> proxiedController.deleteUser(usernameDTO, bindingResult)
        );

        assertNotNull(exception.getValidationErrors());
        assertTrue(exception.getValidationErrors().containsKey("username"));
        
        verify(userService, never()).deleteUserByUsername(any());
    }

    @Test
    void deleteUser_WithShortUsername_ShouldThrowValidationValueException() {
        UsernameDTO usernameDTO = new UsernameDTO();
        usernameDTO.setUsername("a");
        
        BindingResult bindingResult = new BeanPropertyBindingResult(usernameDTO, "usernameDTO");
        bindingResult.addError(new FieldError("usernameDTO", "username", 
            "Username must be between 2 and 50 characters"));

        ValidationValueException exception = assertThrows(
            ValidationValueException.class,
            () -> proxiedController.deleteUser(usernameDTO, bindingResult)
        );

        assertNotNull(exception.getValidationErrors());
        assertTrue(exception.getValidationErrors().containsKey("username"));
        
        verify(userService, never()).deleteUserByUsername(any());
    }

    @Test
    void controllerMethods_ShouldBeAnnotatedWithValidateBindingResult() throws NoSuchMethodException {
        assertTrue(UserController.class.getMethod("getAllUsers")
            .isAnnotationPresent(ValidateBindingResult.class));
        
        assertTrue(UserController.class.getMethod("deleteUser", UsernameDTO.class, BindingResult.class)
            .isAnnotationPresent(ValidateBindingResult.class));
    }

    private UserDTO createUserDTO(Integer id, String username, String role) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);
        userDTO.setUsername(username);
        userDTO.setRole(role);
        return userDTO;
    }
}