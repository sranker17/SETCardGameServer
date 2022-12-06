package com.setcardgameserver.Service;

import com.setcardgameserver.Exception.InvalidGameException;
import com.setcardgameserver.Exception.NotFoundException;
import com.setcardgameserver.Model.Game;
import com.setcardgameserver.Model.GameStatus;
import com.setcardgameserver.DTO.GameplayButtonPress;
import com.setcardgameserver.DTO.Gameplay;
import com.setcardgameserver.Storage.GameStorage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GameService {

    public Game createGame(UUID player) throws NotFoundException {
        Optional<Game> hasGame = GameStorage.getInstance().getGames().values().stream()
                .filter(it -> it.getPlayer1().equals(player))
                .findFirst();

        if (hasGame.isPresent()) {
            Game game = GameStorage.getInstance().getGames().values().stream()
                    .filter(it -> it.getPlayer1().equals(player))
                    .findFirst().orElseThrow(() -> new NotFoundException("Game not found"));
            removeGame(game.getGameId());
        }

        Game game = new Game();
        game.createGame();
        int newGameId;

        do {
            newGameId = new Random().nextInt(99999);
        } while (GameStorage.getInstance().getGames().containsKey(newGameId));

        game.setGameId(newGameId);
        game.setPlayer1(player);
        game.getPoints().put(player, 0);
        game.setStatus(GameStatus.WAITING);
        GameStorage.getInstance().setGame(game);

        return game;
    }

    public Game connectToGame(UUID player2, int gameId) {
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
        Game game;
        Optional<Game> hasGame = GameStorage.getInstance().getGames().values().stream()
                .filter(it -> it.getStatus().equals(GameStatus.NEW))
                .findFirst();

        if (hasGame.isEmpty()) {
            game = createNewRandomGame(player2);
            System.out.println("isEmpty");
            return game;
        } else if (hasGame.isPresent()) {
            game = GameStorage.getInstance().getGames().values().stream()
                    .filter(it -> it.getStatus().equals(GameStatus.NEW))
                    .findFirst().orElseThrow(() -> new NotFoundException("Game not found"));

            if (game.getPlayer1().toString().equals(player2.toString())) {
                removeGame(game.getGameId());
                game = createNewRandomGame(player2);
                System.out.println("same game");
                return game;
            }

            if (game.getPlayer2() != null) {
                if (game.getPlayer2().toString().equals(player2.toString())) {
                    GameStorage.getInstance().removeGame(game);
                    game = createNewRandomGame(player2);
                    System.out.println("left game");
                    return game;
                }
            }

            game.setPlayer2(player2);
            game.getPoints().put(player2, 0);
            game.setStatus(GameStatus.IN_PROGRESS);
            GameStorage.getInstance().setGame(game);
            System.out.println("isPresent");
            return game;
        }

        System.out.println("nothing");
        return null;
    }

    public Game createNewRandomGame(UUID player) {
        Game newGame = new Game();
        newGame.createGame();
        newGame.setGameId(new Random().nextInt(99999));
        newGame.setPlayer1(player);
        newGame.getPoints().put(player, 0);
        newGame.setStatus(GameStatus.NEW);
        GameStorage.getInstance().setGame(newGame);

        return newGame;
    }

    public Game buttonPress(GameplayButtonPress buttonPress) throws InvalidGameException, NotFoundException {
        if (!GameStorage.getInstance().getGames().containsKey(buttonPress.getGameId())) {
            throw new NotFoundException("Game not found");
        }

        Game game = GameStorage.getInstance().getGames().get(buttonPress.getGameId());

        if (game.getStatus().equals(GameStatus.FINISHED)) {
            GameStorage.getInstance().removeGame(game);
            throw new InvalidGameException("Game is already finished");
        }

        if (game.getBlockedBy() != null && game.getBlockedBy().toString().equals(buttonPress.getPlayerId().toString())) {
            System.out.println("same player pressed the button");
            game.setBlockedBy(null);
            game.clearSelectedCardIndexes();
            return game;
        }

        if (game.getBlockedBy() != null && !game.getBlockedBy().toString().equals(buttonPress.getPlayerId().toString())) {
            System.out.println("Both players pressed the button almost at the same time");
            return game;
        }

        if (game.getSelectedCardIndexSize() == 3) {
            game.clearSelectedCardIndexes();
        }
        game.setBlockedBy(buttonPress.getPlayerId());

        return game;
    }

    public Game gameplay(Gameplay gameplay) throws NotFoundException, InvalidGameException {
        if (!GameStorage.getInstance().getGames().containsKey(gameplay.getGameId())) {
            throw new NotFoundException("Game not found");
        }

        Game game = GameStorage.getInstance().getGames().get(gameplay.getGameId());

        if (game.getStatus().equals(GameStatus.FINISHED)) {
            GameStorage.getInstance().removeGame(game);
            throw new InvalidGameException("Game is already finished");
        }

        if (game.getBlockedBy() != null) {
            if (gameplay.isSelect()) {
                if (game.getSelectedCardIndexSize() == 3) {
                    game.clearSelectedCardIndexes();
                }
                game.addToSelectedCardIndexes(gameplay.getSelectedCardIndex());

                if (game.getSelectedCardIndexSize() == 3) {
                    if (game.hasSet(game.getCardsFromIndex(game.getSelectedCardIndexes()))) {
                        game.getPoints().put(gameplay.getPlayerId(), game.getPoints().get(gameplay.getPlayerId()) + 1);
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
                game.removeFromSelectedCardIndexes(gameplay.getSelectedCardIndex());
            }
        }
        return game;
    }

    public Game getGameById(int gameId) throws NotFoundException {
        if (!GameStorage.getInstance().getGames().containsKey(gameId)) {
            throw new NotFoundException("Game not found");
        }
        return GameStorage.getInstance().getGames().get(gameId);
    }

    public void removeGame(int gameId) throws NotFoundException {
        if (!GameStorage.getInstance().getGames().containsKey(gameId)) {
            throw new NotFoundException("Game not found");
        }

        Game game = GameStorage.getInstance().getGames().get(gameId);
        GameStorage.getInstance().removeGame(game);
        System.out.println("Game removed\n");
    }

    public void destroyAllGames() {
        GameStorage.getInstance().removeAllGames();
    }
}
