package com.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.annotation.ValidateBindingResult;
import com.app.dto.UserDTO;
import com.app.dto.UsernameDTO;
import com.app.service.UserService;


@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/all")
    @ValidateBindingResult
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsersAsDTO();
    }

    @PostMapping("/delete")
    @ValidateBindingResult
    public ResponseEntity<?> deleteUser(@RequestBody UsernameDTO username, BindingResult result) {
        userService.deleteUserByUsername(username);
        return ResponseEntity.ok("User delete successfully");
    }
}
