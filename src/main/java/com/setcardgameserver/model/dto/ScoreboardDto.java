package com.setcardgameserver.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ScoreboardDto {
    @NotBlank(message = "The username is required")
    private String username;
    @NotBlank(message = "The difficulty is required")
    private String difficulty;
    @NotBlank(message = "The score is required")
    private int score;
    @NotBlank(message = "The time is required")
    private int time;
}
