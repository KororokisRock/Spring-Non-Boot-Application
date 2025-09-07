package com.app.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserCredentialsDTO credentialsDTO;
    private RegistrationDTO registrationDTO;
    private RefreshTokenDTO refreshTokenDTO;
    private JwtAuthenticationDTO jwtAuthDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRole("ROLE_USER");

        credentialsDTO = new UserCredentialsDTO();
        credentialsDTO.setUsername("testuser");
        credentialsDTO.setPassword("password123");

        registrationDTO = new RegistrationDTO();
        registrationDTO.setUsername("newuser");
        registrationDTO.setPassword("password123");
        registrationDTO.setPasswordConfirm("password123");

        refreshTokenDTO = new RefreshTokenDTO();
        refreshTokenDTO.setRefreshToken("validRefreshToken");

        jwtAuthDTO = new JwtAuthenticationDTO();
        jwtAuthDTO.setToken("accessToken");
        jwtAuthDTO.setRefreshToken("refreshToken");
    }

    @Test
    void signIn_WithValidCredentials_ReturnsJwtTokens() throws UserNotFoundException {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateAuthToken(anyString())).thenReturn(jwtAuthDTO);

        JwtAuthenticationDTO result = userService.singIn(credentialsDTO);

        assertNotNull(result);
        assertEquals("accessToken", result.getToken());
        assertEquals("refreshToken", result.getRefreshToken());
        verify(userRepo).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtService).generateAuthToken("testuser");
    }

    @Test
    void signIn_WithInvalidUsername_ThrowsUserNotFoundException() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.singIn(credentialsDTO));
        verify(userRepo).findByUsername("testuser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void signIn_WithInvalidPassword_ThrowsUserNotFoundException() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.singIn(credentialsDTO));
        verify(userRepo).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", "encodedPassword");
    }

    @Test
    void refreshToken_WithValidRefreshToken_ReturnsNewAccessToken() throws InvalidRefreshTokenException, UserNotFoundException {
        when(jwtService.validateJwtToken(anyString())).thenReturn(true);
        when(jwtService.getUsernameFromToken(anyString())).thenReturn("testuser");
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(jwtService.refreshBaseToken(anyString(), anyString())).thenReturn(jwtAuthDTO);

        JwtAuthenticationDTO result = userService.refreshToken(refreshTokenDTO);

        assertNotNull(result);
        assertEquals("accessToken", result.getToken());
        verify(jwtService).validateJwtToken("validRefreshToken");
        verify(jwtService).getUsernameFromToken("validRefreshToken");
        verify(userRepo).findByUsername("testuser");
        verify(jwtService).refreshBaseToken("testuser", "validRefreshToken");
    }

    @Test
    void refreshToken_WithInvalidRefreshToken_ThrowsInvalidRefreshTokenException() {
        when(jwtService.validateJwtToken(anyString())).thenReturn(false);

        assertThrows(InvalidRefreshTokenException.class, () -> userService.refreshToken(refreshTokenDTO));
        verify(jwtService).validateJwtToken("validRefreshToken");
        verify(jwtService, never()).getUsernameFromToken(anyString());
    }

    @Test
    void refreshToken_WithNonExistentUser_ThrowsUserNotFoundException() {
        when(jwtService.validateJwtToken(anyString())).thenReturn(true);
        when(jwtService.getUsernameFromToken(anyString())).thenReturn("nonexistent");
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.refreshToken(refreshTokenDTO));
        verify(jwtService).validateJwtToken("validRefreshToken");
        verify(jwtService).getUsernameFromToken("validRefreshToken");
        verify(userRepo).findByUsername("nonexistent");
    }

    @Test
    void registerUser_WithMatchingPasswords_SavesUser() throws AuthenticationFailedException {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        userService.registerUser(registrationDTO);

        verify(passwordEncoder).encode("password123");
        verify(userRepo).save(any(User.class));
    }

    @Test
    void registerUser_WithNonMatchingPasswords_ThrowsAuthenticationFailedException() {
        registrationDTO.setPasswordConfirm("differentPassword");

        assertThrows(AuthenticationFailedException.class, () -> userService.registerUser(registrationDTO));
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void deleteUserByUsername_WithExistingUser_DeletesUser() {
        UsernameDTO usernameDTO = new UsernameDTO();
        usernameDTO.setUsername("testuser");
        when(userRepo.existsByUsername(anyString())).thenReturn(true);

        userService.deleteUserByUsername(usernameDTO);

        verify(userRepo).existsByUsername("testuser");
        verify(userRepo).deleteByUsername("testuser");
    }

    @Test
    void deleteUserByUsername_WithNonExistingUser_ThrowsUserNotFoundException() {
        UsernameDTO usernameDTO = new UsernameDTO();
        usernameDTO.setUsername("nonexistent");
        when(userRepo.existsByUsername(anyString())).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUserByUsername(usernameDTO));
        verify(userRepo).existsByUsername("nonexistent");
        verify(userRepo, never()).deleteByUsername(anyString());
    }

    @Test
    void getAllUsersAsDTO_ReturnsListOfUserDTOs() {
        User user1 = new User();
        user1.setId(1);
        user1.setUsername("user1");
        user1.setRole("ROLE_USER");

        User user2 = new User();
        user2.setId(2);
        user2.setUsername("user2");
        user2.setRole("ROLE_ADMIN");

        List<User> users = Arrays.asList(user1, user2);
        when(userRepo.findAll()).thenReturn(users);

        List<UserDTO> result = userService.getAllUsersAsDTO();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getUsername());
        assertEquals("user2", result.get(1).getUsername());
        verify(userRepo).findAll();
    }

    @Test
    void findByCredentials_WithValidCredentials_ReturnsUser() throws UserNotFoundException {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        User result = userService.findByCredentials(credentialsDTO);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepo).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", "encodedPassword");
    }

    @Test
    void findByCredentials_WithInvalidCredentials_ThrowsUserNotFoundException() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.findByCredentials(credentialsDTO));
        verify(userRepo).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", "encodedPassword");
    }
}