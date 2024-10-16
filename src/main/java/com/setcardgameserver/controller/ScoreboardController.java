package com.setcardgameserver.controller;

import com.setcardgameserver.model.User;
import com.setcardgameserver.model.dto.ScoreboardDto;
import com.setcardgameserver.service.ScoreboardService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.status;

//TODO change to @RestController("/scoreboard")
@RestController
@AllArgsConstructor
public class ScoreboardController {
    private final ScoreboardService scoreboardService;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/scoreboard")
    public ResponseEntity<List<ScoreboardDto>> scoreboard() {
        return status(HttpStatus.OK).body(scoreboardService.scoreboard());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/scoreboard")
    public ResponseEntity<ScoreboardDto> addScore(@RequestBody ScoreboardDto score) {
        return status(HttpStatus.CREATED).body(scoreboardService.addScore(score));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    @DeleteMapping("/scoreboard")
    public void clearScoreboard() {
        scoreboardService.clearScoreboard();
    }

    //TODO delete
    @GetMapping("/available")
    public ResponseEntity<String> available() {
        return status(HttpStatus.OK).body("available");
    }

    @PreAuthorize("isAuthenticated()")
    //TODO instead of player, use user and use username instead of id
    @GetMapping("/scoreboard/player/{id}")
    public ResponseEntity<List<ScoreboardDto>> userScores(@PathVariable("id") UUID userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getId().equals(userId) || currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") || auth.getAuthority().equals("ROLE_SUPER_ADMIN"))) {
            return status(HttpStatus.OK).body(scoreboardService.findUserScores(userId));
        } else {
            return status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/scoreboard/top")
    public ResponseEntity<List<ScoreboardDto>> topScores() {
        return status(HttpStatus.OK).body(scoreboardService.findTopScores());
    }
}
