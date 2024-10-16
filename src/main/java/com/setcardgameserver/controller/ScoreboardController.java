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

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/scoreboard")
@AllArgsConstructor
public class ScoreboardController {
    private final ScoreboardService scoreboardService;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<List<ScoreboardDto>> scoreboard() {
        return status(HttpStatus.OK).body(scoreboardService.scoreboard());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<ScoreboardDto> addScore(@RequestBody ScoreboardDto score) {
        return status(HttpStatus.CREATED).body(scoreboardService.addScore(score));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    @DeleteMapping
    public void clearScoreboard() {
        scoreboardService.clearScoreboard();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user")
    public ResponseEntity<List<ScoreboardDto>> ownScores() {
        //TODO move to service
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        return status(HttpStatus.OK).body(scoreboardService.findUserScores(currentUser.getUsername()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/user/{username}")
    public ResponseEntity<List<ScoreboardDto>> userScores(@PathVariable("username") String username) {
        return status(HttpStatus.OK).body(scoreboardService.findUserScores(username));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/top")
    public ResponseEntity<List<ScoreboardDto>> topScores() {
        return status(HttpStatus.OK).body(scoreboardService.findTopScores());
    }
}
