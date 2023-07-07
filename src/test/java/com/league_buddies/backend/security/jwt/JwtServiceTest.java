package com.league_buddies.backend.security.jwt;

import com.league_buddies.backend.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;


class JwtServiceTest {
    private JwtService jwtService;
    private String username;
    private String token;
    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        username = "username";
        token = jwtService.generateToken(username);
    }

    @Test
    void generatesToken() {
        // Assert
        assertNotNull(token);
    }

    @Test
    void checksIfTokenIsValid() {
        // Arrange
        UserDetails userDetails = new User(username, "1234");
        UserDetails badUserDetails = new User("badusername", "1234");
        // badToken has a different username.
        String badToken = jwtService.generateToken(username);

        // Act
        boolean isTokenValid = jwtService.isTokenValid(token, userDetails);
        boolean isBadTokenValid = jwtService.isTokenValid(badToken, badUserDetails);

        // Assert
        assertTrue(isTokenValid);
        assertFalse(isBadTokenValid);
    }
}