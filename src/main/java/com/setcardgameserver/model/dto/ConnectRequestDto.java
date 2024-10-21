package com.setcardgameserver.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ConnectRequestDto {
    private int gameId;
    private UUID playerId;
}
