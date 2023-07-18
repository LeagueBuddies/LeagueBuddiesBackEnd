package com.league_buddies.backend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.league_buddies.backend.exception.*;
import com.league_buddies.backend.exception.IllegalArgumentException;
import com.league_buddies.backend.security.jwt.JwtService;
import com.league_buddies.backend.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String username;

    private String password;

    private AuthRequest authRequest;

    @BeforeEach
    void setUp() {
        username = "username";
        password = "password";
        authRequest = new AuthRequest(username, password);

        User user = new User(username, password);
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test
    public void throwsWhenRegisterWithAnAlreadyRegisteredEmail() throws Exception {
        // Arrange
        when(authService.register(any())).thenThrow(new UsernameAlreadyExistsException("Username is already taken."));

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest))
        ).andReturn().getResponse();

        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
        assertEquals("Username is already taken.", apiException.getMessage());
    }

    @Test
    public void throwsWhenRegisterIsMissingUsernameOrPassword() throws Exception {
        // Arrange
        when(authService.register(any())).thenThrow(
                new IllegalArgumentException("Both a username and a password are needed."))
        ;

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/v1/auth/register")
                        .with(csrf())
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
        AuthResponse fakeAuthResponse = new AuthResponse("fakeToken");
        when(authService.register(any())).thenReturn(fakeAuthResponse);

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest))
        ).andReturn().getResponse();

        AuthResponse authResponse = objectMapper.readValue(response.getContentAsString(), AuthResponse.class);


        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("fakeToken", authResponse.token());
        assertTrue(authResponse.token() instanceof String);
    }

    @Test
    public void throwsWhenLoggingIsMissingUsernameOrPassword() throws Exception {
        // Arrange
        when(authService.login(any())).thenThrow(
                new IllegalArgumentException("Both a username and a password are needed.")
        );

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest))
        ).andReturn().getResponse();

        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Both a username and a password are needed.", apiException.getMessage());
    }

    @Test
    public void throwsWhenLoggingWithIncorrectPassword() throws Exception {
        // Arrange
        when(authService.login(any())).thenThrow(new InvalidPasswordException("Password entered is incorrect."));

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest))
        ).andReturn().getResponse();

        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertEquals("Password entered is incorrect.", apiException.getMessage());
    }

    @Test
    public void throwsWhenLoggingWithUsernameThatDoesNotExist() throws Exception {
        // Arrange
        // TODO Make a file where we can read all the exception messages from.
        when(authService.login(any())).thenThrow(
                new UserNotFoundException("The user was not found using the email you've provided.")
        );

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest))
        ).andReturn().getResponse();

        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals("The user was not found using the email you've provided.", apiException.getMessage());
    }

    @Test
    public void canLogin() throws Exception {
        // Arrange
        AuthResponse fakeAuthResponse = new AuthResponse("fakeToken");
        when(authService.login(any())).thenReturn(fakeAuthResponse);

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest))
        ).andReturn().getResponse();
        AuthResponse authResponse = objectMapper.readValue(response.getContentAsString(), AuthResponse.class);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("fakeToken", authResponse.token());
        assertTrue(authResponse.token() instanceof String);
    }
}