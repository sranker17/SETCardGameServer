package com.setcardgameserver.mapper;

import com.setcardgameserver.model.Scoreboard;
import com.setcardgameserver.model.dto.ScoreboardDto;
import com.setcardgameserver.model.dto.ScoreboardWithUserScoreDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ScoreboardMapper {
    Scoreboard dtoToEntity(ScoreboardDto scoreboardDto);

    ScoreboardDto entityToDto(Scoreboard scoreboard);

    List<ScoreboardDto> entityListToDtoList(List<Scoreboard> scoreboardList);

    List<ScoreboardWithUserScoreDto> entityListToTopDtoList(List<Scoreboard> scoreboardList);
}
