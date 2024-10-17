package com.setcardgameserver.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class TopScores {
    private List<ScoreboardDto> easyScores;
    private List<ScoreboardDto> normalScores;
}
