package com.league_buddies.backend.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.league_buddies.backend.exception.ApiException;
import com.league_buddies.backend.exception.IllegalArgumentException;
import com.league_buddies.backend.exception.UserNotFoundException;
import com.league_buddies.backend.security.jwt.JwtService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {
    // TODO Make a constant for the API url to avoid writing it over and over.
    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final long id = 1L;

    private final String displayName = "Isolated";

    private final String email = "email@gmail.com";

    private final String password = "pw12345";

    private final User user = new User(email, password);

    private String exceptionMessage;

    private MessageResolver messageResolver;

    @BeforeEach
    void setUp() {
        user.setId(id);
        user.setDisplayName(displayName);

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
    public void throwsWhenUserDoesNotExistInDatabaseWithGivenId() throws Exception {
        // Arrange
        exceptionMessage = messageResolver.getMessage("userNotFound");
        when(userService.findById(anyLong())).thenThrow(
                new UserNotFoundException(exceptionMessage)
        );

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                get(String.format("/api/v1/user/%d", id)))
                .andReturn().getResponse();
        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals(exceptionMessage, apiException.getMessage());
    }

    @Test
    public void throwsWhenGivenNegativeValueToGetById() throws Exception {
        // Arrange
        exceptionMessage = messageResolver.getMessage("illegalArgument");
        when(userService.findById(anyLong())).thenThrow(new IllegalArgumentException(exceptionMessage));

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                get(String.format("/api/v1/user/%s", id)))
                .andReturn().getResponse();
        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(exceptionMessage, apiException.getMessage());
    }

    @Test
    public void canGetUserById() throws Exception {
        // Arrange
        when(userService.findById(anyLong())).thenReturn(user);

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                get(String.format("/api/v1/user/%d", id)))
                .andReturn().getResponse();
        User user = objectMapper.readValue(response.getContentAsString(), User.class);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(displayName, user.getDisplayName());
    }

    @Test
    public void throwsWhenUserDoesNotExistInDatabaseWithGivenUsername() throws Exception {
        // Arrange
        exceptionMessage = messageResolver.getMessage("userNotFound");
        when(userService.findByEmailAddress(anyString())).thenThrow(new UserNotFoundException(exceptionMessage));

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                get(String.format("/api/v1/user/username/%s", displayName)))
                .andReturn().getResponse();
        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);


        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals(exceptionMessage, apiException.getMessage());
    }

    @Test
    public void throwsWhenGivenEmptyStringToGetByUsername() throws Exception {
        // Arrange
        exceptionMessage = messageResolver.getMessage("illegalArgument");
        when(userService.findByEmailAddress(anyString())).thenThrow(new IllegalArgumentException(exceptionMessage));

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                get(String.format("/api/v1/user/username/%s", email)))
                .andReturn().getResponse();
        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(exceptionMessage, apiException.getMessage());
    }

    @Test
    public void canGetUserByUsername() throws Exception {
        // Arrange
        when(userService.findByEmailAddress(anyString())).thenReturn(user);

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                        get(String.format("/api/v1/user/username/%s", email)))
                .andReturn().getResponse();
        User user = objectMapper.readValue(response.getContentAsString(), User.class);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(displayName, user.getDisplayName());
    }

    @Test
    public void throwsWhenUpdateUserGetsNullUser() throws Exception {
        // Arrange
        exceptionMessage = messageResolver.getMessage("illegalArgument");
        when(userService.updateUser(anyLong(), any())).thenThrow(new IllegalArgumentException(exceptionMessage));

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                put(String.format("/api/v1/user/%d", id))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
        ).andReturn().getResponse();

        ApiException exception = objectMapper.readValue(
                response.getContentAsString(), ApiException.class
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    public void throwsWhenUpdateUserGetsNegativeId() throws Exception {
        // Arrange
        exceptionMessage = messageResolver.getMessage("illegalArgument");
        when(userService.updateUser(anyLong(), any())).thenThrow(new IllegalArgumentException(exceptionMessage));

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                put(String.format("/api/v1/user/%d", id))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
        ).andReturn().getResponse();

        ApiException exception = objectMapper.readValue(
                response.getContentAsString(), ApiException.class
        );

        // Arrange
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    public void throwsWhenUpdateUserIsCalledWithIdOfNonExistentUser() throws Exception {
        // Arrange
        exceptionMessage = messageResolver.getMessage("userNotFound");
        when(userService.updateUser(anyLong(), any())).thenThrow(
                new UserNotFoundException(exceptionMessage)
        );

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                put(String.format("/api/v1/user/%d", id))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
        ).andReturn().getResponse();
        ApiException exception = objectMapper.readValue(
                response.getContentAsString(), ApiException.class
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    public void canUpdateUser() throws Exception {
        // Arrange
        String newDisplayName = "Ghost";
        User newUser = new User();
        newUser.setDisplayName(newDisplayName);
        when(userService.updateUser(anyLong(), any(User.class))).thenReturn(newUser);

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                put(String.format("/api/v1/user/%d", id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(newUser))
        ).andReturn().getResponse();
        User updatedUser = objectMapper.readValue(response.getContentAsString(), User.class);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(newDisplayName, updatedUser.getDisplayName());
    }


    @Test
    public void throwsWhenUserToDeleteDoesNotExistInDatabase() throws Exception {
        // Arrange
        exceptionMessage = messageResolver.getMessage("userNotFound");
        when(userService.deleteUser(anyLong())).thenThrow(new UserNotFoundException(exceptionMessage));

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                delete(String.format("/api/v1/user/%d", id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(user))
        ).andReturn().getResponse();
        ApiException exception = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    public void throwsWhenGivenNegativeIdToDeleteUser() throws Exception {
        // Arrange
        exceptionMessage = messageResolver.getMessage("illegalArgument");
        when(userService.deleteUser(anyLong())).thenThrow(new IllegalArgumentException(exceptionMessage));

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                delete(String.format("/api/v1/user/%d", id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        ).andReturn().getResponse();
        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals(exceptionMessage, apiException.getMessage());
    }

    @Test
    public void canDeleteUser() throws Exception {
        // Arrange
        String message = messageResolver.getMessage("userDeleted", new Object[] {id});
        when(userService.deleteUser(anyLong())).thenReturn(message);

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                delete(String.format("/api/v1/user/%d", id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        ).andReturn().getResponse();

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(message, response.getContentAsString());
    }
}