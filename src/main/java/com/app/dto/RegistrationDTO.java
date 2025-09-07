package com.app.dto;

import com.app.validator.UniqueUsername;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegistrationDTO {

    @NotBlank(message="Username is required")
    @Size(min=2, max=50, message="Username must be between 2 and 50 characters")
    @UniqueUsername
    private String username;

    @NotBlank(message="Password is required")
    @Size(min=3, message="Password must be longer then 3 character")
    private String password;

    @NotBlank(message="Password confirm is required")
    private String passwordConfirm;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
}
