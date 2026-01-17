package com.setcardgameserver.repository;

import com.setcardgameserver.model.Game;
import com.setcardgameserver.model.GameStatus;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends CrudRepository<@NotNull Game, @NotNull Integer> {

    List<Game> findByPlayer1(String player1);

    List<Game> findByStatus(GameStatus status);
}