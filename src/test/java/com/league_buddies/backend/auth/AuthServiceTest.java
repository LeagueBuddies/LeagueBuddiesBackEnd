package com.league_buddies.backend.auth;

import com.league_buddies.backend.exception.IllegalArgumentException;
import com.league_buddies.backend.exception.InvalidPasswordException;
import com.league_buddies.backend.exception.UserNotFoundException;
import com.league_buddies.backend.exception.UsernameAlreadyExistsException;
import com.league_buddies.backend.security.jwt.JwtService;
import com.league_buddies.backend.user.User;
import com.league_buddies.backend.user.UserRepository;
import com.league_buddies.backend.util.MessageResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    private MessageResolver messageResolver;

    private final String username = "username";

    private final String password = "password";

    private final String fakeToken = "JWTTokenFake";

    private final User user = new User(username, password);

    @BeforeEach
    void setUp() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");

        messageResolver = new MessageResolver(messageSource);
        authService = new AuthService(jwtService, userRepository, passwordEncoder, messageResolver);
    }

    @Test
    void canRegister() {
        // Arrange
        when(userRepository.save(any())).thenReturn(user);
        when(jwtService.generateToken(anyString())).thenReturn(fakeToken);

        // Act
        AuthResponse authResponse = authService.register(new AuthRequest(username, password));

        // Assert
        assertEquals(fakeToken, authResponse.token());
    }

    @Test
    void registerThrowsWhenMissingUsernameOrPassword() {
        // Arrange
        String emptyPassword = "";

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> authService.register(new AuthRequest(username, emptyPassword))
        );

        // Assert
        assertEquals(messageResolver.getMessage("illegalArgument"), exception.getMessage());
    }

    @Test
    void registerThrowsWhenUserAlreadyExists() {
        // Arrange
        when(userRepository.findByEmailAddress(anyString())).thenReturn(Optional.of(user));

        // Act
        UsernameAlreadyExistsException exception = assertThrows(
                UsernameAlreadyExistsException.class, () -> authService.register(new AuthRequest(username, password))
        );

        // Assert
        assertEquals(messageResolver.getMessage(
                "usernameAlreadyExists",
                new Object[] {username}
        ), exception.getMessage());
    }

    @Test
    void canLogin() {
        // Arrange
        when(userRepository.findByEmailAddress(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(anyString())).thenReturn(fakeToken);
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        // Act
        AuthResponse authResponse = authService.login(new AuthRequest(username, password));

        // Assert
        assertEquals(fakeToken, authResponse.token());
    }

    @Test
    void loginThrowsWhenMissingUsernameOrPassword() {
        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> authService.login(new AuthRequest("", password))
        );

        // Assert
        assertEquals(messageResolver.getMessage("illegalArgument"), exception.getMessage());
    }

    @Test
    void loginThrowsWhenPasswordIsIncorrect() {
        // Arrange
        when(userRepository.findByEmailAddress(anyString())).thenReturn(Optional.of(user));

        // Act
        InvalidPasswordException exception = assertThrows(
                InvalidPasswordException.class, () -> authService.login(new AuthRequest(username, "badPassword"))
        );

        // Assert
        assertEquals(messageResolver.getMessage("invalidPassword"), exception.getMessage());
    }

    @Test
    void loginThrowsWhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findByEmailAddress(anyString())).thenReturn(Optional.empty());

        // Act
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class, () -> authService.login(new AuthRequest(username, password))
        );

        // Assert
        assertEquals(messageResolver.getMessage("userNotFound"), exception.getMessage());
    }
}