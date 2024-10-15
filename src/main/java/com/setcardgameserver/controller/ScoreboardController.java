package com.setcardgameserver.controller;

import com.setcardgameserver.dto.ScoreboardDto;
import com.setcardgameserver.service.ScoreboardService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.ResponseEntity.status;

@RestController
@AllArgsConstructor
public class ScoreboardController {
    private final ScoreboardService scoreboardService;

    @GetMapping("/scoreboard")
    public ResponseEntity<List<ScoreboardDto>> scoreboard() {
        return status(HttpStatus.OK).body(scoreboardService.scoreboard());
    }

    @PostMapping("/scoreboard")
    public ResponseEntity<ScoreboardDto> addScore(@RequestBody ScoreboardDto score) {
        if (score.getScore() > 0 && score.getScore() < 10 && score.getTime() > 0 && (score.getDifficulty().equals("Easy") || score.getDifficulty().equals("Normal"))) {
            return status(HttpStatus.CREATED).body(scoreboardService.addScore(score));
        }
        return status(HttpStatus.BAD_REQUEST).body(null);
    }

    @DeleteMapping("/scoreboard")
    public void clearScoreboard() {
        scoreboardService.clearScoreboard();
    }

    @GetMapping("/available")
    public ResponseEntity<String> available() {
        return status(HttpStatus.OK).body("available");
    }

    @GetMapping("/scoreboard/player/{id}")
    public ResponseEntity<List<ScoreboardDto>> playerScores(@PathVariable("id") UUID playerId) {
        return status(HttpStatus.OK).body(scoreboardService.findPlayerScores(playerId));
    }

    @GetMapping("/scoreboard/top")
    public ResponseEntity<List<ScoreboardDto>> topScores() {
        return status(HttpStatus.OK).body(scoreboardService.findTopScores());
    }
}
