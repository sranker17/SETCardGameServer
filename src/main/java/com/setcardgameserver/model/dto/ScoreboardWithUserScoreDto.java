package com.setcardgameserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@AllArgsConstructor
public class ScoreboardWithUserScoreDto extends ScoreboardDto {
    private boolean userScore;
}
