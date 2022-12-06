package com.setcardgameserver.DTO;

import lombok.Data;

import java.util.UUID;

@Data
public class ConnectRequest {
    private int gameId;
    private UUID playerId;
}
