package com.setcardgameserver.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ScoreboardDto {
    private String username;
    private String difficulty;
    private int score;
    private int time;
}
