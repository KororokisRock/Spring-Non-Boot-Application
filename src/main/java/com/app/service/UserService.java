package com.app.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.dto.JwtAuthenticationDTO;
import com.app.dto.RefreshTokenDTO;
import com.app.dto.RegistrationDTO;
import com.app.dto.UserCredentialsDTO;
import com.app.dto.UserDTO;
import com.app.dto.UsernameDTO;
import com.app.exception.AuthenticationFailedException;
import com.app.exception.InvalidRefreshTokenException;
import com.app.exception.UserNotFoundException;
import com.app.model.User;
import com.app.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public JwtAuthenticationDTO singIn(UserCredentialsDTO userCredentialsDTO) throws UserNotFoundException {
        User user = findByCredentials(userCredentialsDTO);
        return jwtService.generateAuthToken(user.getUsername());
    }

    public JwtAuthenticationDTO refreshToken(RefreshTokenDTO refreshTokenDTO) throws InvalidRefreshTokenException, UserNotFoundException {
        String refreshToken = refreshTokenDTO.getRefreshToken();
        if (refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
            User user = findByUsername(jwtService.getUsernameFromToken(refreshToken));
            return jwtService.refreshBaseToken(user.getUsername(), refreshToken);
        }
        throw new InvalidRefreshTokenException();
    }

    public void registerUser(RegistrationDTO registerDTO) throws AuthenticationFailedException {
        if (registerDTO.getPassword().equals(registerDTO.getPasswordConfirm())) {
            User user = new User();
            user.setUsername(registerDTO.getUsername());
            user.setRole("ROLE_USER");
            user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
            userRepo.save(user);
        } else {
            throw new AuthenticationFailedException("Password not confirm");
        }
    }

    public User findByCredentials(UserCredentialsDTO userCredentialsDTO) throws UserNotFoundException {
        Optional<User> optionalUser = userRepo.findByUsername(userCredentialsDTO.getUsername());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(userCredentialsDTO.getPassword(), user.getPassword())) {
                return user;
            }
        }
        throw new UserNotFoundException(userCredentialsDTO.getUsername());
    }

    public void deleteUserByUsername(UsernameDTO usernameDTO) {
        if (!userRepo.existsByUsername(usernameDTO.getUsername())) {
            throw new UserNotFoundException(usernameDTO.getUsername());
        }
        userRepo.deleteByUsername(usernameDTO.getUsername());
    }

    public List<UserDTO> getAllUsersAsDTO() {
        List<User> users = userRepo.findAll();
        List<UserDTO> userDTOs = users.stream().map(UserDTO::new).collect(Collectors.toList());
        return userDTOs;
    }

    private User findByUsername(String username) throws UserNotFoundException {
        return userRepo.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }
}
