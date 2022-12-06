package com.setcardgameserver.DTO;

import lombok.Data;

import java.util.UUID;

@Data
public class Gameplay {

    private int gameId;
    private UUID playerId;
    private boolean select;  //selected = true | deselected = false
    private int selectedCardIndex;
}
