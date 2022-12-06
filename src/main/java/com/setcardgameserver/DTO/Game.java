package com.setcardgameserver.DTO;

import com.setcardgameserver.Model.Card;
import lombok.Data;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@Data
public class Game {
    private int gameId;
    private UUID player1;
    private UUID player2;
    private ArrayList<Card> board;
    private UUID winner;
    private UUID blockedBy;
    private ArrayList<Integer> selectedCardIndexes;
    private Map<UUID, Integer> points;
    private ArrayList<Integer> nullCardIndexes;

    public Game(com.setcardgameserver.Model.Game game) {
        this.gameId = game.getGameId();
        this.player1 = game.getPlayer1();
        this.player2 = game.getPlayer2();
        this.board = game.getBoard();
        this.winner = game.getWinner();
        this.blockedBy = game.getBlockedBy();
        this.selectedCardIndexes = game.getSelectedCardIndexes();
        this.points = game.getPoints();
        this.nullCardIndexes = game.getNullCardIndexes();
    }
}
