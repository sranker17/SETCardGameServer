package com.setcardgameserver.Storage;

import com.setcardgameserver.Model.Game;

import java.util.HashMap;
import java.util.Map;

public class GameStorage {

    private static Map<Integer, Game> games;
    private static GameStorage instance;

    private GameStorage() {
        games = new HashMap<>();
    }

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
