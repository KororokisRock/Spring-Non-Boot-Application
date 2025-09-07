package com.app.security;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.app.model.User;

class CustomUserDetailsTest {

    @Test
    void customUserDetails_ShouldReturnCorrectAuthorities() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setRole("ROLE_ADMIN");

        // Act
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        // Assert
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
    }

    @Test
    void customUserDetails_WithDifferentRoles_ShouldReturnCorrectAuthorities() {
        // Arrange
        User user1 = new User();
        user1.setRole("ROLE_USER");
        
        User user2 = new User();
        user2.setRole("ROLE_ADMIN");

        // Act
        CustomUserDetails details1 = new CustomUserDetails(user1);
        CustomUserDetails details2 = new CustomUserDetails(user2);

        // Assert
        assertTrue(details1.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(details2.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void customUserDetails_UserDetailsMethods_ShouldReturnExpectedValues() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole("ROLE_USER");

        // Act
        CustomUserDetails userDetails = new CustomUserDetails(user);

        // Assert
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }
}