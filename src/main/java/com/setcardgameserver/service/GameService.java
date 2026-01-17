package com.setcardgameserver.service;

import com.setcardgameserver.exception.GameNotFoundException;
import com.setcardgameserver.exception.InvalidGameException;
import com.setcardgameserver.model.GameStatus;
import com.setcardgameserver.model.dto.GameplayButtonPress;
import com.setcardgameserver.model.dto.GameplayDto;
import com.setcardgameserver.model.Game;
import com.setcardgameserver.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final GameRepository gameRepository;
    private static final Random random = new Random();

    /**
     * Játék létrehozása
     */
    public Game createGame(String player1) {
        log.info("Creating game for player: {}", player1);

        // Ellenőrizzük, hogy van-e már játéka
        List<Game> existingGames = gameRepository.findByPlayer1(player1);
        if (!existingGames.isEmpty()) {
            log.info("Player {} already has a game, deleting old games", player1);
            gameRepository.deleteAll(existingGames);
        }

        // Új játék létrehozása
        Game game = new Game();
        game.setGameId(generateUniqueGameId());
        game.setPlayer1(player1);
        game.setStatus(GameStatus.WAITING);
        game.getPoints().put(player1, 0);
        game.createGame();

        // Mentés Redis-be
        return gameRepository.save(game);
    }

    /**
     * Csatlakozás játékhoz
     */
    public Game joinGame(Integer gameId, String player2) {
        log.info("Player {} joining game {}", player2, gameId);

        Optional<Game> gameOpt = gameRepository.findById(gameId);
        if (gameOpt.isEmpty()) {
            log.error("Game {} not found", gameId);
            return null;
        }

        Game game = gameOpt.get();

        if (game.getPlayer2() != null) {
            log.error("Game {} already has a second player", gameId);
            return null;
        }

        game.setPlayer2(player2);
        game.setStatus(GameStatus.IN_PROGRESS);
        game.getPoints().put(player2, 0);

        return gameRepository.save(game);
    }

    public Game joinRandomGame(String player2) {
        log.info("Connecting player to random game: {}", player2);

        // Keresés NEW státuszú játék után
        List<Game> newGames = gameRepository.findByStatus(GameStatus.NEW);
        Optional<Game> gameOpt = newGames.stream().findFirst();

        Game game;
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
        gameRepository.save(game);
        log.debug("isPresent");
        return game;
    }

    /**
     * Új random játék létrehozása
     */
    private Game createNewRandomGame(String player) {
        log.info("Creating new random game for player: {}", player);
        Game newGame = new Game();
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

        return gameRepository.save(newGame);
    }

    public Game getGameById(Integer gameId) throws GameNotFoundException {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException("Game with ID " + gameId + " not found"));
    }

    public void deleteGame(Integer gameId) {
        log.info("Deleting game {}", gameId);
        gameRepository.deleteById(gameId);
    }

    /**
     * Játék törlése másik játékos nyertesnek jelölésével, ha a játék még folyamatban van
     * @param gameId a játék azonosítója
     * @param playerId a játékos azonosítója, aki törli a játékot
     * @return Game objektum, ha van másik játékos és a játék folyamatban van, különben null
     * @throws GameNotFoundException ha a játék nem található
     */
    public Game destroyGameWithWinnerCheck(Integer gameId, String playerId) throws GameNotFoundException {
        log.info("Destroying game {} by player {}", gameId, playerId);
        
        Game game = getGameById(gameId);
        
        // Ha a játék még folyamatban van, a másik játékost nyertesnek jelöljük
        if (game.getStatus() != null && !game.getStatus().equals(GameStatus.FINISHED)) {
            String otherPlayer = null;
            if (game.getPlayer1() != null && !game.getPlayer1().equals(playerId)) {
                otherPlayer = game.getPlayer1();
            } else if (game.getPlayer2() != null && !game.getPlayer2().equals(playerId)) {
                otherPlayer = game.getPlayer2();
            }
            
            if (otherPlayer != null) {
                game.setWinner(otherPlayer);
                game.setBlockedBy(null);
                game.setPlayerLeft(true);
                game.setStatus(GameStatus.FINISHED);
                log.info("Game {} finished, winner: {}", gameId, otherPlayer);
                return game;
            }
        }
        
        return null;
    }

    public void deleteAllGames() {
        log.info("Deleting all games from Redis");
        gameRepository.deleteAll();
    }

    /**
     * Egyedi játék ID generálás
     */
    private Integer generateUniqueGameId() {
        int gameId;
        do {
            gameId = random.nextInt(99999);
        } while (gameRepository.existsById(gameId));
        return gameId;
    }

    public Game buttonPress(GameplayButtonPress buttonPress) throws InvalidGameException, GameNotFoundException {
        log.info("Button pressed: {}", buttonPress.getPlayerId());

        Optional<Game> gameOpt = gameRepository.findById(buttonPress.getGameId());
        if (gameOpt.isEmpty()) {
            log.debug("RedisGame not found on button press");
            return gameRepository.save(new Game(buttonPress.getGameId(), buttonPress.getPlayerId(), true));
        }

        Game game = gameOpt.get();

        if (game.getStatus() == null || game.getStatus().equals(GameStatus.FINISHED)) {
            gameRepository.deleteById(game.getGameId());
            throw new InvalidGameException("RedisGame is already finished");
        }

        if (game.getBlockedBy() != null && game.getBlockedBy().equals(buttonPress.getPlayerId())) {
            log.debug("same player pressed the button");
            game.setBlockedBy(null);
            game.clearSelectedCardIndexes();
            return gameRepository.save(game);
        }

        if (game.getBlockedBy() != null) {
            log.debug("Both players pressed the button almost at the same time");
            return game;
        }

        if (game.getSelectedCardIndexSize() == 3) {
            game.clearSelectedCardIndexes();
        }
        game.setBlockedBy(buttonPress.getPlayerId());

        return gameRepository.save(game);
    }

    public Game gameplay(GameplayDto gameplayDto) throws GameNotFoundException, InvalidGameException {
        log.info("Gameplay: {}", gameplayDto.getPlayerId());

        Optional<Game> gameOpt = gameRepository.findById(gameplayDto.getGameId());
        if (gameOpt.isEmpty()) {
            throw new GameNotFoundException("RedisGame not found while in gameplay");
        }

        Game game = gameOpt.get();

        if (game.getStatus().equals(GameStatus.FINISHED)) {
            gameRepository.deleteById(game.getGameId());
            throw new InvalidGameException("RedisGame with id %s is already finished".formatted(game.getGameId()));
        }

        if (game.getBlockedBy() != null) {
            handleGameplayWhenBlockActive(gameplayDto, game);
        }

        if (GameStatus.FINISHED.equals(game.getStatus())) {
            return game;
        }

        return gameRepository.save(game);
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
                        gameRepository.deleteById(game.getGameId());
                    }
                }
                game.setBlockedBy(null);
            }
        } else {
            game.removeFromSelectedCardIndexes(gameplayDto.getSelectedCardIndex());
        }
    }
}