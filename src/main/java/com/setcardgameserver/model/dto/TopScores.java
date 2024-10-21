package com.setcardgameserver.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class TopScores {
    private List<ScoreboardWithUserScoreDto> easyScores;
    private List<ScoreboardWithUserScoreDto> normalScores;
}
