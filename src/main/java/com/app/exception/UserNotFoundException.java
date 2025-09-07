package com.app.exception;

public class UserNotFoundException extends AppException {
    public UserNotFoundException(String username) {
        super("User with username '" + username + "' not found", "USER_NOT_FOUND");
    }
    
    public UserNotFoundException(Integer userId) {
        super("User with ID '" + userId + "' not found", "USER_NOT_FOUND");
    }
}
