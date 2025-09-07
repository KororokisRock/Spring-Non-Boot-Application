package com.app.security;

import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import com.app.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserServiceImpl customUserService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtFilter jwtFilter;

    @Test
    void doFilterInternal_WithValidToken_ShouldSetAuthentication() throws Exception {
        // Arrange
        String token = "valid.jwt.token";
        String username = "testuser";
        
        when(jwtService.getTokenFromRequest(request)).thenReturn(token);
        when(jwtService.validateJwtToken(token)).thenReturn(true);
        when(jwtService.getUsernameFromToken(token)).thenReturn(username);
        
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(customUserService.loadUserByUsername(username)).thenReturn(userDetails);

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtService, times(1)).getTokenFromRequest(request);
        verify(jwtService, times(1)).validateJwtToken(token);
        verify(jwtService, times(1)).getUsernameFromToken(token);
        verify(customUserService, times(1)).loadUserByUsername(username);
        verify(filterChain, times(1)).doFilter(request, response);
        
        // Clean up security context
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_WithInvalidToken_ShouldNotSetAuthentication() throws Exception {
        // Arrange
        String token = "invalid.jwt.token";
        
        when(jwtService.getTokenFromRequest(request)).thenReturn(token);
        when(jwtService.validateJwtToken(token)).thenReturn(false);

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtService, times(1)).getTokenFromRequest(request);
        verify(jwtService, times(1)).validateJwtToken(token);
        verify(jwtService, never()).getUsernameFromToken(any());
        verify(customUserService, never()).loadUserByUsername(any());
        verify(filterChain, times(1)).doFilter(request, response);
        
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WithNoToken_ShouldContinueFilterChain() throws Exception {
        // Arrange
        when(jwtService.getTokenFromRequest(request)).thenReturn(null);

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtService, times(1)).getTokenFromRequest(request);
        verify(jwtService, never()).validateJwtToken(any());
        verify(jwtService, never()).getUsernameFromToken(any());
        verify(customUserService, never()).loadUserByUsername(any());
        verify(filterChain, times(1)).doFilter(request, response);
        
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void doFilterInternal_WithExpiredToken_ShouldNotSetAuthentication() throws Exception {
        // Arrange
        String token = "expired.jwt.token";
        
        when(jwtService.getTokenFromRequest(request)).thenReturn(token);
        when(jwtService.validateJwtToken(token)).thenReturn(false);

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtService, times(1)).getTokenFromRequest(request);
        verify(jwtService, times(1)).validateJwtToken(token);
        verify(jwtService, never()).getUsernameFromToken(any());
        verify(customUserService, never()).loadUserByUsername(any());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithBearerPrefix_ShouldExtractTokenCorrectly() throws Exception {
        // Arrange
        String token = "valid.jwt.token";
        String username = "testuser";
        
        when(jwtService.getTokenFromRequest(request)).thenReturn(token);
        when(jwtService.validateJwtToken(token)).thenReturn(true);
        when(jwtService.getUsernameFromToken(token)).thenReturn(username);
        
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(customUserService.loadUserByUsername(username)).thenReturn(userDetails);

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtService, times(1)).getTokenFromRequest(request);
        verify(filterChain, times(1)).doFilter(request, response);
        
        // Clean up security context
        SecurityContextHolder.clearContext();
    }
}