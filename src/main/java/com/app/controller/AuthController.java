package com.app.controller;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.annotation.ValidateBindingResult;
import com.app.dto.JwtAuthenticationDTO;
import com.app.dto.RefreshTokenDTO;
import com.app.dto.UserCredentialsDTO;
import com.app.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    
    @PostMapping("/sing-in")
    @ValidateBindingResult
    public ResponseEntity<?> singIn(@RequestBody UserCredentialsDTO userCredentialsDTO) throws AuthenticationException {
        JwtAuthenticationDTO jwtAuthenticationDTO = userService.singIn(userCredentialsDTO);
        return ResponseEntity.ok(jwtAuthenticationDTO);
    }

    @PostMapping("/refresh")
    @ValidateBindingResult
    public JwtAuthenticationDTO refresh(@RequestBody RefreshTokenDTO refreshTokenDTO) throws Exception {
        return userService.refreshToken(refreshTokenDTO);
    }
}
