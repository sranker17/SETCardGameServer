package com.setcardgameserver.repository;

import com.setcardgameserver.model.GameStatus;
import com.setcardgameserver.model.redis.RedisGame;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RedisGameRepository extends CrudRepository<@NotNull RedisGame, @NotNull Integer> {

    List<RedisGame> findByPlayer1(String player1);

    List<RedisGame> findByPlayer2(String player2);

    List<RedisGame> findByStatus(GameStatus status);

    List<RedisGame> findByPlayer1AndStatus(String player1, GameStatus status);

    boolean existsByPlayer1OrPlayer2(String player1, String player2);

    void deleteByPlayer1(String player1);

    long countByStatus(GameStatus status);


    List<RedisGame> findByPlayer1OrPlayer2(String player1, String player2);

    List<RedisGame> findByPlayer1Is(String player1);

    List<RedisGame> findByPlayer1Equals(String player1);

    List<RedisGame> findByStatusNot(GameStatus status);

    boolean existsByPlayer1(String player1);

    boolean existsByPlayer1AndStatus(String player1, GameStatus status);

    long countByPlayer1(String player1);

    long deleteByStatus(GameStatus status);

    Optional<RedisGame> findFirstByStatus(GameStatus status);

    List<RedisGame> findTop10ByStatus(GameStatus status);
}