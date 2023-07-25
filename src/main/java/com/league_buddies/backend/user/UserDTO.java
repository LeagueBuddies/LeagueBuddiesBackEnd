package com.league_buddies.backend.user;

import lombok.Builder;

import java.util.Set;

@Builder
public record UserDTO (
        String displayName,
        String leagueOfLegendsUserName,
        Position favoritePosition,
        String favoriteChampion,
        String description,
        PlayerType playerType,
        double winRate,
        Set<Server> servers
        ) {}