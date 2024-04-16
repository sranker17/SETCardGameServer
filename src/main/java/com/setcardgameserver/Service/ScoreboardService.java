package com.setcardgameserver.service;

import com.setcardgameserver.model.Scoreboard;
import com.setcardgameserver.repository.ScoreboardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ScoreboardService {

    private final ScoreboardRepository scoreboardRepository;

    public List<Scoreboard> scoreboard() {
        return scoreboardRepository.findAll();
    }

    public Optional<Scoreboard> addScore(Scoreboard newScore) {
        Optional<Scoreboard> optionalScoreboard = scoreboardRepository.findByScoreId(newScore.getScoreId());

        if (optionalScoreboard.isEmpty()) {
            scoreboardRepository.save(newScore);
            return scoreboardRepository.findByScoreId(newScore.getScoreId());
        }
        return Optional.empty();
    }

    public List<Scoreboard> findPlayerScores(UUID playerId) {
        return scoreboardRepository.findByPlayerIdOrderByDifficultyDescScoreDescTimeAsc(playerId);
    }

    public List<Scoreboard> findTopScores() {
        return scoreboardRepository.findByOrderByDifficultyDescScoreDescTimeAsc();
    }

    public void clearScoreboard() {
        scoreboardRepository.deleteAll();
    }
}
