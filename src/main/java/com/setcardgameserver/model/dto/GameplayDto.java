package com.setcardgameserver.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class GameplayDto {
    private int gameId;
    private UUID playerId;
    private boolean select;  //selected = true | unselected = false
    private int selectedCardIndex;
}
