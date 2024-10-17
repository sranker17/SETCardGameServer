package com.setcardgameserver.service;

import com.setcardgameserver.exception.InvalidScoreException;
import com.setcardgameserver.mapper.ScoreboardMapper;
import com.setcardgameserver.model.Difficulty;
import com.setcardgameserver.model.Scoreboard;
import com.setcardgameserver.model.User;
import com.setcardgameserver.model.dto.ScoreboardDto;
import com.setcardgameserver.model.dto.TopScores;
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
        userService.validateUser(newScore.getUsername());
        if (newScore.getScore() > 0 && newScore.getScore() < 10 && newScore.getTime() > 0
                && (newScore.getDifficulty().equals(Difficulty.EASY.toString()) || newScore.getDifficulty().equals(Difficulty.NORMAL.toString()))) {
            log.info("Adding score to scoreboard: {}", newScore);
            Scoreboard score = scoreboardMapper.dtoToEntity(newScore);
            Scoreboard saved = scoreboardRepository.save(score);
            return scoreboardMapper.entityToDto(saved);
        } else {
            throw new InvalidScoreException("The score or the difficulty is invalid");
        }
    }

    public TopScores getUserScores(String username) {
        log.info("Getting top scores for user: {}", username);
        TopScores topUserScores = new TopScores();
        topUserScores.setEasyScores(scoreboardMapper.entityListToDto(
                scoreboardRepository.findTop100ByUsernameAndDifficultyOrderByScoreDescTimeAsc(username, Difficulty.EASY.toString())));
        topUserScores.setNormalScores(scoreboardMapper.entityListToDto(
                scoreboardRepository.findTop100ByUsernameAndDifficultyOrderByScoreDescTimeAsc(username, Difficulty.NORMAL.toString())));
        return topUserScores;
    }

    public TopScores getTopScores() {
        log.info("Getting top scores");
        TopScores topScores = new TopScores();
        topScores.setEasyScores(scoreboardMapper.entityListToDto(scoreboardRepository.findTop100ByDifficultyOrderByScoreDescTimeAsc(Difficulty.EASY.toString())));
        topScores.setNormalScores(scoreboardMapper.entityListToDto(scoreboardRepository.findTop100ByDifficultyOrderByScoreDescTimeAsc(Difficulty.NORMAL.toString())));
        return topScores;
    }

    public void clearScoreboard() {
        log.warn("Clearing scoreboard");
        scoreboardRepository.deleteAll();
    }

    public TopScores getOwnUserScores() {
        User user = userService.getLoggedInUser();
        log.info("Getting top user scores for self: {}", user.getUsername());
        return getUserScores(user.getUsername());
    }
}
