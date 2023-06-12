package com.league_buddies.backend.player;

import com.league_buddies.backend.exception.PlayerNotFoundException;
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
class PlayerServiceTest {
    @Mock
    private PlayerRepository playerRepository;

    private PlayerService playerService;

    private Optional<Player> optionalPlayer;

    private Player player;

    private Long id;

    @BeforeEach
    void setUp() {
        // Stub the playerRepo into our service before each test.
        playerService = new PlayerService(playerRepository);

        optionalPlayer = Optional.of(new Player());

        player = optionalPlayer.get();
        player.setId(1L);
        player.setUsername("Isolated");

        // DRY
        id = 1L;
    }

    @Test
    void findsById() {
        // Arrange
        optionalPlayer.get().setId(id);
        when(playerRepository.findById(anyLong())).thenReturn(optionalPlayer);

        // Act
        Player player = playerService.findById(id);

        // Assert
        verify(playerRepository).findById(anyLong());
        assertEquals(id, player.getId());
    }

    @Test()
    void throwsWhenPlayerDoesNotExistWithGivenId() {
        // Arrange
        when(playerRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        PlayerNotFoundException exception = assertThrows(
                PlayerNotFoundException.class, () -> playerService.findById(1L)
        );

        // Assert
        assertEquals("Player with id: 1 was not found.", exception.getMessage());
    }

    @Test
    void throwsWhenFindByIdIsGivenANegativeValue() {
        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> playerService.findById(-1L)
        );

        // Assert
        assertEquals("Id cannot be negative.", exception.getMessage());
    }

    @Test
    void findsByUsername() {
        // Arrange
        optionalPlayer.get().setUsername("Isolated");
        when(playerRepository.findByUsername(anyString())).thenReturn(optionalPlayer);

        // Act
        Player player = playerService.findByUsername("Isolated");

        // Assert
        verify(playerRepository).findByUsername("Isolated");
        assertEquals("Isolated", player.getUsername());
    }

    @Test
    void throwsWhenPlayerDoesNotExistWithGivenUsername() {
        // Arrange
        when(playerRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // Act
        PlayerNotFoundException exception = assertThrows(
                PlayerNotFoundException.class, () -> playerService.findByUsername("Isolated")
        );

        // Assert
        assertEquals("Player with username: Isolated was not found.", exception.getMessage());
    }

    @Test
    void throwsWhenFindByUsernameGetsEmptyString() {
        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> playerService.findByUsername("")
        );

        assertEquals("Username must not be empty.", exception.getMessage());
    }

    @Test
    void throwsWhenCreatePlayerIsGivenNullValue() {
        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> playerService.createPlayer(null)
        );

        // Assert
        assertEquals("Player must not be null.", exception.getMessage());
    }

    @Test
    void createsPlayer() {
        // Arrange
        when(playerRepository.save(any(Player.class))).thenReturn(player);

        Player playerToSave = new Player();
        playerToSave.setId(player.getId());
        playerToSave.setUsername(player.getUsername());

        // Act
        Player createdPlayer = playerService.createPlayer(playerToSave);

        // Assert
        assertNotNull(createdPlayer);
        assertEquals(playerToSave.getId(), createdPlayer.getId());
        assertEquals(playerToSave.getUsername(), createdPlayer.getUsername());
    }

    @Test
    void throwsWhenCreatesPlayerIsGivenAlreadyExistingUsername() {
        // Arrange
        when(playerRepository.findByUsername(anyString())).thenReturn(optionalPlayer);

        Player playerToSave = new Player();
        playerToSave.setId(player.getId());
        playerToSave.setUsername(player.getUsername());

        // Act
        UsernameAlreadyExistsException exception = assertThrows(
                UsernameAlreadyExistsException.class, () -> playerService.createPlayer(playerToSave)
        );

        // Assert
        assertEquals("Username is already taken.", exception.getMessage());
    }

    @Test
    void throwsWhenPlayerToUpdateDoesNotExist() {
        // Arrange
        when(playerRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        PlayerNotFoundException exception = assertThrows(
                PlayerNotFoundException.class, () -> playerService.updatePlayer(anyLong(), player)
        );

        // Assert
        assertEquals("Player with Id: 0 was not found.", exception.getMessage());
    }

    @Test
    void throwsWhenGivenPlayerToUpdateIsNull() {
        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> playerService.updatePlayer(1L, null)
        );

        // Assert
        assertEquals("Player cannot be null.", exception.getMessage());
    }

    @Test
    void throwsWhenGivenNegativeIdToUpdatePlayer() {
        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> playerService.updatePlayer(-1L, player)
        );

        // Assert
        assertEquals("Id cannot be negative.", exception.getMessage());
    }

    @Test
    void updatesPlayer() {
        // Arrange
        when(playerRepository.findById(anyLong())).thenReturn(optionalPlayer);

        // Act
        Player newPlayerData = new Player();
        newPlayerData.setUsername("Noel");
        newPlayerData.setId(1L);

        Player updatedPlayer = playerService.updatePlayer(5L, newPlayerData);

        // Assert
        assertEquals(newPlayerData.getId(), updatedPlayer.getId());
        assertEquals(newPlayerData.getUsername(), updatedPlayer.getUsername());
    }

    @Test
    void throwsWhenDeletePlayerGetsANegativeId() {
        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> playerService.deletePlayer(-2L)
        );

        // Assert
        assertEquals("Id cannot be negative.", exception.getMessage());
    }

    @Test
    void throwsWhenPlayerToDeleteDoesNotExist() {
        // Arrange
        when(playerRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        PlayerNotFoundException exception = assertThrows(
                PlayerNotFoundException.class, () -> playerService.deletePlayer(id)
        );

        // Assert
        assertEquals(String.format("Player with id: %d was not found.", id), exception.getMessage());
    }

    @Test
    void deletesPlayer() {
        // Arrange
        when(playerRepository.findById(anyLong())).thenReturn(optionalPlayer);

        // Act
        String response = playerService.deletePlayer(id);

        // Assert
        assertEquals(String.format("Player with id: 1 was deleted.", id), response);
    }
}