package com.setcardgameserver.service;

import com.setcardgameserver.dto.GameplayButtonPress;
import com.setcardgameserver.dto.GameplayDto;
import com.setcardgameserver.exception.InvalidGameException;
import com.setcardgameserver.exception.NotFoundException;
import com.setcardgameserver.model.Game;
import com.setcardgameserver.model.GameStatus;
import com.setcardgameserver.storage.GameStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class GameService {

    private static final Random random = new Random();

    public Game createGame(UUID player) throws NotFoundException {
        log.info("Creating game for player: {}", player);
        Optional<Game> hasGame = GameStorage.getInstance().getGames().values().stream()
                .filter(it -> it.getPlayer1().equals(player))
                .findFirst();

        if (hasGame.isPresent()) {
            Game game = GameStorage.getInstance().getGames().values().stream()
                    .filter(it -> it.getPlayer1().equals(player))
                    .findFirst().orElseThrow(() -> new NotFoundException("Game not found while creating game"));
            removeGame(game.getGameId());
        }

        Game game = new Game();
        game.createGame();
        int newGameId;

        do {
            newGameId = random.nextInt(99999);
        } while (GameStorage.getInstance().getGames().containsKey(newGameId));

        game.setGameId(newGameId);
        game.setPlayer1(player);
        game.getPoints().put(player, 0);
        game.setStatus(GameStatus.WAITING);
        GameStorage.getInstance().setGame(game);

        return game;
    }

    public Game connectToGame(UUID player2, int gameId) {
        log.info("Connecting player to game: {}", player2);
        if (!GameStorage.getInstance().getGames().containsKey(gameId)) {
            return new Game();
        }

        Game game = GameStorage.getInstance().getGames().get(gameId);

        if (game.getPlayer2() != null) {
            return new Game();
        }

        game.setPlayer2(player2);
        game.getPoints().put(player2, 0);
        game.setStatus(GameStatus.IN_PROGRESS);
        GameStorage.getInstance().setGame(game);
        return game;
    }

    public Game connectToRandomGame(UUID player2) throws NotFoundException {
        log.info("Connecting player to random game: {}", player2);
        Game game;
        Optional<Game> hasGame = GameStorage.getInstance().getGames().values().stream()
                .filter(it -> it.getStatus().equals(GameStatus.NEW))
                .findFirst();

        if (hasGame.isEmpty()) {
            game = createNewRandomGame(player2);
            log.debug("isEmpty");
        } else {
            game = GameStorage.getInstance().getGames().values().stream()
                    .filter(it -> it.getStatus().equals(GameStatus.NEW))
                    .findFirst().orElseThrow(() -> new NotFoundException("Game not found while connecting to random game"));

            if (game.getPlayer1().toString().equals(player2.toString())) {
                removeGame(game.getGameId());
                game = createNewRandomGame(player2);
                log.debug("same game");
                return game;
            }

            if (game.getPlayer2() != null && game.getPlayer2().toString().equals(player2.toString())) {
                GameStorage.getInstance().removeGame(game);
                game = createNewRandomGame(player2);
                log.debug("left game");
                return game;
            }

            game.setPlayer2(player2);
            game.getPoints().put(player2, 0);
            game.setStatus(GameStatus.IN_PROGRESS);
            GameStorage.getInstance().setGame(game);
            log.debug("isPresent");
        }
        return game;
    }

    public Game createNewRandomGame(UUID player) {
        log.info("Creating new random game for player: {}", player);
        Game newGame = new Game();
        newGame.createGame();
        newGame.setGameId(random.nextInt(99999));
        newGame.setPlayer1(player);
        newGame.getPoints().put(player, 0);
        newGame.setStatus(GameStatus.NEW);
        GameStorage.getInstance().setGame(newGame);

        return newGame;
    }

    public Game buttonPress(GameplayButtonPress buttonPress) throws InvalidGameException, NotFoundException {
        if (!GameStorage.getInstance().getGames().containsKey(buttonPress.getGameId())) {
            log.debug("Game not found on button press");
            return new Game(buttonPress.getGameId(), buttonPress.getPlayerId(), true);
        }

        Game game = GameStorage.getInstance().getGames().get(buttonPress.getGameId());

        if (game.getStatus().equals(GameStatus.FINISHED)) {
            GameStorage.getInstance().removeGame(game);
            throw new InvalidGameException("Game is already finished");
        }

        if (game.getBlockedBy() != null && game.getBlockedBy().toString().equals(buttonPress.getPlayerId().toString())) {
            log.debug("same player pressed the button");
            game.setBlockedBy(null);
            game.clearSelectedCardIndexes();
            return game;
        }

        if (game.getBlockedBy() != null && !game.getBlockedBy().toString().equals(buttonPress.getPlayerId().toString())) {
            log.debug("Both players pressed the button almost at the same time");
            return game;
        }

        if (game.getSelectedCardIndexSize() == 3) {
            game.clearSelectedCardIndexes();
        }
        game.setBlockedBy(buttonPress.getPlayerId());

        return game;
    }

    public Game gameplay(GameplayDto gameplayDto) throws NotFoundException, InvalidGameException {
        if (!GameStorage.getInstance().getGames().containsKey(gameplayDto.getGameId())) {
            throw new NotFoundException("Game not found while in gameplay");
        }

        Game game = GameStorage.getInstance().getGames().get(gameplayDto.getGameId());

        if (game.getStatus().equals(GameStatus.FINISHED)) {
            GameStorage.getInstance().removeGame(game);
            throw new InvalidGameException("Game is already finished");
        }

        if (game.getBlockedBy() != null) {
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
                            GameStorage.getInstance().removeGame(game);
                        }
                    }
                    game.setBlockedBy(null);
                }
            } else {
                game.removeFromSelectedCardIndexes(gameplayDto.getSelectedCardIndex());
            }
        }
        return game;
    }

    public Game getGameById(int gameId) throws NotFoundException {
        if (!GameStorage.getInstance().getGames().containsKey(gameId)) {
            throw new NotFoundException("Game not found with id " + gameId);
        }
        return GameStorage.getInstance().getGames().get(gameId);
    }

    public void removeGame(int gameId) throws NotFoundException {
        if (!GameStorage.getInstance().getGames().containsKey(gameId)) {
            throw new NotFoundException("Game not found while removing game");
        }

        Game game = GameStorage.getInstance().getGames().get(gameId);
        GameStorage.getInstance().removeGame(game);
        log.debug("Game removed");
    }

    public void destroyAllGames() {
        GameStorage.getInstance().removeAllGames();
    }
}
