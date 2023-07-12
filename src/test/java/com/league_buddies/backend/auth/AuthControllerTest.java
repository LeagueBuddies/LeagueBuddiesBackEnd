package com.league_buddies.backend.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.league_buddies.backend.exception.ApiException;
import com.league_buddies.backend.exception.CustomExceptionHandler;
import com.league_buddies.backend.security.jwt.JwtService;
import com.league_buddies.backend.user.User;
import com.league_buddies.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AuthControllerTest.class)
class AuthControllerTest {
    private AuthController authController;

    private AuthService authService;

    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String username;

    private String password;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        authService = new AuthService(jwtService, userRepository);
        authController = new AuthController(authService);

        username = "username";
        password = "password";


        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(CustomExceptionHandler.class)
                .build();
    }

    @Test
    public void throwsWhenRegisterWithAnAlreadyRegisteredEmail() throws Exception {
        // Arrange
        when(userRepository.findByEmailAddress(anyString())).thenReturn(
                Optional.of(new User(username, password))
        );
        AuthRequest authRequest = new AuthRequest(username, password);

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                        post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper
                                        .writeValueAsString(authRequest))
        ).andReturn().getResponse();

        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
        assertEquals("Username is already taken.", apiException.getMessage());
    }

    @Test
    public void throwsWhenRegisterIsMissingUsernameOrPassword() throws Exception {
        AuthRequest authRequest = new AuthRequest(username, "");

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest))
        ).andReturn().getResponse();

        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Both a username and a password are needed.", apiException.getMessage());
    }

    @Test
    public void canRegister() throws Exception {
        // Arrange
        when(userRepository.findByEmailAddress(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(new User(username, password));

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(
                                        new AuthRequest(username, password)
                                ))
        ).andReturn().getResponse();

        AuthResponse authResponse = objectMapper.readValue(response.getContentAsString(), AuthResponse.class);


        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(authResponse.token() instanceof String);
    }

    @Test
    public void throwsWhenLoggingIsMissingUsernameOrPassword() throws Exception {
        // Act
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper
                                .writeValueAsString(new AuthRequest(username, null)))
        ).andReturn().getResponse();

        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Both a username and a password are needed.", apiException.getMessage());
    }

    @Test
    public void throwsWhenLoggingWithIncorrectPassword() throws Exception {
        // Arrange
        when(userRepository.findByEmailAddress(anyString())).thenReturn(
                Optional.of(new User(username, "wrongPassword")));

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AuthRequest(username, password)))
        ).andReturn().getResponse();

        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertEquals("Password entered is incorrect.", apiException.getMessage());
    }

    @Test
    public void throwsWhenLoggingWithUsernameThatDoesNotExist() throws Exception {
        // Arrange
        when(userRepository.findByEmailAddress(anyString())).thenReturn(Optional.empty());

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AuthRequest(username, password)))
        ).andReturn().getResponse();

        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals("The user was not found using the email you've provided.", apiException.getMessage());
    }

    @Test
    public void canLoggin() {

    }
}