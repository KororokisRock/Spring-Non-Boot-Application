package com.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UsernameDTO {
    @NotBlank(message="Username is required")
    @Size(min=2, max=50, message="Username must be between 2 and 50 characters")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
