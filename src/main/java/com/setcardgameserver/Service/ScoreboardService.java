package com.setcardgameserver.service;

import com.setcardgameserver.dto.ScoreboardDto;
import com.setcardgameserver.mapper.ScoreboardMapper;
import com.setcardgameserver.mapper.ScoreboardMapperImpl;
import com.setcardgameserver.model.Scoreboard;
import com.setcardgameserver.repository.ScoreboardRepository;
import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ScoreboardService {

    private final ScoreboardRepository scoreboardRepository;
    private final ScoreboardMapper scoreboardMapper;

    public List<ScoreboardDto> scoreboard() {
        return scoreboardMapper.entityListToDto(scoreboardRepository.findAll());
    }

    public ScoreboardDto addScore(ScoreboardDto newScore) {
        Scoreboard saved = scoreboardRepository.save(scoreboardMapper.dtoToEntity(newScore));
        return scoreboardMapper.entityToDto(saved);
    }

    public List<ScoreboardDto> findPlayerScores(UUID playerId) {
        return scoreboardMapper.entityListToDto(scoreboardRepository.findByPlayerIdOrderByDifficultyDescScoreDescTimeAsc(playerId));
    }

    public List<ScoreboardDto> findTopScores() {
        return scoreboardMapper.entityListToDto(scoreboardRepository.findByOrderByDifficultyDescScoreDescTimeAsc());
    }

    public void clearScoreboard() {
        scoreboardRepository.deleteAll();
    }
}
