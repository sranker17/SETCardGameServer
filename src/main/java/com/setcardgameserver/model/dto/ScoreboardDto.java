package com.setcardgameserver.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScoreboardDto {
    @NotBlank(message = "The username is required")
    private String username;
    @NotBlank(message = "The difficulty is required")
    private String difficulty;
    @NotNull(message = "The score is required")
    private int score;
    @NotNull(message = "The time is required")
    private int time;
    private boolean userScore;
}
