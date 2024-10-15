package com.setcardgameserver.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ConnectRequestDto {
    private int gameId;
    private UUID playerId;
}
