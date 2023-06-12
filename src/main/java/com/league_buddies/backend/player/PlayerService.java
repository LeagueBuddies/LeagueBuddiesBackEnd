package com.league_buddies.backend.player;

import com.league_buddies.backend.exception.PlayerNotFoundException;
import com.league_buddies.backend.exception.UsernameAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerService {
    PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository repository) {
        playerRepository = repository;
    }

    public Player findById(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Id cannot be negative.");
        }
        Optional<Player> optionalPlayer = playerRepository.findById(id);
        if (optionalPlayer.isPresent()) {
            return optionalPlayer.get();
        } else {
            throw new PlayerNotFoundException(String.format("Player with id: %d was not found.", id));
        }
    }

    public Player findByUsername(String username) {
        if (username.isEmpty()) {
            throw new IllegalArgumentException("Username must not be empty.");
        }
        Optional<Player> optionalPlayer = playerRepository.findByUsername(username);
        if (optionalPlayer.isPresent()) {
            return optionalPlayer.get();
        } else {
            throw new PlayerNotFoundException(String.format("Player with username: %s was not found.", username));
        }
    }

    public Player createPlayer(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player must not be null.");
        }

        Optional<Player> optionalPlayer = playerRepository.findByUsername(player.getUsername());
        if (optionalPlayer.isPresent()) {
            throw new UsernameAlreadyExistsException("Username is already taken.");
        }

        Player postedPlayer = playerRepository.save(player);
        return postedPlayer;
    }

    public Player updatePlayer(Long Id, Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
        if (Id < 0) {
            throw new IllegalArgumentException("Id cannot be negative.");
        }

        Optional<Player> optionalPlayer = playerRepository.findById(Id);
        if (optionalPlayer.isEmpty()) {
            throw new PlayerNotFoundException(String.format("Player with Id: %d was not found.", Id));
        } else {
            Player currPlayer = optionalPlayer.get();
            currPlayer.setUsername(player.getUsername());
            currPlayer.setEmailAddress(player.getEmailAddress());
            currPlayer.setPassword(player.getPassword());
            currPlayer.setLeagueOfLegendsUserName(player.getLeagueOfLegendsUserName());
            currPlayer.setFavoritePosition(player.getFavoritePosition());
            currPlayer.setFavoriteChampion(player.getFavoriteChampion());
            currPlayer.setDescription(player.getDescription());
            currPlayer.setPlayerType(player.getPlayerType());
            currPlayer.setWinRate(player.getWinRate());
            playerRepository.save(currPlayer);

            return currPlayer;
        }
    }

    public String deletePlayer(Long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Id cannot be negative.");
        }

        Optional<Player> optionalPlayer = playerRepository.findById(id);
        if (optionalPlayer.isEmpty()) {
            throw new PlayerNotFoundException(String.format("Player with id: %d was not found.", id));
        } else {
            playerRepository.delete(optionalPlayer.get());
            return String.format("Player with id: %d was deleted.", id);
        }
    }
}
