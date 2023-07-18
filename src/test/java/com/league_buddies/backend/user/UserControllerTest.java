package com.league_buddies.backend.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.league_buddies.backend.exception.ApiException;
import com.league_buddies.backend.exception.IllegalArgumentException;
import com.league_buddies.backend.exception.UserNotFoundException;
import com.league_buddies.backend.security.jwt.JwtService;
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

import java.util.Optional;

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

    private long id;

    private Optional<User> optionalUser;

    private String displayName;

    private String email;

    private String password;

    private User user;

    @BeforeEach
    void setUp() {
        id = 1L;
        displayName = "Isolated";
        email = "email@gmail.com";
        password = "pw12345";
        user = new User();
        user.setId(id);
        user.setDisplayName(displayName);
        user.setEmailAddress(email);
        user.setPassword(password);
        optionalUser = Optional.of(user);

        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }
    @Test
    public void throwsWhenUserDoesNotExistInDatabaseWithGivenId() throws Exception {
        // Arrange
        when(userService.findById(anyLong())).thenThrow(
                new UserNotFoundException(String.format("User with id: %d was not found.", id))
        );

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                get(String.format("/api/v1/user/%d", id)))
                .andReturn().getResponse();

        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals(String.format("User with id: %d was not found.", id), apiException.getMessage());
    }

    @Test
    public void throwsWhenGivenNegativeValueToGetById() throws Exception {
        // Arrange
        when(userService.findById(anyLong())).thenThrow(
                new IllegalArgumentException("Id cannot be negative.")
        );

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                get(String.format("/api/v1/user/%s", id)))
                .andReturn().getResponse();
        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Id cannot be negative.", apiException.getMessage());
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
        when(userService.findByEmailAddress(anyString())).thenThrow(
                new UserNotFoundException(String.format("User with username: %s was not found.", email))
        );

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                get(String.format("/api/v1/user/username/%s", displayName)))
                .andReturn().getResponse();
        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);


        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals(String.format("User with username: %s was not found.", email), apiException.getMessage());
    }

    @Test
    public void throwsWhenGivenEmptyStringToGetByUsername() throws Exception {
        // Arrange
        when(userService.findByEmailAddress(anyString())).thenThrow(
                new IllegalArgumentException("Username must not be empty.")
        );

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                get(String.format("/api/v1/user/username/%s", email)))
                .andReturn().getResponse();
        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Username must not be empty.", apiException.getMessage());
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
        when(userService.updateUser(anyLong(), any())).thenThrow(new IllegalArgumentException("User cannot be null."));

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
        assertEquals("User cannot be null.", exception.getMessage());
    }

    @Test
    public void throwsWhenUpdateUserGetsNegativeId() throws Exception {
        // Arrange
        when(userService.updateUser(anyLong(), any())).thenThrow(new IllegalArgumentException("Id cannot be negative."));

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
        assertEquals("Id cannot be negative.", exception.getMessage());
    }

    @Test
    public void throwsWhenUpdateUserIsCalledWithIdOfNonExistentUser() throws Exception {
        // Arrange
        when(userService.updateUser(anyLong(), any())).thenThrow(
                new UserNotFoundException(String.format("User with Id: %d was not found.", id))
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
        assertEquals(String.format("User with Id: %d was not found.", id), exception.getMessage());
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
        when(userService.deleteUser(anyLong())).thenThrow(new UserNotFoundException("User with id: 1 was not found."));

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
        assertEquals(String.format("User with id: %d was not found.", id), exception.getMessage());
    }

    @Test
    public void throwsWhenGivenNegativeIdToDeleteUser() throws Exception {
        // Arrange
        when(userService.deleteUser(anyLong())).thenThrow(new IllegalArgumentException("Id cannot be negative."));

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                delete(String.format("/api/v1/user/%d", id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        ).andReturn().getResponse();
        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Id cannot be negative.", apiException.getMessage());
    }

    @Test
    public void canDeleteUser() throws Exception {
        // Arrange
        when(userService.deleteUser(anyLong())).thenReturn(String.format("User with id: %d was deleted.", id));

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                delete(String.format("/api/v1/user/%d", id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
        ).andReturn().getResponse();

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(String.format("User with id: %d was deleted.", id), response.getContentAsString());
    }
}