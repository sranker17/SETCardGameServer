package com.setcardgameserver.repository;

import com.setcardgameserver.model.Scoreboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScoreboardRepository extends JpaRepository<Scoreboard, Long> {

    List<Scoreboard> findByUsernameOrderByDifficultyDescScoreDescTimeAsc(String username);

    List<Scoreboard> findTop100ByDifficultyOrderByScoreDescTimeAsc(String difficulty);
}
