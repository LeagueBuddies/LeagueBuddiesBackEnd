package com.league_buddies.backend.user;

import com.league_buddies.backend.exception.IllegalArgumentException;
import com.league_buddies.backend.exception.UserNotFoundException;
import com.league_buddies.backend.exception.UsernameAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    private UserService userService;

    private Optional<User> optionalUser;

    private User user;

    private Long id;

    private String username;

    @BeforeEach
    void setUp() {
        // Stub the userRepo into our service before each test.
        userService = new UserService(userRepository);

        optionalUser = Optional.of(new User("email@gmail.com", "password1234"));

        id = 1L;
        username = "Isolated";

        optionalUser.get().setId(id);
        optionalUser.get().setUsername(username);

        user = optionalUser.get();
    }

    @Test
    void findsById() {
        // Arrange
        optionalUser.get().setId(id);
        when(userRepository.findById(anyLong())).thenReturn(optionalUser);

        // Act
        User user = userService.findById(id);

        // Assert
        verify(userRepository).findById(anyLong());
        assertEquals(id, user.getId());
    }

    @Test()
    void throwsWhenUserDoesNotExistWithGivenId() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class, () -> userService.findById(1L)
        );

        // Assert
        assertEquals("User with id: 1 was not found.", exception.getMessage());
    }

    @Test
    void throwsWhenFindByIdIsGivenANegativeValue() {
        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> userService.findById(-1L)
        );

        // Assert
        assertEquals("Id cannot be negative.", exception.getMessage());
    }

    @Test
    void findsByUsername() {
        // Arrange
        optionalUser.get().setUsername("Isolated");
        when(userRepository.findByUsername(anyString())).thenReturn(optionalUser);

        // Act
        User user = userService.findByUsername("Isolated");

        // Assert
        verify(userRepository).findByUsername("Isolated");
        assertEquals("Isolated", user.getUsername());
    }

    @Test
    void throwsWhenUserDoesNotExistWithGivenUsername() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // Act
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class, () -> userService.findByUsername("Isolated")
        );

        // Assert
        assertEquals("User with username: Isolated was not found.", exception.getMessage());
    }

    @Test
    void throwsWhenFindByUsernameGetsEmptyString() {
        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> userService.findByUsername("")
        );

        assertEquals("Username must not be empty.", exception.getMessage());
    }

    @Test
    void throwsWhenCreateUserIsGivenNullValue() {
        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> userService.createUser(null)
        );

        // Assert
        assertEquals("User must not be null.", exception.getMessage());
    }

    @Test
    void createsUser() {
        // Arrange
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User createdUser = userService.createUser(user);

        // Assert
        assertNotNull(createdUser);
        assertEquals(user.getId(), createdUser.getId());
        assertEquals(user.getUsername(), createdUser.getUsername());
    }

    @Test
    void throwsWhenCreatesUserIsGivenAlreadyExistingUsername() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(optionalUser);

        // Act
        UsernameAlreadyExistsException exception = assertThrows(
                UsernameAlreadyExistsException.class, () -> userService.createUser(user)
        );

        // Assert
        assertEquals("Username is already taken.", exception.getMessage());
    }

    @Test
    void throwsWhenUserToUpdateDoesNotExist() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class, () -> userService.updateUser(anyLong(), user)
        );

        // Assert
        assertEquals("User with Id: 0 was not found.", exception.getMessage());
    }

    @Test
    void throwsWhenGivenUserToUpdateIsNull() {
        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> userService.updateUser(1L, null)
        );

        // Assert
        assertEquals("User cannot be null.", exception.getMessage());
    }

    @Test
    void throwsWhenGivenNegativeIdToUpdateUser() {
        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> userService.updateUser(-1L, user)
        );

        // Assert
        assertEquals("Id cannot be negative.", exception.getMessage());
    }

    @Test
    void updatesUser() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(optionalUser);
        User newUserData = new User();
        newUserData.setUsername("Noel");
        newUserData.setEmailAddress("newEmail@gmail.com");

        // Act
        User updatedUser = userService.updateUser(id, newUserData);

        // Assert
        assertEquals(newUserData.getEmailAddress(), updatedUser.getEmailAddress());
        assertEquals(newUserData.getUsername(), updatedUser.getUsername());
    }

    @Test
    void throwsWhenDeleteUserGetsANegativeId() {
        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> userService.deleteUser(-2L)
        );

        // Assert
        assertEquals("Id cannot be negative.", exception.getMessage());
    }

    @Test
    void throwsWhenUserToDeleteDoesNotExist() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class, () -> userService.deleteUser(id)
        );

        // Assert
        assertEquals(String.format("User with id: %d was not found.", id), exception.getMessage());
    }

    @Test
    void deletesUser() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(optionalUser);

        // Act
        String response = userService.deleteUser(id);

        // Assert
        assertEquals(String.format("User with id: 1 was deleted.", id), response);
    }
}