package com.setcardgameserver.service;

import com.setcardgameserver.exception.GameNotFoundException;
import com.setcardgameserver.exception.InvalidGameException;
import com.setcardgameserver.model.Game;
import com.setcardgameserver.model.GameStatus;
import com.setcardgameserver.model.dto.GameplayButtonPress;
import com.setcardgameserver.model.dto.GameplayDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
@Slf4j
public class GameService {
    private static final Random random = new Random();
    private final RedisGameService redisGameService;

    public Game createGame(String player) throws GameNotFoundException {
        log.info("Creating game for player: {}", player);
        Optional<Game> hasGame = redisGameService
                .getGames()
                .values().stream()
                .filter(it -> it.getPlayer1().equals(player))
                .findFirst();

        if (hasGame.isPresent()) {
            Game game = redisGameService
                    .getGames()
                    .values().stream()
                    .filter(it -> it.getPlayer1().equals(player))
                    .findFirst().orElseThrow(() -> new GameNotFoundException("Game not found while creating game"));
            removeGame(game.getId());
        }

        Game game = new Game();
        game.createGame();
        int newGameId;

        do {
            newGameId = random.nextInt(99999);
        } while (redisGameService.gameExists(newGameId));

        game.setId(newGameId);
        game.setPlayer1(player);
        game.getPoints().put(player, 0);
        game.setStatus(GameStatus.WAITING);
        redisGameService.setGame(game);

        return game;
    }

    public Game connectToGame(String player2, int gameId) {
        log.info("Connecting player to game: {}", player2);
        if (!redisGameService.gameExists(gameId)) {
            return new Game();
        }

        Game game = redisGameService.getGame(gameId);

        if (game.getPlayer2() != null) {
            return new Game();
        }

        game.setPlayer2(player2);
        game.getPoints().put(player2, 0);
        game.setStatus(GameStatus.IN_PROGRESS);
        redisGameService.setGame(game);
        return game;
    }

    public Game connectToRandomGame(String player2) throws GameNotFoundException {
        log.info("Connecting player to random game: {}", player2);
        Game game = redisGameService.getGames().values().stream()
                .filter(it -> it.getStatus().equals(GameStatus.NEW))
                .findFirst()
                .orElse(createNewRandomGame(player2));

        if (game.getPlayer1().equals(player2)) {
            removeGame(game.getId());
            game = createNewRandomGame(player2);
            log.debug("same game");
            return game;
        }

        if (game.getPlayer2() != null && game.getPlayer2().equals(player2)) {
            redisGameService.removeGame(game.getId());
            game = createNewRandomGame(player2);
            log.debug("left game");
            return game;
        }

        game.setPlayer2(player2);
        game.getPoints().put(player2, 0);
        game.setStatus(GameStatus.IN_PROGRESS);
        redisGameService.setGame(game);
        log.debug("isPresent");
        return game;
    }

    public Game createNewRandomGame(String player) {
        log.info("Creating new random game for player: {}", player);
        Game newGame = new Game();
        newGame.createGame();
        newGame.setId(random.nextInt(99999));
        newGame.setPlayer1(player);
        newGame.getPoints().put(player, 0);
        newGame.setStatus(GameStatus.NEW);
        redisGameService.setGame(newGame);

        return newGame;
    }

    public Game buttonPress(GameplayButtonPress buttonPress) throws InvalidGameException, GameNotFoundException {
        log.info("Button pressed: {}", buttonPress.getPlayerId());
        if (!redisGameService.gameExists(buttonPress.getGameId())) {
            log.debug("Game not found on button press");
            return new Game(buttonPress.getGameId(), buttonPress.getPlayerId(), true);
        }

        Game game = redisGameService.getGame(buttonPress.getGameId());

        if (game.getStatus().equals(GameStatus.FINISHED)) {
            redisGameService.removeGame(game.getId());
            throw new InvalidGameException("Game is already finished");
        }

        if (game.getBlockedBy() != null && game.getBlockedBy().equals(buttonPress.getPlayerId())) {
            log.debug("same player pressed the button");
            game.setBlockedBy(null);
            game.clearSelectedCardIndexes();
            return game;
        }

        if (game.getBlockedBy() != null) {
            log.debug("Both players pressed the button almost at the same time");
            return game;
        }

        if (game.getSelectedCardIndexSize() == 3) {
            game.clearSelectedCardIndexes();
        }
        game.setBlockedBy(buttonPress.getPlayerId());

        return game;
    }

    public Game gameplay(GameplayDto gameplayDto) throws GameNotFoundException, InvalidGameException {
        log.info("Gameplay: {}", gameplayDto.getPlayerId());
        if (!redisGameService.gameExists(gameplayDto.getGameId())) {
            throw new GameNotFoundException("Game not found while in gameplay");
        }

        Game game = redisGameService.getGame(gameplayDto.getGameId());

        if (game.getStatus().equals(GameStatus.FINISHED)) {
            redisGameService.removeGame(game.getId());
            throw new InvalidGameException("Game with id %s is already finished".formatted(game.getId()));
        }
        if (game.getBlockedBy() != null) {
            handleGameplayWhenBlockActive(gameplayDto, game);
        }
        return game;
    }

    private void handleGameplayWhenBlockActive(GameplayDto gameplayDto, Game game) {
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
                        redisGameService.removeGame(game.getId());
                    }
                }
                game.setBlockedBy(null);
            }
        } else {
            game.removeFromSelectedCardIndexes(gameplayDto.getSelectedCardIndex());
        }
    }

    public Game getGameById(int gameId) throws GameNotFoundException {
        log.info("Getting game by id: {}", gameId);
        if (!redisGameService.gameExists(gameId)) {
            throw new GameNotFoundException("Game not found with id %s".formatted(gameId));
        }
        return redisGameService.getGame(gameId);
    }

    public void removeGame(int gameId) throws GameNotFoundException {
        log.info("Removing game: {}", gameId);
        if (!redisGameService.gameExists(gameId)) {
            throw new GameNotFoundException("Game with id %S not found while removing game".formatted(gameId));
        }
        redisGameService.removeGame(gameId);
    }

    public void destroyAllGames() {
        log.info("Destroying all games");
        redisGameService.removeAllGames();
    }
}
