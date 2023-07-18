package com.league_buddies.backend.user;

import com.league_buddies.backend.exception.IllegalArgumentException;
import com.league_buddies.backend.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findById(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Id cannot be negative.");
        }
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new UserNotFoundException(String.format("User with id: %d was not found.", id));
        }
    }

    public User findByEmailAddress(String username) {
        if (username.isEmpty() || username.isBlank() || username == null) {
            throw new IllegalArgumentException("Username must not be empty.");
        }
        Optional<User> optionalUser = userRepository.findByEmailAddress(username);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new UserNotFoundException(String.format("User with username: %s was not found.", username));
        }
    }

    public User updateUser(Long Id, User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        if (Id < 0) {
            throw new IllegalArgumentException("Id cannot be negative.");
        }

        Optional<User> optionalUser = userRepository.findById(Id);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(String.format("User with Id: %d was not found.", Id));
        } else {
            User currUser = optionalUser.get();
            currUser.setDisplayName(user.getDisplayName());
            currUser.setEmailAddress(user.getEmailAddress());
            currUser.setPassword(user.getPassword());
            currUser.setLeagueOfLegendsUserName(user.getLeagueOfLegendsUserName());
            currUser.setFavoritePosition(user.getFavoritePosition());
            currUser.setFavoriteChampion(user.getFavoriteChampion());
            currUser.setDescription(user.getDescription());
            currUser.setPlayerType(user.getPlayerType());
            currUser.setWinRate(user.getWinRate());
            currUser.setRole(user.getRole());
            userRepository.save(currUser);

            return currUser;
        }
    }

    public String deleteUser(Long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Id cannot be negative.");
        }

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(String.format("User with id: %d was not found.", id));
        } else {
            userRepository.delete(optionalUser.get());
            return String.format("User with id: %d was deleted.", id);
        }
    }
}
