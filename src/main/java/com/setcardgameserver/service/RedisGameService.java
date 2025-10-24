package com.setcardgameserver.service;

import com.setcardgameserver.model.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisGameService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String GAME_PREFIX = "game:";
    private static final String ACTIVE_GAMES_KEY = "active_games";
    private static final Duration GAME_EXPIRY = Duration.ofHours(1); // Games expire after 1 hour

    /**
     * Get all games
     */
    public Map<Integer, Game> getGames() {
        Map<Integer, Game> games = new HashMap<>();
        try {
            Set<Object> gameIds = redisTemplate.opsForSet().members(ACTIVE_GAMES_KEY);
            if (gameIds != null) {
                for (Object gameIdObj : gameIds) {
                    Integer gameId = (Integer) gameIdObj;
                    Game game = getGame(gameId);
                    if (game != null) {
                        games.put(gameId, game);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error retrieving all games from Redis: {}", e.getMessage());
        }
        return games;
    }

    /**
     * Retrieve a multiplayer game from Redis
     */
    public void setGame(Game game) {
        String gameKey = GAME_PREFIX + game.getId();
        try {
            redisTemplate.opsForValue().set(gameKey, game, GAME_EXPIRY);
            redisTemplate.opsForSet().add(ACTIVE_GAMES_KEY, game.getId());
            log.info("Game {} stored in Redis", game.getId());
        } catch (Exception e) {
            log.error("Error storing game {} in Redis: {}", game.getId(), e.getMessage());
        }
    }

    /**
     * Update an existing game in Redis
     */
    public void removeGame(Game game) {
        removeGame(game.getId());
    }

    /**
     * Remove a game by ID
     */
    public void removeGame(Integer gameId) {
        String gameKey = GAME_PREFIX + gameId;
        try {
            redisTemplate.delete(gameKey);
            redisTemplate.opsForSet().remove(ACTIVE_GAMES_KEY, gameId);
            log.info("Game {} removed from Redis", gameId);
        } catch (Exception e) {
            log.error("Error removing game {} from Redis: {}", gameId, e.getMessage());
        }
    }

    /**
     * Remove all games
     */
    public void removeAllGames() {
        try {
            Set<Object> gameIds = redisTemplate.opsForSet().members(ACTIVE_GAMES_KEY);
            if (gameIds != null) {
                for (Object gameIdObj : gameIds) {
                    Integer gameId = (Integer) gameIdObj;
                    String gameKey = GAME_PREFIX + gameId;
                    redisTemplate.delete(gameKey);
                }
            }
            redisTemplate.delete(ACTIVE_GAMES_KEY);
            log.info("All games removed from Redis");
        } catch (Exception e) {
            log.error("Error removing all games from Redis: {}", e.getMessage());
        }
    }

    /**
     * Retrieve a specific game by ID
     */
    public Game getGame(Integer gameId) {
        String gameKey = GAME_PREFIX + gameId;
        try {
            Object game = redisTemplate.opsForValue().get(gameKey);
            return (Game) game;
        } catch (Exception e) {
            log.error("Error retrieving game {} from Redis: {}", gameId, e.getMessage());
        }
        return null;
    }

    /**
     * Get all active game IDs
     */
    public Set<Object> getActiveGameIds() {
        try {
            return redisTemplate.opsForSet().members(ACTIVE_GAMES_KEY);
        } catch (Exception e) {
            log.error("Error retrieving active game IDs from Redis: {}", e.getMessage());
            return Set.of();
        }
    }

    /**
     * Check if a game exists
     */
    public boolean gameExists(Integer gameId) {
        String gameKey = GAME_PREFIX + gameId;
        try {
            return redisTemplate.hasKey(gameKey);
        } catch (Exception e) {
            log.error("Error checking if game {} exists in Redis: {}", gameId, e.getMessage());
            return false;
        }
    }

    /**
     * Extend the expiry time for a game
     */
    public void extendGameExpiry(Integer gameId) {
        String gameKey = GAME_PREFIX + gameId;
        try {
            redisTemplate.expire(gameKey, GAME_EXPIRY);
            log.info("Extended expiry for game {}", gameId);
        } catch (Exception e) {
            log.error("Error extending expiry for game {} in Redis: {}", gameId, e.getMessage());
        }
    }
}
