package com.setcardgameserver.service;

import com.setcardgameserver.model.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisGameService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String GAME_PREFIX = "game:";
    private static final String ACTIVE_GAMES_KEY = "active_games";
    private static final Duration GAME_EXPIRY = Duration.ofHours(2); // Games expire after 2 hours

    /**
     * Store a multiplayer game in Redis
     */
    public void storeGame(Game game) {
        String gameKey = GAME_PREFIX + game.getGameId();
        try {
            redisTemplate.opsForValue().set(gameKey, game, GAME_EXPIRY);
            redisTemplate.opsForSet().add(ACTIVE_GAMES_KEY, game.getGameId());
            log.info("Game {} stored in Redis", game.getGameId());
        } catch (Exception e) {
            log.error("Error storing game {} in Redis: {}", game.getGameId(), e.getMessage());
        }
    }

    /**
     * Retrieve a multiplayer game from Redis
     */
    public Game getGame(String gameId) {
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
     * Update an existing game in Redis
     */
    public void updateGame(Game game) {
        storeGame(game); // Redis will overwrite the existing key
    }

    /**
     * Remove a game from Redis
     */
    public void removeGame(String gameId) {
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
     * Check if a game exists in Redis
     */
    public boolean gameExists(String gameId) {
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
    public void extendGameExpiry(String gameId) {
        String gameKey = GAME_PREFIX + gameId;
        try {
            redisTemplate.expire(gameKey, GAME_EXPIRY);
            log.info("Extended expiry for game {}", gameId);
        } catch (Exception e) {
            log.error("Error extending expiry for game {} in Redis: {}", gameId, e.getMessage());
        }
    }
}
