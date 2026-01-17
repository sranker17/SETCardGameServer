package com.setcardgameserver.model.dto;

import lombok.Data;

@Data
public class ConnectRequestDto {
    private int gameId;
    private String playerId;
}