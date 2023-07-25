package com.league_buddies.backend.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private User user;

    @BeforeEach
    void setUp() {
        this.user = new User();
    }

    @Test
    void setUsername() {
        // Act
        user.setDisplayName("Noel");

        // Assert
        assertEquals("Noel", user.getDisplayName());
    }

    @Test
    void doesNotSetUsernameWhenGivenEmptyString() {
        // Arrange
        user.setDisplayName("Noel");

        // Act
        user.setDisplayName("");

        // Assert
        assertNotEquals("", user.getDisplayName());
        assertEquals("Noel", user.getDisplayName());
    }

    @Test
    void setPassword() {
        // Act
        user.setPassword("123");

        // Assert
        assertEquals("123", user.getPassword());
    }

    @Test
    void doesNotSetPasswordWhenGivenEmptyString() {
        // Arrange
        user.setPassword("1234");

        // Act
        user.setPassword("");

        // Assert
        assertNotEquals("", user.getPassword());
        assertEquals("1234", user.getPassword());
    }

    @Test
    void setLeagueOfLegendUserName() {
        // Act
        user.setLeagueOfLegendsUserName("Isolated");

        // Assert
        assertEquals("Isolated", user.getLeagueOfLegendsUserName());
    }

    @Test
    void doesNotSetLeagueOfLegendsUserNameWhenGivenEmptyString() {
        // Arrange
        user.setLeagueOfLegendsUserName("Isolated");

        // Act
        user.setLeagueOfLegendsUserName("");

        // Assert
        assertNotEquals("", user.getLeagueOfLegendsUserName());
        assertEquals("Isolated", user.getLeagueOfLegendsUserName());
    }

    @Test
    void setFavoritePosition() {
        // Act
        user.setFavoritePosition(Position.JUNGLE);

        // Assert
        assertEquals(Position.JUNGLE, user.getFavoritePosition());
    }

    @Test
    void doesNotSetFavoritePositionWhenGivenNullValue() {
        // Arrange
        user.setFavoritePosition(Position.MID);

        // Act
        user.setFavoritePosition(null);

        // Assert
        assertEquals(Position.MID, user.getFavoritePosition());
        assertNotEquals(null, user.getFavoritePosition());
    }

    @Test
    void setFavoriteChampion() {
        // Act
        user.setFavoriteChampion("Zed");

        // Assert
        assertEquals("Zed", user.getFavoriteChampion());
    }

    @Test
    void doesNotSetFavoriteChampionWhenGivenEmptyString() {
        // Arrange
        user.setFavoriteChampion("Zed");

        // Act
        user.setFavoriteChampion("");

        // Assert
        assertEquals("Zed", user.getFavoriteChampion());
        assertNotEquals("", user.getFavoriteChampion());
    }

    @Test
    void setDescription() {
        // Act
        user.setDescription("Looking for friends to play with.");

        // Assert
        assertEquals("Looking for friends to play with.", user.getDescription());
    }

    @Test
    void doesNotSetDescriptionWhenGivenEmptyString() {
        // Arrange
        user.setDescription("Looking for friends to play with.");

        // Act
        user.setDescription("");

        // Assert
        assertEquals("Looking for friends to play with.", user.getDescription());
        assertNotEquals("", user.getDescription());
    }

    @Test
    void setPlayerType() {
        // Act
        user.setPlayerType(PlayerType.Competitive);

        // Assert
        assertEquals(PlayerType.Competitive, user.getPlayerType());
    }

    @Test
    void doesNotSetPlayerTypeWhenGivenNullValue() {
        // Arrange
        user.setPlayerType(PlayerType.Competitive);

        // Act
        user.setPlayerType(null);

        // Assert
        assertEquals(PlayerType.Competitive, user.getPlayerType());
        assertNotEquals(null, user.getPlayerType());
    }

    @Test
    void setWinRate() {
        // Act
        user.setWinRate(60.2F);

        // Assert
        assertEquals(60.2F, user.getWinRate());
    }

    @Test
    void doesNotSetWinRateWhenGivenNegativeValue() {
        // Arrange
        user.setWinRate(55.0F);

        // Act
        user.setWinRate(-50.0F);

        // Assert
        assertEquals(55.0F, user.getWinRate());
        assertNotEquals(-50.0F, user.getWinRate());

    }

    @Test
    void setEmailAddress() {
        // Act
        user.setEmailAddress("no3lcodes@gmail.com");

        // Assert
        assertEquals("no3lcodes@gmail.com", user.getEmailAddress());
    }

    @Test
    void doesNotSetEmailAddressWhenGivenEmptyString() {
        // Arrange
        user.setEmailAddress("no3lcodes@gmail.com");

        // Act
        user.setEmailAddress("");

        // Assert
        assertEquals("no3lcodes@gmail.com", user.getEmailAddress());
        assertNotEquals("", user.getEmailAddress());
    }
}
