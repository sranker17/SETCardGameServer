package com.setcardgameserver.model.dto;

import com.setcardgameserver.model.Card;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameDto {
    private int gameId;
    private UUID player1;
    private UUID player2;
    private ArrayList<Card> board;
    private UUID winner;
    private UUID blockedBy;
    private ArrayList<Integer> selectedCardIndexes;
    private Map<UUID, Integer> points;
    private ArrayList<Integer> nullCardIndexes;
    private boolean playerLeft;
}
