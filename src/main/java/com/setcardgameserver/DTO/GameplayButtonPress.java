package com.setcardgameserver.DTO;

import lombok.Data;

import java.util.UUID;

@Data
public class GameplayButtonPress {

    private int gameId;
    private UUID playerId;
}
