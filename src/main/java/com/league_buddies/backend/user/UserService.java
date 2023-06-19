package com.league_buddies.backend.user;

import com.league_buddies.backend.exception.IllegalArgumentException;
import com.league_buddies.backend.exception.UserNotFoundException;
import com.league_buddies.backend.exception.UsernameAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    UserRepository userRepository;

    @Autowired
    public UserService(UserRepository repository) {
        userRepository = repository;
    }

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

    public User findByUsername(String username) {
        if (username.isEmpty() || username.isBlank() || username == null) {
            throw new IllegalArgumentException("Username must not be empty.");
        }
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new UserNotFoundException(String.format("User with username: %s was not found.", username));
        }
    }

    public User createUser(User user) {
        if (user == null || user.getEmailAddress() == null || user.getPassword() == null) {
            // TODO Separate all the errors from exceptions into a file for the code to be DRY.
            throw new IllegalArgumentException("User must not be null.");
        }

        Optional<User> optionalUser = userRepository.findByUsername(user.getUsername());
        if (optionalUser.isPresent()) {
            throw new UsernameAlreadyExistsException("Username is already taken.");
        }

        User postedUser = userRepository.save(user);
        return postedUser;
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
            currUser.setUsername(user.getUsername());
            currUser.setEmailAddress(user.getEmailAddress());
            currUser.setPassword(user.getPassword());
            currUser.setLeagueOfLegendsUserName(user.getLeagueOfLegendsUserName());
            currUser.setFavoritePosition(user.getFavoritePosition());
            currUser.setFavoriteChampion(user.getFavoriteChampion());
            currUser.setDescription(user.getDescription());
            currUser.setPlayerType(user.getPlayerType());
            currUser.setWinRate(user.getWinRate());
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