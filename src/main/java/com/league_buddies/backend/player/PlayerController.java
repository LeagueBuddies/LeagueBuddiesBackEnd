package com.league_buddies.backend.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/player")
public class PlayerController {
    PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable("id") long id) {
        return ResponseEntity.ok(playerService.findById(id));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Player> getPlayerByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(playerService.findByUsername(username));
    }

    @PostMapping()
    public ResponseEntity<Player> postPlayer(@RequestBody Player player) {
        // TODO fix this because what if the player in the body comes with everything?
        return ResponseEntity.ok(playerService.createPlayer(player));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable("id") long id, @RequestBody Player player) {
        return ResponseEntity.ok(playerService.updatePlayer(id, player));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePlayer(@PathVariable("id") long id) {
        return ResponseEntity.ok(playerService.deletePlayer(id));
    }
}
