package com.league_buddies.backend.user;

import com.league_buddies.backend.playerType.PlayerType;
import com.league_buddies.backend.position.Position;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="user_table")
public class User {
    @Id
    @GeneratedValue
    private long id;

    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String emailAddress;

    private String leagueOfLegendsUserName;

    private Position favoritePosition;

    private String favoriteChampion;

    @Column
    private String description;

    private PlayerType playerType;

    @Column
    private float winRate;

    public User(String emailAddress, String password) {
        this.emailAddress = emailAddress;
        this.password = password;
    }

    public void setUsername(String username) {
        if (username != null && !username.isEmpty()) {
            this.username = username;
        }
    }

    public void setPassword(String password) {
        if (password != null && !password.isEmpty()) {
            this.password = password;
        }
    }

    public void setLeagueOfLegendsUserName(String leagueOfLegendsUserName) {
        if (leagueOfLegendsUserName != null && !leagueOfLegendsUserName.isEmpty()) {
            this.leagueOfLegendsUserName = leagueOfLegendsUserName;
        }
    }

    public void setFavoritePosition(Position favoritePosition) {
        if (favoritePosition != null) {
            this.favoritePosition = favoritePosition;
        }
    }

    public void setFavoriteChampion(String favoriteChampion) {
        if (favoriteChampion != null && !favoriteChampion.isEmpty()) {
            this.favoriteChampion = favoriteChampion;
        }
    }

    public void setDescription(String description) {
        if (description != null && !description.isEmpty()) {
            this.description = description;
        }
    }

    public void setPlayerType(PlayerType playerType) {
        if (playerType != null) {
            this.playerType = playerType;
        }
    }

    public void setWinRate(float winRate) {
        if (winRate > 0F) {
            this.winRate = winRate;
        }

    }

    public void setEmailAddress(String emailAddress) {
        if (emailAddress != null && !emailAddress.isEmpty()) {
            this.emailAddress = emailAddress;
        }
    }
}
