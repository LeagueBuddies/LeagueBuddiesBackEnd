package com.league_buddies.backend.user;

import com.league_buddies.backend.exception.IllegalArgumentException;
import com.league_buddies.backend.exception.UserNotFoundException;
import com.league_buddies.backend.util.MessageResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.ResourceBundleMessageSource;

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

    private final Long id = 1L;

    private final String username = "Isolated";

    private final String email = "email@gmail.com";

    private final String password = "password1234";

    private MessageResolver messageResolver;

    @BeforeEach
    void setUp() {
        // TODO Look more into this.
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        // TODO Add the basename and default encoding as properties in application so that the app config and this config have the exact same one.
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");

        messageResolver = new MessageResolver(messageSource);
        userService = new UserService(userRepository, messageResolver);

        user = new User(email, password);
        user.setEmailAddress(email);
        user.setDisplayName(username);
        user.setId(id);

        optionalUser = Optional.of(user);
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
        // TODO Before finalizing this PR, check how to change the language for error messages.
        assertEquals(messageResolver.getMessage("userNotFound"), exception.getMessage());
    }

    @Test
    void throwsWhenFindByIdIsGivenANegativeValue() {
        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> userService.findById(-1L)
        );

        // Assert
        assertEquals(messageResolver.getMessage("illegalArgument"),
                exception.getMessage()
        );
    }

    @Test
    void findsByUsername() {
        // Arrange
        when(userRepository.findByEmailAddress(anyString())).thenReturn(optionalUser);

        // Act
        User user = userService.findByEmailAddress(email);

        // Assert
        verify(userRepository).findByEmailAddress(anyString());
        assertEquals(username, user.getDisplayName());
    }

    @Test
    void throwsWhenUserDoesNotExistWithGivenUsername() {
        // Arrange
        when(userRepository.findByEmailAddress(anyString())).thenReturn(Optional.empty());

        // Act
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class, () -> userService.findByEmailAddress(email)
        );

        // Assert
        assertEquals(messageResolver.getMessage("userNotFound"), exception.getMessage());
    }

    @Test
    void throwsWhenFindByUsernameGetsEmptyString() {
        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> userService.findByEmailAddress("")
        );

        assertEquals(messageResolver.getMessage("illegalArgument"), exception.getMessage());
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
        assertEquals(messageResolver.getMessage("userNotFound"), exception.getMessage());
    }

    @Test
    void throwsWhenGivenUserToUpdateIsNull() {
        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> userService.updateUser(1L, null)
        );

        // Assert
        assertEquals(messageResolver.getMessage("illegalArgument"), exception.getMessage());
    }

    @Test
    void throwsWhenGivenNegativeIdToUpdateUser() {
        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> userService.updateUser(-1L, user)
        );

        // Assert
        assertEquals(messageResolver.getMessage("illegalArgument"), exception.getMessage());    }

    @Test
    void updatesUser() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(optionalUser);
        User newUserData = new User();
        newUserData.setDisplayName("Noel");
        newUserData.setEmailAddress("newEmail@gmail.com");

        // Act
        User updatedUser = userService.updateUser(id, newUserData);

        // Assert
        assertEquals(newUserData.getEmailAddress(), updatedUser.getEmailAddress());
        assertEquals(newUserData.getDisplayName(), updatedUser.getDisplayName());
    }

    @Test
    void throwsWhenDeleteUserGetsANegativeId() {
        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> userService.deleteUser(-2L)
        );

        // Assert
        assertEquals(messageResolver.getMessage("illegalArgument"), exception.getMessage());    }

    @Test
    void throwsWhenUserToDeleteDoesNotExist() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class, () -> userService.deleteUser(id)
        );

        // Assert
        assertEquals(messageResolver.getMessage("userNotFound"), exception.getMessage());
    }

    @Test
    void deletesUser() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(optionalUser);

        // Act
        String response = userService.deleteUser(id);

        // Assert
        assertEquals(messageResolver.getMessage(
                "userDeleted",
                new Object[] {id}
                ), response);
    }
}