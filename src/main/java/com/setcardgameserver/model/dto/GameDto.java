package com.setcardgameserver.model.dto;

import com.setcardgameserver.model.Card;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameDto {
    private Integer gameId;
    private String player1;
    private String player2;
    private ArrayList<Card> board;
    private String winner;
    private String blockedBy;
    private ArrayList<Integer> selectedCardIndexes;
    private Map<String, Integer> points;
    private ArrayList<Integer> nullCardIndexes;
    private boolean playerLeft;
}