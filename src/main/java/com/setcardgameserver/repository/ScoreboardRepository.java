package com.setcardgameserver.repository;

import com.setcardgameserver.model.Scoreboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreboardRepository extends JpaRepository<Scoreboard, Long> {

    List<Scoreboard> findTop100ByUsernameAndDifficultyOrderByScoreDescTimeAsc(String username, String difficulty);

    List<Scoreboard> findTop100ByDifficultyOrderByScoreDescTimeAsc(String difficulty);
}
