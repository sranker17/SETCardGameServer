package com.setcardgameserver.controller;

import com.setcardgameserver.model.dto.ScoreboardDto;
import com.setcardgameserver.model.dto.TopScores;
import com.setcardgameserver.service.ScoreboardService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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

    @Hidden
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ScoreboardDto> scoreboard() {
        return scoreboardService.scoreboard();
    }

    @Operation(summary = "Add new score", description = "Add new score to the scoreboard, needs authentication")
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScoreboardDto addScore(@Valid @RequestBody ScoreboardDto score) {
        return scoreboardService.addScore(score);
    }

    @Hidden
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void clearScoreboard() {
        scoreboardService.clearScoreboard();
    }

    @Operation(summary = "Get own top scores", description = "Get logged in user's top scores, needs authentication")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    public TopScores getOwnUserScores() {
        return scoreboardService.getOwnUserScores();
    }

    @Hidden
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/user/{username}")
    @ResponseStatus(HttpStatus.OK)
    public TopScores getUserScores(@PathVariable("username") String username) {
        return scoreboardService.getUserScores(username);
    }

    @Operation(summary = "Get top scores", description = "Get top scores, needs authentication")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/top")
    @ResponseStatus(HttpStatus.OK)
    public TopScores getTopScores() {
        return scoreboardService.getTopScores();
    }
}
