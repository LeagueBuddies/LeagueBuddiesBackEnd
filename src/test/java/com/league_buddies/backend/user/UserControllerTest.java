package com.league_buddies.backend.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.league_buddies.backend.exception.ApiException;
import com.league_buddies.backend.exception.CustomExceptionHandler;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserControllerTest.class)
class UserControllerTest {
    // TODO Make a constant for the API url to avoid writing it over and over.
    @Mock
    private UserRepository userRepository;

    private UserService userService;

    private UserController userController;

    @Autowired
    CustomExceptionHandler customExceptionHandler;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private long id;

    private Optional<User> optionalUser;

    private String username;

    private String email;

    private String password;

    @BeforeEach
    void setUp() {
        // Stub user service with mock user repository
        userService = new UserService(userRepository);

        // Start controller with user service that has the mock user repository
        userController = new UserController(userService);

        // Builds a MockMvcBuilder and passes it the CustomExceptionHandler
        // so that it can use it, otherwise it will use the default one Spring Boot
        // comes with.
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(customExceptionHandler)
                .build();

        id = 1L;
        username = "Isolated";
        email = "email@gmail.com";
        password = "pw12345";
        optionalUser = Optional.of(new User());
        optionalUser.get().setId(id);
        optionalUser.get().setDisplayName(username);
        optionalUser.get().setEmailAddress(email);
        optionalUser.get().setPassword(password);
    }

    @Test
    public void throwsWhenUserDoesNotExistInDatabaseWithGivenId() throws Exception {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                get(String.format("/api/v1/user/%d", id)).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals(String.format("User with id: %d was not found.", id), apiException.getMessage());
    }

    @Test
    public void throwsWhenGivenNegativeValueToGetById() throws Exception {
        // Act
        id = -1L;
        MockHttpServletResponse response = mockMvc.perform(
                get(String.format("/api/v1/user/%s", id)).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Id cannot be negative.", apiException.getMessage());
    }

    @Test
    public void canGetUserById() throws Exception {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(optionalUser);

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                get(String.format("/api/v1/user/%d", id)).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        User user = objectMapper.readValue(response.getContentAsString(), User.class);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(id, user.getId());
    }

    @Test
    public void throwsWhenUserDoesNotExistInDatabaseWithGivenUsername() throws Exception {
        // Arrange
        when(userRepository.findByEmailAddress(anyString())).thenReturn(Optional.empty());

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                get(String.format("/api/v1/user/username/%s", username)).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);


        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals(String.format("User with username: %s was not found.", username), apiException.getMessage());
    }

    @Test
    public void throwsWhenGivenEmptyStringToGetByUsername() throws Exception {
        // Arrange
        username = " ";

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                get(String.format("/api/v1/user/username/%s", username)).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Username must not be empty.", apiException.getMessage());
    }

    @Test
    public void canGetUserByUsername() throws Exception {
        // Arrange
        when(userRepository.findByEmailAddress(anyString())).thenReturn(optionalUser);

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                        get(String.format("/api/v1/user/username/%s", username)).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        User user = objectMapper.readValue(response.getContentAsString(), User.class);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(username, user.getDisplayName());
    }

    @Test
    public void canCreateUser() throws Exception {
        // Arrange
        User user = new User(email, password);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/v1/user/").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))).andReturn().getResponse();
        User userPosted = objectMapper.readValue(response.getContentAsString(), User.class);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(email, userPosted.getEmailAddress());
        assertEquals(password, userPosted.getPassword());
    }

    @Test
    public void throwsWhenCreateUserGetsNullValue() throws Exception {
        // TODO look up if there is a way to allow only data JPA to create an empty entity. To make it easier to test.
        // Act
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/v1/user/").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new User()))).andReturn().getResponse();
        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("User must not be null.", apiException.getMessage());
    }

    @Test
    public void throwsWhenCreateUserGetsAlreadyExistingUsername() throws Exception {
        // Arrange
        when(userRepository.findByEmailAddress(anyString())).thenReturn(optionalUser);

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/v1/user").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(optionalUser.get()))
        ).andReturn().getResponse();
        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
        assertEquals("Username is already taken.", apiException.getMessage());
    }

    @Test
    public void canUpdateUser() throws Exception {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(optionalUser);

        // Act
        String newEmail = "newEmail@gmail.com";
        String newPassword = "newPassword123";
        MockHttpServletResponse response = mockMvc.perform(
                put(String.format("/api/v1/user/%d", id)).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new User(newEmail, newPassword)
                        ))).andReturn().getResponse();
        User updatedUser = objectMapper.readValue(response.getContentAsString(), User.class);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(newEmail, updatedUser.getEmailAddress());
        assertEquals(newPassword, updatedUser.getPassword());
    }

    @Test
    public void throwsWhenUserToDeleteDoesNotExistInDatabase() throws Exception {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                delete(String.format("/api/v1/user/%d", id)).contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();
        ApiException exception = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals(String.format("User with id: %d was not found.", id), exception.getMessage());
    }

    @Test
    public void throwsWhenGivenNegativeIdToDeleteUser() throws Exception {
        // Arrange
        id = -1L;

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                delete(String.format("/api/v1/user/%d", id)).contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();
        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Id cannot be negative.", apiException.getMessage());
    }

    @Test
    public void canDeleteUser() throws Exception {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(optionalUser);

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                delete(String.format("/api/v1/user/%d", id)).contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(String.format("User with id: %d was deleted.", id), response.getContentAsString());
    }
}