package com.league_buddies.backend.user;

import com.league_buddies.backend.exception.IllegalArgumentException;
import com.league_buddies.backend.exception.UserNotFoundException;
import com.league_buddies.backend.util.MessageResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final MessageResolver messageResolver;

    public User findById(long id) {
        if (id < 0) {
            throw new IllegalArgumentException(messageResolver.getMessage("illegalArgument"));
        }
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new UserNotFoundException(messageResolver.getMessage("userNotFound"));
        }
    }

    public User findByEmailAddress(String username) {
        if (username.isEmpty() || username.isBlank() || username == null) {
            throw new IllegalArgumentException(messageResolver.getMessage("illegalArgument"));
        }
        Optional<User> optionalUser = userRepository.findByEmailAddress(username);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new UserNotFoundException(messageResolver.getMessage("userNotFound"));
        }
    }

    public User updateUser(Long Id, User user) {
        if (user == null || Id < 0) {
            throw new IllegalArgumentException(messageResolver.getMessage("illegalArgument"));
        }

        Optional<User> optionalUser = userRepository.findById(Id);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(messageResolver.getMessage("userNotFound"));
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
            currUser.setServers(user.getServers());
            userRepository.save(currUser);
            return currUser;
        }
    }

    public String deleteUser(Long id) {
        if (id < 0) {
            throw new IllegalArgumentException(messageResolver.getMessage("illegalArgument"));
        }

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(messageResolver.getMessage("userNotFound"));
        } else {
            userRepository.delete(optionalUser.get());
            return String.format(messageResolver.getMessage(
                    "userDeleted",
                    new Object[] {id}
            ), id);
        }
    }
}
