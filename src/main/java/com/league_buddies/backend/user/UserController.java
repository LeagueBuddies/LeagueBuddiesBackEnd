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
        return ResponseEntity.ok(createUserDTO(userService.findById(id)));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(createUserDTO(userService.findByEmailAddress(username)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id") long id, @RequestBody User user) {
        return ResponseEntity.ok(createUserDTO(userService.updateUser(id, user)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }

    private UserDTO createUserDTO(User user) {
        return UserDTO.builder()
                .displayName(user.getDisplayName())
                .leagueOfLegendsUserName(user.getLeagueOfLegendsUserName())
                .playerType(user.getPlayerType())
                .favoriteChampion(user.getFavoriteChampion())
                .description(user.getDescription())
                .favoritePosition(user.getFavoritePosition())
                .winRate(user.getWinRate())
                .servers(user.getServers())
                .build();
    }
}
