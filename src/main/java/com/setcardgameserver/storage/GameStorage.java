package com.setcardgameserver.storage;

import com.setcardgameserver.model.Game;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores all ongoing multiplayer games
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameStorage {

    private static final Map<Integer, Game> games = new HashMap<>();
    private static GameStorage instance;

    public static synchronized GameStorage getInstance() {
        if (instance == null) {
            instance = new GameStorage();
        }
        return instance;
    }

    public Map<Integer, Game> getGames() {
        return games;
    }

    public void setGame(Game game) {
        games.put(game.getGameId(), game);
    }

    public void removeGame(Game game) {
        games.remove(game.getGameId());
    }

    public void removeAllGames() {
        games.clear();
    }
}
