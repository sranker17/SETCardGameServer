package com.setcardgameserver.service;

import com.setcardgameserver.exception.GameNotFoundException;
import com.setcardgameserver.exception.InvalidGameException;
import com.setcardgameserver.model.GameStatus;
import com.setcardgameserver.model.dto.GameplayButtonPress;
import com.setcardgameserver.model.dto.GameplayDto;
import com.setcardgameserver.model.redis.RedisGame;
import com.setcardgameserver.repository.RedisGameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisGameService {

    private final RedisGameRepository redisGameRepository;
    private static final Random random = new Random();

    /**
     * Játék létrehozása
     */
    public RedisGame createGame(String player1) {
        log.info("Creating game for player: {}", player1);

        // Ellenőrizzük, hogy van-e már játéka
        List<RedisGame> existingGames = redisGameRepository.findByPlayer1(player1);
        if (!existingGames.isEmpty()) {
            log.info("Player {} already has a game, deleting old games", player1);
            redisGameRepository.deleteAll(existingGames);
        }

        // Új játék létrehozása
        RedisGame game = new RedisGame();
        game.setGameId(generateUniqueGameId());
        game.setPlayer1(player1);
        game.setStatus(GameStatus.WAITING);
        game.getPoints().put(player1, 0);
        game.createGame();

        // Mentés Redis-be
        return redisGameRepository.save(game);
    }

    /**
     * Csatlakozás játékhoz
     */
    public RedisGame joinGame(Integer gameId, String player2) {
        log.info("Player {} joining game {}", player2, gameId);

        Optional<RedisGame> gameOpt = redisGameRepository.findById(gameId);
        if (gameOpt.isEmpty()) {
            log.error("Game {} not found", gameId);
            return null;
        }

        RedisGame game = gameOpt.get();

        if (game.getPlayer2() != null) {
            log.error("Game {} already has a second player", gameId);
            return null;
        }

        game.setPlayer2(player2);
        game.setStatus(GameStatus.IN_PROGRESS);
        game.getPoints().put(player2, 0);

        return redisGameRepository.save(game);
    }

    public RedisGame joinRandomGame(String player2) {
        log.info("Connecting player to random game: {}", player2);

        // Keresés NEW státuszú játék után
        List<RedisGame> newGames = redisGameRepository.findByStatus(GameStatus.NEW);
        Optional<RedisGame> gameOpt = newGames.stream().findFirst();

        RedisGame game;
        if (gameOpt.isEmpty()) {
            game = createNewRandomGame(player2);
            log.debug("isEmpty");
            return game;
        }

        game = gameOpt.get();

        if (game.getPlayer1().equals(player2)) {
            deleteGame(game.getGameId());
            game = createNewRandomGame(player2);
            log.debug("same game");
            return game;
        }

        if (game.getPlayer2() != null && game.getPlayer2().equals(player2)) {
            deleteGame(game.getGameId());
            game = createNewRandomGame(player2);
            log.debug("left game");
            return game;
        }

        game.setPlayer2(player2);
        game.getPoints().put(player2, 0);
        game.setStatus(GameStatus.IN_PROGRESS);
        game.setSelectedCardIndexes(new ArrayList<>());
        game.setNullCardIndexes(new ArrayList<>());
        redisGameRepository.save(game);
        log.debug("isPresent");
        return game;
    }

    /**
     * Új random játék létrehozása
     */
    private RedisGame createNewRandomGame(String player) {
        log.info("Creating new random game for player: {}", player);
        RedisGame newGame = new RedisGame();
        newGame.setGameId(generateUniqueGameId());
        newGame.setPlayer1(player);
        newGame.setStatus(GameStatus.NEW);
        newGame.setBoard(new ArrayList<>());
        newGame.setCardDeck(new ArrayList<>());
        newGame.setPoints(new HashMap<>());
        newGame.getPoints().put(player, 0);
        newGame.setSelectedCardIndexes(new ArrayList<>());
        newGame.setNullCardIndexes(new ArrayList<>());
        newGame.createGame();

        return redisGameRepository.save(newGame);
    }

    public RedisGame getGameById(Integer gameId) throws GameNotFoundException {
        return redisGameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException("Game with ID " + gameId + " not found"));
    }

    public void deleteGame(Integer gameId) {
        log.info("Deleting game {}", gameId);
        redisGameRepository.deleteById(gameId);
    }

    public void deleteAllGames() {
        log.info("Deleting all games from Redis");
        redisGameRepository.deleteAll();
    }

    /**
     * Egyedi játék ID generálás
     */
    private Integer generateUniqueGameId() {
        int gameId;
        do {
            gameId = random.nextInt(99999);
        } while (redisGameRepository.existsById(gameId));
        return gameId;
    }

    public RedisGame buttonPress(GameplayButtonPress buttonPress) throws InvalidGameException, GameNotFoundException {
        log.info("Button pressed: {}", buttonPress.getPlayerId());

        Optional<RedisGame> gameOpt = redisGameRepository.findById(buttonPress.getGameId());
        if (gameOpt.isEmpty()) {
            log.debug("RedisGame not found on button press");
            return redisGameRepository.save(new RedisGame(buttonPress.getGameId(), buttonPress.getPlayerId(), true));
        }

        RedisGame game = gameOpt.get();

        if (game.getStatus() == null || game.getStatus().equals(GameStatus.FINISHED)) {
            redisGameRepository.deleteById(game.getGameId());
            throw new InvalidGameException("RedisGame is already finished");
        }

        if (game.getBlockedBy() != null && game.getBlockedBy().equals(buttonPress.getPlayerId())) {
            log.debug("same player pressed the button");
            game.setBlockedBy(null);
            game.clearSelectedCardIndexes();
            return redisGameRepository.save(game);
        }

        if (game.getBlockedBy() != null) {
            log.debug("Both players pressed the button almost at the same time");
            return game;
        }

        if (game.getSelectedCardIndexSize() == 3) {
            game.clearSelectedCardIndexes();
        }
        game.setBlockedBy(buttonPress.getPlayerId());

        return redisGameRepository.save(game);
    }

    public RedisGame gameplay(GameplayDto gameplayDto) throws GameNotFoundException, InvalidGameException {
        log.info("Gameplay: {}", gameplayDto.getPlayerId());

        Optional<RedisGame> gameOpt = redisGameRepository.findById(gameplayDto.getGameId());
        if (gameOpt.isEmpty()) {
            throw new GameNotFoundException("RedisGame not found while in gameplay");
        }

        RedisGame game = gameOpt.get();

        if (game.getStatus().equals(GameStatus.FINISHED)) {
            redisGameRepository.deleteById(game.getGameId());
            throw new InvalidGameException("RedisGame with id %s is already finished".formatted(game.getGameId()));
        }

        if (game.getBlockedBy() != null) {
            handleGameplayWhenBlockActive(gameplayDto, game);
        }

        if (GameStatus.FINISHED.equals(game.getStatus())) {
            return game;
        }

        return redisGameRepository.save(game);
    }

    private void handleGameplayWhenBlockActive(GameplayDto gameplayDto, RedisGame game) {
        if (gameplayDto.isSelect()) {
            if (game.getSelectedCardIndexSize() == 3) {
                game.clearSelectedCardIndexes();
            }
            game.addToSelectedCardIndexes(gameplayDto.getSelectedCardIndex());

            if (game.getSelectedCardIndexSize() == 3) {
                if (game.hasSet(game.getCardsFromIndex(game.getSelectedCardIndexes()))) {
                    game.getPoints().put(gameplayDto.getPlayerId(), game.getPoints().get(gameplayDto.getPlayerId()) + 1);
                    game.changeCardsOnBoard();
                    if (!game.hasSet(game.getBoard())) {
                        game.setWinner(game.calculateWinner());
                        game.setStatus(GameStatus.FINISHED);
                        redisGameRepository.deleteById(game.getGameId());
                    }
                }
                game.setBlockedBy(null);
            }
        } else {
            game.removeFromSelectedCardIndexes(gameplayDto.getSelectedCardIndex());
        }
    }
}