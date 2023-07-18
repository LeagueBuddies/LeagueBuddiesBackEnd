package com.league_buddies.backend.user;

import com.league_buddies.backend.playerType.PlayerType;
import com.league_buddies.backend.position.Position;
import lombok.Builder;

@Builder
public record UserDTO (
        String displayName,
        String leagueOfLegendsUserName,
        Position favoritePosition,
        String favoriteChampion,
        String description,
        PlayerType playerType,
        double winRate) {}