package com.league_buddies.backend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.league_buddies.backend.exception.*;
import com.league_buddies.backend.exception.IllegalArgumentException;
import com.league_buddies.backend.security.jwt.JwtService;
import com.league_buddies.backend.user.User;
import com.league_buddies.backend.util.MessageResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.support.ResourceBundleMessageSource;
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

    private final String username = "username";

    private final String password = "password";

    private final AuthRequest authRequest = new AuthRequest(username, password);

    private MessageResolver messageResolver;

    private String exceptionMessage;

    private final String controllerEndpoint = "/api/v1/auth/";

    @BeforeEach
    void setUp() {
        User user = new User(username, password);
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");

        messageResolver = new MessageResolver(messageSource);
    }

    @Test
    public void throwsWhenRegisterWithAnAlreadyRegisteredEmail() throws Exception {
        // Arrange
        exceptionMessage = messageResolver.getMessage("usernameAlreadyExists", new Object[] {username});
        when(authService.register(any())).thenThrow(new UsernameAlreadyExistsException(exceptionMessage));

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                post(controllerEndpoint + "/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest))
        ).andReturn().getResponse();

        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
        assertEquals(exceptionMessage, apiException.getMessage());
    }

    @Test
    public void throwsWhenRegisterIsMissingUsernameOrPassword() throws Exception {
        // Arrange
        exceptionMessage = messageResolver.getMessage("illegalArgument");
        when(authService.register(any())).thenThrow(new IllegalArgumentException(exceptionMessage));

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                post(controllerEndpoint + "/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest))
        ).andReturn().getResponse();

        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(exceptionMessage, apiException.getMessage());
    }

    @Test
    public void canRegister() throws Exception {
        // Arrange
        AuthResponse fakeAuthResponse = new AuthResponse("fakeToken");
        when(authService.register(any())).thenReturn(fakeAuthResponse);

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                post(controllerEndpoint + "/register")
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
        exceptionMessage = messageResolver.getMessage("illegalArgument");
        when(authService.login(any())).thenThrow(new IllegalArgumentException(exceptionMessage));

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                post(controllerEndpoint +"/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest))
        ).andReturn().getResponse();

        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(exceptionMessage, apiException.getMessage());
    }

    @Test
    public void throwsWhenLoggingWithIncorrectPassword() throws Exception {
        // Arrange
        exceptionMessage = messageResolver.getMessage("invalidPassword");
        when(authService.login(any())).thenThrow(new InvalidPasswordException(exceptionMessage));

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                post(controllerEndpoint + "/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest))
        ).andReturn().getResponse();

        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
        assertEquals(exceptionMessage, apiException.getMessage());
    }

    @Test
    public void throwsWhenLoggingWithUsernameThatDoesNotExist() throws Exception {
        // Arrange
        exceptionMessage = messageResolver.getMessage("userNotFound");
        when(authService.login(any())).thenThrow(new UserNotFoundException(exceptionMessage));

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                post(controllerEndpoint + "/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest))
        ).andReturn().getResponse();

        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals(exceptionMessage, apiException.getMessage());
    }

    @Test
    public void canLogin() throws Exception {
        // Arrange
        AuthResponse fakeAuthResponse = new AuthResponse("fakeToken");
        when(authService.login(any())).thenReturn(fakeAuthResponse);

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                post(controllerEndpoint + "/login")
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