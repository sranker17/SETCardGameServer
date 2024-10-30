package com.setcardgameserver.model.dto;

import lombok.Data;

@Data
public class GameplayDto {
    private int gameId;
    private String playerId;
    private boolean select;  //selected = true | unselected = false
    private int selectedCardIndex;
}