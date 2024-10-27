package com.setcardgameserver.model.dto;

import lombok.Data;

@Data
public class GameplayButtonPress {
    private int gameId;
    private String playerId;
}