package com.setcardgameserver.controller;

import com.setcardgameserver.model.dto.ScoreboardDto;
import com.setcardgameserver.model.dto.TopScores;
import com.setcardgameserver.service.ScoreboardService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/scoreboard")
@AllArgsConstructor
public class ScoreboardController {
    private final ScoreboardService scoreboardService;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ScoreboardDto> scoreboard() {
        return scoreboardService.scoreboard();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScoreboardDto addScore(@RequestBody ScoreboardDto score) {
        return scoreboardService.addScore(score);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void clearScoreboard() {
        scoreboardService.clearScoreboard();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    public List<ScoreboardDto> ownScores() {
        return scoreboardService.findOwnUserScores();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/user/{username}")
    @ResponseStatus(HttpStatus.OK)
    public List<ScoreboardDto> userScores(@PathVariable("username") String username) {
        return scoreboardService.findUserScores(username);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/top")
    @ResponseStatus(HttpStatus.OK)
    public TopScores topScores() {
        return scoreboardService.findTopScores();
    }
}
