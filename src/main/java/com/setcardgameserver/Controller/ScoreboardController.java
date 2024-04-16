package com.setcardgameserver.controller;

import com.setcardgameserver.model.Scoreboard;
import com.setcardgameserver.service.ScoreboardService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class ScoreboardController {
    private final ScoreboardService scoreboardService;

    @GetMapping("/scoreboard")
    public List<Scoreboard> scoreboard() {
        return scoreboardService.scoreboard();
    }

    @PostMapping("/scoreboard")
    public Optional<Scoreboard> addScore(@RequestBody Scoreboard score) {
        if (score.getScore() > 0 && score.getScore() < 10 && score.getTime() > 0 && (score.getDifficulty().equals("Easy") || score.getDifficulty().equals("Normal"))) {
            return scoreboardService.addScore(score);
        }
        return Optional.of(new Scoreboard());
    }

    @DeleteMapping("/scoreboard")
    public void clearScoreboard() {
        scoreboardService.clearScoreboard();
    }

    @GetMapping("/available")
    public String available() {
        return "available";
    }

    @GetMapping("/scoreboard/player/{id}")
    public List<Scoreboard> playerScores(@PathVariable("id") UUID playerId) {
        return scoreboardService.findPlayerScores(playerId);
    }

    @GetMapping("/scoreboard/top")
    public List<Scoreboard> topScores() {
        return scoreboardService.findTopScores();
    }
}
