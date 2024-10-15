package com.setcardgameserver.service;

import com.setcardgameserver.dto.ScoreboardDto;
import com.setcardgameserver.mapper.ScoreboardMapper;
import com.setcardgameserver.model.Scoreboard;
import com.setcardgameserver.repository.ScoreboardRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class ScoreboardService {

    private final ScoreboardRepository scoreboardRepository;
    private final ScoreboardMapper scoreboardMapper;

    public List<ScoreboardDto> scoreboard() {
        log.info("Getting scoreboard");
        return scoreboardMapper.entityListToDto(scoreboardRepository.findAll());
    }

    public ScoreboardDto addScore(ScoreboardDto newScore) {
        log.info("Adding score to scoreboard: {}", newScore);
        Scoreboard saved = scoreboardRepository.save(scoreboardMapper.dtoToEntity(newScore));
        return scoreboardMapper.entityToDto(saved);
    }

    public List<ScoreboardDto> findPlayerScores(UUID playerId) {
        log.info("Getting scores for player: {}", playerId);
        return scoreboardMapper.entityListToDto(scoreboardRepository.findByPlayerIdOrderByDifficultyDescScoreDescTimeAsc(playerId));
    }

    public List<ScoreboardDto> findTopScores() {
        log.info("Getting top scores");
        return scoreboardMapper.entityListToDto(scoreboardRepository.findByOrderByDifficultyDescScoreDescTimeAsc());
    }

    public void clearScoreboard() {
        log.info("Clearing scoreboard");
        scoreboardRepository.deleteAll();
    }
}
