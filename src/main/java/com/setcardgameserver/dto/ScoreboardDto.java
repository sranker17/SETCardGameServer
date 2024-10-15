package com.setcardgameserver.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ScoreboardDto {
    private UUID playerId;
    private String difficulty;
    private int score;
    private int time;
}
