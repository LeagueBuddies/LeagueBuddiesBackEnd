package com.league_buddies.backend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(UserDTO.builder()
                .displayName(user.getDisplayName())
                .leagueOfLegendsUserName(user.getLeagueOfLegendsUserName())
                .playerType(user.getPlayerType())
                .favoriteChampion(user.getFavoriteChampion())
                .description(user.getDescription())
                .favoritePosition(user.getFavoritePosition())
                .winRate(user.getWinRate())
                .build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable("username") String username) {
        User user = userService.findByEmailAddress(username);
        return ResponseEntity.ok(UserDTO.builder()
                .displayName(user.getDisplayName())
                .leagueOfLegendsUserName(user.getLeagueOfLegendsUserName())
                .playerType(user.getPlayerType())
                .favoriteChampion(user.getFavoriteChampion())
                .description(user.getDescription())
                .favoritePosition(user.getFavoritePosition())
                .winRate(user.getWinRate())
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id") long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(
                UserDTO.builder()
                        .displayName(user.getDisplayName())
                        .leagueOfLegendsUserName(user.getLeagueOfLegendsUserName())
                        .playerType(user.getPlayerType())
                        .favoriteChampion(user.getFavoriteChampion())
                        .description(user.getDescription())
                        .favoritePosition(user.getFavoritePosition())
                        .winRate(user.getWinRate())
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }
}
