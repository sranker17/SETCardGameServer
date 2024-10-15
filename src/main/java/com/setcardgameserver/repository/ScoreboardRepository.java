package com.setcardgameserver.repository;

import com.setcardgameserver.model.Scoreboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScoreboardRepository extends JpaRepository<Scoreboard, Long> {

    @Query("SELECT s FROM Scoreboard s WHERE s.scoreId = ?1")
    Optional<Scoreboard> findByScoreId(Long scoreId);

    List<Scoreboard> findByPlayerIdOrderByDifficultyDescScoreDescTimeAsc(UUID playerId);

    List<Scoreboard> findByOrderByDifficultyDescScoreDescTimeAsc();
}
