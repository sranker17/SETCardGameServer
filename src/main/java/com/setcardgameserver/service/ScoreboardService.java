package com.setcardgameserver.service;

import com.setcardgameserver.model.User;
import com.setcardgameserver.model.dto.ScoreboardDto;
import com.setcardgameserver.mapper.ScoreboardMapper;
import com.setcardgameserver.model.Scoreboard;
import com.setcardgameserver.repository.ScoreboardRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ScoreboardService {
    private final ScoreboardRepository scoreboardRepository;
    private final ScoreboardMapper scoreboardMapper;
    private final UserService userService;

    public List<ScoreboardDto> scoreboard() {
        log.info("Getting scoreboard");
        return scoreboardMapper.entityListToDto(scoreboardRepository.findAll());
    }

    public ScoreboardDto addScore(ScoreboardDto newScore) {
        //TODO add validation service with more validations all around
        if (newScore.getScore() > 0 && newScore.getScore() < 10 && newScore.getTime() > 0 && (newScore.getDifficulty().equals("Easy") || newScore.getDifficulty().equals("Normal"))) {
            log.info("Adding score to scoreboard: {}", newScore);
            Scoreboard saved = scoreboardRepository.save(scoreboardMapper.dtoToEntity(newScore));
            return scoreboardMapper.entityToDto(saved);
        } else {
            throw new IllegalArgumentException("Invalid score");
        }
    }

    public List<ScoreboardDto> findUserScores(String username) {
        //TODO validate if username is the same as the logged in user
        log.info("Getting scores for user: {}", username);
        User user = userService.findByUsername(username);
        return scoreboardMapper.entityListToDto(scoreboardRepository.findByUserIdOrderByDifficultyDescScoreDescTimeAsc(user.getId()));
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
