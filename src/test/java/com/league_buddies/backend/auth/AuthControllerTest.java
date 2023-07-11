package com.league_buddies.backend.auth;

import com.league_buddies.backend.exception.CustomExceptionHandler;
import com.league_buddies.backend.security.jwt.JwtService;
import com.league_buddies.backend.user.User;
import com.league_buddies.backend.user.UserRepository;
import com.league_buddies.backend.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class AuthControllerTest {
    private AuthController authController;

    private AuthService authService;

    private JwtService jwtService;

    private UserService userService;

    @Mock
    private UserRepository repository;

    private MockMvc mockMvc;

//    @BeforeEach
//    void setUp() {
//        jwtService = new JwtService();
//        userService = new UserService(repository);
//        authService = new AuthService(userService, jwtService);
//        authController = new AuthController(authService);
//
//
//        mockMvc = MockMvcBuilders.standaloneSetup(authController)
//                .setControllerAdvice(CustomExceptionHandler.class)
//                .build();
//    }

//    @Test
//    void login() {
//        // Arrange
//        User user = new User("", "");
//        when(repository.findByEmailAddress(anyString())).thenReturn(Optional.of(user));
//        // Controller -> AuthService -> UserService -> Repository
//
//        // Act
//        MockHttpServletResponse response = mockMvc.perform()
//
//        // Assert
//    }

//    @Test
//    public void throwsWhenUserDoesNotExistInDatabaseWithGivenId() throws Exception {
//        // Arrange
//        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        // Act
//        MockHttpServletResponse response = mockMvc.perform(
//                        get(String.format("/api/v1/user/%d", id)).accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        ApiException apiException = objectMapper.readValue(response.getContentAsString(), ApiException.class);
//
//        // Assert
//        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
//        assertEquals(String.format("User with id: %d was not found.", id), apiException.getMessage());
//    }
}