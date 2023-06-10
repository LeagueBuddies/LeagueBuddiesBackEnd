package com.league_buddies.backend.player;

import com.league_buddies.backend.playerType.PlayerType;
import com.league_buddies.backend.position.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private Player player;

    @BeforeEach
    void setUp() {
        this.player = new Player();
    }

    @Test
    void setUsername() {
        // Act
        player.setUsername("Noel");

        // Assert
        assertEquals("Noel", player.getUsername());
    }

    @Test
    void doesNotSetUsernameWhenGivenEmptyString() {
        // Arrange
        player.setUsername("Noel");

        // Act
        player.setUsername("");

        // Assert
        assertNotEquals("", player.getUsername());
        assertEquals("Noel", player.getUsername());
    }

    @Test
    void setPassword() {
        // Act
        player.setPassword("123");

        // Assert
        assertEquals("123", player.getPassword());
    }

    @Test
    void doesNotSetPasswordWhenGivenEmptyString() {
        // Arrange
        player.setPassword("1234");

        // Act
        player.setPassword("");

        // Assert
        assertNotEquals("", player.getPassword());
        assertEquals("1234", player.getPassword());
    }

    @Test
    void setLeagueOfLegendUserName() {
        // Act
        player.setLeagueOfLegendsUserName("Isolated");

        // Assert
        assertEquals("Isolated", player.getLeagueOfLegendsUserName());
    }

    @Test
    void doesNotSetLeagueOfLegendsUserNameWhenGivenEmptyString() {
        // Arrange
        player.setLeagueOfLegendsUserName("Isolated");

        // Act
        player.setLeagueOfLegendsUserName("");

        // Assert
        assertNotEquals("", player.getLeagueOfLegendsUserName());
        assertEquals("Isolated", player.getLeagueOfLegendsUserName());
    }

    @Test
    void setFavoritePosition() {
        // Act
        player.setFavoritePosition(Position.JUNGLE);

        // Assert
        assertEquals(Position.JUNGLE, player.getFavoritePosition());
    }

    @Test
    void doesNotSetFavoritePositionWhenGivenNullValue() {
        // Arrange
        player.setFavoritePosition(Position.MID);

        // Act
        player.setFavoritePosition(null);

        // Assert
        assertEquals(Position.MID, player.getFavoritePosition());
        assertNotEquals(null, player.getFavoritePosition());
    }

    @Test
    void setFavoriteChampion() {
        // Act
        player.setFavoriteChampion("Zed");

        // Assert
        assertEquals("Zed", player.getFavoriteChampion());
    }

    @Test
    void doesNotSetFavoriteChampionWhenGivenEmptyString() {
        // Arrange
        player.setFavoriteChampion("Zed");

        // Act
        player.setFavoriteChampion("");

        // Assert
        assertEquals("Zed", player.getFavoriteChampion());
        assertNotEquals("", player.getFavoriteChampion());
    }

    @Test
    void setDescription() {
        // Act
        player.setDescription("Looking for friends to play with.");

        // Assert
        assertEquals("Looking for friends to play with.", player.getDescription());
    }

    @Test
    void doesNotSetDescriptionWhenGivenEmptyString() {
        // Arrange
        player.setDescription("Looking for friends to play with.");

        // Act
        player.setDescription("");

        // Assert
        assertEquals("Looking for friends to play with.", player.getDescription());
        assertNotEquals("", player.getDescription());
    }

    @Test
    void setPlayerType() {
        // Act
        player.setPlayerType(PlayerType.Competitive);

        // Assert
        assertEquals(PlayerType.Competitive, player.getPlayerType());
    }

    @Test
    void doesNotSetPlayerTypeWhenGivenNullValue() {
        // Arrange
        player.setPlayerType(PlayerType.Competitive);

        // Act
        player.setPlayerType(null);

        // Assert
        assertEquals(PlayerType.Competitive, player.getPlayerType());
        assertNotEquals(null, player.getPlayerType());
    }

    @Test
    void setWinRate() {
        // Act
        player.setWinRate(60.2F);

        // Assert
        assertEquals(60.2F, player.getWinRate());
    }

    @Test
    void doesNotSetWinRateWhenGivenNegativeValue() {
        // Arrange
        player.setWinRate(55.0F);

        // Act
        player.setWinRate(-50.0F);

        // Assert
        assertEquals(55.0F, player.getWinRate());
        assertNotEquals(-50.0F, player.getWinRate());

    }

    @Test
    void setEmailAddress() {
        // Act
        player.setEmailAddress("no3lcodes@gmail.com");

        // Assert
        assertEquals("no3lcodes@gmail.com", player.getEmailAddress());
    }

    @Test
    void doesNotSetEmailAddressWhenGivenEmptyString() {
        // Arrange
        player.setEmailAddress("no3lcodes@gmail.com");

        // Act
        player.setEmailAddress("");

        // Assert
        assertEquals("no3lcodes@gmail.com", player.getEmailAddress());
        assertNotEquals("", player.getEmailAddress());
    }
}
