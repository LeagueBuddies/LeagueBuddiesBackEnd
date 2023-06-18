package com.league_buddies.backend.player;

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
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(PlayerControllerTest.class)
class PlayerControllerTest {
    // TODO Make a constant for the API url to avoid writing it over and over.
    @Mock
    private PlayerRepository playerRepository;

    private PlayerService playerService;

    private PlayerController playerController;

    @Autowired
    private Jackson2ObjectMapperBuilder mapperBuilder;

    @Autowired
    CustomExceptionHandler customExceptionHandler;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private long id;

    private Optional<Player> optionalPlayer;

    private String username;

    private String email;

    private String password;

    @BeforeEach
    void setUp() {
        // Stub player service with mock player repository
        playerService = new PlayerService(playerRepository);

        // Start controller with player service that has the mock player repository
        playerController = new PlayerController(playerService);

        // Builds a MockMvcBuilder and passes it the CustomExceptionHandler
        // so that it can use it, otherwise it will use the default one Spring Boot
        // comes with.
        mockMvc = MockMvcBuilders.standaloneSetup(playerController)
                .setControllerAdvice(customExceptionHandler)
                .build();

        // Before I was creating a new ObjectMapper() but it comes with no configurations
        // The correct way of doing it is using JacksonObjectMapperBuilder which comes
        // preconfigured
        objectMapper = mapperBuilder.build();

        id = 1L;
        username = "Isolated";
        email = "email@gmail.com";
        password = "pw12345";
        optionalPlayer = Optional.of(new Player());
        optionalPlayer.get().setId(id);
        optionalPlayer.get().setUsername(username);
        optionalPlayer.get().setEmailAddress(email);
        optionalPlayer.get().setPassword(password);
    }

    @Test
    public void throwsWhenPlayerDoesNotExistInDatabaseWithGivenId() throws Exception {
        // Arrange
        when(playerRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                get(String.format("/api/v1/player/%d", id)).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals(String.format("Player with id: %d was not found.", id), apiException.getMessage());
    }

    @Test
    public void throwsWhenGivenNegativeValueToGetById() throws Exception {
        // Act
        id = -1L;
        MockHttpServletResponse response = mockMvc.perform(
                get(String.format("/api/v1/player/%s", id)).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Id cannot be negative.", apiException.getMessage());
    }

    @Test
    public void canGetPlayerById() throws Exception {
        // Arrange
        when(playerRepository.findById(anyLong())).thenReturn(optionalPlayer);

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                get(String.format("/api/v1/player/%d", id)).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        Player player = objectMapper.readValue(response.getContentAsString(), Player.class);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(id, player.getId());
    }

    @Test
    public void throwsWhenPlayerDoesNotExistInDatabaseWithGivenUsername() throws Exception {
        // Arrange
        when(playerRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                get(String.format("/api/v1/player/username/%s", username)).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);


        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals(String.format("Player with username: %s was not found.", username), apiException.getMessage());
    }

    @Test
    public void throwsWhenGivenEmptyStringToGetByUsername() throws Exception {
        // Arrange
        username = " ";

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                get(String.format("/api/v1/player/username/%s", username)).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Username must not be empty.", apiException.getMessage());
    }

    @Test
    public void canGetPlayerByUsername() throws Exception {
        // Arrange
        when(playerRepository.findByUsername(anyString())).thenReturn(optionalPlayer);

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                        get(String.format("/api/v1/player/username/%s", username)).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        Player player = objectMapper.readValue(response.getContentAsString(), Player.class);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(username, player.getUsername());
    }

    @Test
    public void canCreatePlayer() throws Exception {
        // Arrange
        Player player = new Player(email, password);
        when(playerRepository.save(any(Player.class))).thenReturn(player);

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/v1/player/").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(player))).andReturn().getResponse();
                Player playerPosted = objectMapper.readValue(response.getContentAsString(), Player.class);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(email, playerPosted.getEmailAddress());
        assertEquals(password, playerPosted.getPassword());
    }

    @Test
    public void throwsWhenCreatePlayerGetsNullValue() throws Exception {
        // TODO look up if there is a way to allow only data JPA to create an empty entity. To make it easier to test.
        // Act
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/v1/player/").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Player()))).andReturn().getResponse();
        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Player must not be null.", apiException.getMessage());
    }

    @Test
    public void throwsWhenCreatePlayerGetsAlreadyExistingUsername() throws Exception {
        // Arrange
        when(playerRepository.findByUsername(anyString())).thenReturn(optionalPlayer);

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                post("/api/v1/player").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(optionalPlayer.get()))
        ).andReturn().getResponse();
        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
        assertEquals("Username is already taken.", apiException.getMessage());
    }

    @Test
    public void canUpdatePlayer() throws Exception {
        // Arrange
        when(playerRepository.findById(anyLong())).thenReturn(optionalPlayer);

        // Act
        String newEmail = "newEmail@gmail.com";
        String newPassword = "newPassword123";
        MockHttpServletResponse response = mockMvc.perform(
                put(String.format("/api/v1/player/%d", id)).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new Player(newEmail, newPassword)
                        ))).andReturn().getResponse();
        Player updatedPlayer = objectMapper.readValue(response.getContentAsString(), Player.class);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(newEmail, updatedPlayer.getEmailAddress());
        assertEquals(newPassword, updatedPlayer.getPassword());
    }

    @Test
    public void throwsWhenPlayerToDeleteDoesNotExistInDatabase() throws Exception {
        // Arrange
        when(playerRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                delete(String.format("/api/v1/player/%d", id)).contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();
        ApiException exception = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals(String.format("Player with id: %d was not found.", id), exception.getMessage());
    }

    @Test
    public void throwsWhenGivenNegativeIdToDeletePlayer() throws Exception {
        // Arrange
        id = -1L;

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                delete(String.format("/api/v1/player/%d", id)).contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();
        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertEquals("Id cannot be negative.", apiException.getMessage());
    }

    @Test
    public void canDeletePlayer() throws Exception {
        // Arrange
        when(playerRepository.findById(anyLong())).thenReturn(optionalPlayer);

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                delete(String.format("/api/v1/player/%d", id)).contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(String.format("Player with id: %d was deleted.", id), response.getContentAsString());
    }
}