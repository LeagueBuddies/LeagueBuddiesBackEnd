package com.league_buddies.backend.player;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.ErrorResponse;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(PlayerControllerTest.class)
class PlayerControllerTest {
    @Mock
    private PlayerRepository playerRepository;
    private PlayerService playerService;
    private PlayerController playerController;
    @Autowired
    Jackson2ObjectMapperBuilder mapperBuilder;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Stub player service with mock player repository
        playerService = new PlayerService(playerRepository);

        // Start controller with player service that has the mock player repository
        playerController = new PlayerController(playerService);

        // Builds a MockMvcBuilder and pass it the CustomExceptionHandler
        // so that it can use it, otherwise it will use the default one Spring Boot
        // comes with.
        mockMvc = MockMvcBuilders.standaloneSetup(playerController)
                .setControllerAdvice(new CustomExceptionHandler())
                .build();
    }

    @Test
    public void throwsWhenPlayerDoesNotExistInDatabase() throws Exception {
        // Arrange
        when(playerRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        MockHttpServletResponse response = mockMvc.perform(
                get("/api/v1/player/1").accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Before I was creating a new ObjectMapper() but it comes with no configurations
        // The correct way of doing it is using JacksonObjectMapperBuilder which comes
        // preconfigured
        ObjectMapper objectMapper = mapperBuilder.build();
        ApiException errorResponse = objectMapper.readValue(response.getContentAsString(), ApiException.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals("Player with id: 1 was not found.", errorResponse.getMessage());
    }
}