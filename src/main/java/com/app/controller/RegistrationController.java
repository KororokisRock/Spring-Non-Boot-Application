package com.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.annotation.ValidateBindingResult;
import com.app.dto.RegistrationDTO;
import com.app.exception.AuthenticationFailedException;
import com.app.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/register")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @PostMapping
    @ValidateBindingResult
    public ResponseEntity<?> setRegistration(@RequestBody @Valid RegistrationDTO registerDTO, BindingResult result) throws AuthenticationFailedException {
        userService.registerUser(registerDTO);
        return ResponseEntity.ok("Registration complete");
    }
}
