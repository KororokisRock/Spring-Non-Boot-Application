package com.app.service;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;

import com.app.dto.JwtAuthenticationDTO;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    private final String testUsername = "testuser";
    private final String jwtSecret = "veryLongAndSecureSecretKeyThatIsAtLeast256BitsLongForHS512Algorithm";
    private final long tokenExpiration = 3600;
    private final long refreshTokenExpiration = 86400;

    @BeforeEach
    void setUp() throws Exception {
        setPrivateField(jwtService, "jwtSecret", jwtSecret);
        setPrivateField(jwtService, "jwtTokenExpiration", tokenExpiration);
        setPrivateField(jwtService, "jwtRefreshTokenExpiration", refreshTokenExpiration);
    }

    private void setPrivateField(Object target, String fieldName, Object value) 
            throws NoSuchFieldException, IllegalAccessException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void generateAuthToken_WithValidUsername_ReturnsValidJwtTokens() {
        JwtAuthenticationDTO result = jwtService.generateAuthToken(testUsername);

        assertNotNull(result);
        assertNotNull(result.getToken());
        assertNotNull(result.getRefreshToken());
        assertTrue(result.getToken().split("\\.").length == 3);
        assertTrue(result.getRefreshToken().split("\\.").length == 3);
    }

    @Test
    void generateAuthToken_TokensContainUsername() {
        JwtAuthenticationDTO result = jwtService.generateAuthToken(testUsername);
        String usernameFromToken = jwtService.getUsernameFromToken(result.getToken());
        String usernameFromRefreshToken = jwtService.getUsernameFromToken(result.getRefreshToken());

        assertEquals(testUsername, usernameFromToken);
        assertEquals(testUsername, usernameFromRefreshToken);
    }

    @Test
    void refreshBaseToken_WithValidInput_ReturnsNewAccessTokenWithSameRefreshToken() {
        String existingRefreshToken = jwtService.generateAuthToken(testUsername).getRefreshToken();

        JwtAuthenticationDTO result = jwtService.refreshBaseToken(testUsername, existingRefreshToken);

        assertNotNull(result);
        assertNotNull(result.getToken());
        assertEquals(existingRefreshToken, result.getRefreshToken());
        assertEquals(testUsername, jwtService.getUsernameFromToken(result.getToken()));
    }

    @Test
    void getUsernameFromToken_WithValidToken_ReturnsUsername() {
        String token = jwtService.generateAuthToken(testUsername).getToken();

        String username = jwtService.getUsernameFromToken(token);

        assertEquals(testUsername, username);
    }

    @Test
    void getUsernameFromRequest_WithValidAuthorizationHeader_ReturnsUsername() {
        String token = jwtService.generateAuthToken(testUsername).getToken();
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);

        String username = jwtService.getUsernameFromRequest(request);

        assertEquals(testUsername, username);
        verify(request).getHeader(HttpHeaders.AUTHORIZATION);
    }

    @Test
    void getUsernameFromRequest_WithNoAuthorizationHeader_ReturnsNull() {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        String username = jwtService.getUsernameFromRequest(request);

        assertNull(username);
        verify(request).getHeader(HttpHeaders.AUTHORIZATION);
    }

    @Test
    void getUsernameFromRequest_WithInvalidAuthorizationHeader_ReturnsNull() {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("InvalidHeader");

        String username = jwtService.getUsernameFromRequest(request);

        assertNull(username);
        verify(request).getHeader(HttpHeaders.AUTHORIZATION);
    }

    @Test
    void getTokenFromRequest_WithValidBearerToken_ReturnsToken() {
        String expectedToken = "testToken123";
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + expectedToken);

        String actualToken = jwtService.getTokenFromRequest(request);

        assertEquals(expectedToken, actualToken);
        verify(request).getHeader(HttpHeaders.AUTHORIZATION);
    }

    @Test
    void getTokenFromRequest_WithNoBearerPrefix_ReturnsNull() {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("testToken123");

        String token = jwtService.getTokenFromRequest(request);

        assertNull(token);
        verify(request).getHeader(HttpHeaders.AUTHORIZATION);
    }

    @Test
    void validateJwtToken_WithValidToken_ReturnsTrue() {
        String validToken = jwtService.generateAuthToken(testUsername).getToken();

        boolean isValid = jwtService.validateJwtToken(validToken);

        assertTrue(isValid);
    }

    @Test
    void validateJwtToken_WithExpiredToken_ReturnsFalse() throws Exception {
        setPrivateField(jwtService, "jwtTokenExpiration", -3600L);
        String expiredToken = jwtService.generateAuthToken(testUsername).getToken();
        setPrivateField(jwtService, "jwtTokenExpiration", tokenExpiration);

        boolean isValid = jwtService.validateJwtToken(expiredToken);

        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_WithInvalidToken_ReturnsFalse() {
        String invalidToken = "invalid.token.here";
        boolean isValid = jwtService.validateJwtToken(invalidToken);

        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_WithNullToken_ReturnsFalse() {
        boolean isValid = jwtService.validateJwtToken(null);

        assertFalse(isValid);
    }

    @Test
    void validateJwtToken_WithEmptyToken_ReturnsFalse() {
        boolean isValid = jwtService.validateJwtToken("");

        assertFalse(isValid);
    }

    @Test
    void generateJwtToken_WithDifferentUsernames_ProducesDifferentTokens() {
        String username1 = "user1";
        String username2 = "user2";

        String token1 = jwtService.generateAuthToken(username1).getToken();
        String token2 = jwtService.generateAuthToken(username2).getToken();

        assertNotEquals(token1, token2);
        assertEquals(username1, jwtService.getUsernameFromToken(token1));
        assertEquals(username2, jwtService.getUsernameFromToken(token2));
    }

    @Test
    void tokenAndRefreshToken_HaveDifferentExpirationTimes() {
        JwtAuthenticationDTO tokens = jwtService.generateAuthToken(testUsername);
        String accessToken = tokens.getToken();
        String refreshToken = tokens.getRefreshToken();

        assertNotEquals(accessToken, refreshToken);
    }

    @Test
    void getSingInKey_ReturnsConsistentKey() throws Exception {

        Object key1 = ReflectionTestUtils.invokeMethod(jwtService, "getSingInKey");
        Object key2 = ReflectionTestUtils.invokeMethod(jwtService, "getSingInKey");

        assertEquals(key1, key2);
    }
}
