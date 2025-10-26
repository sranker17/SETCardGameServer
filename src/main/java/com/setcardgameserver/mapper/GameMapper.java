package com.setcardgameserver.mapper;

import com.setcardgameserver.model.Game;
import com.setcardgameserver.model.dto.GameDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GameMapper {
    GameDto entityToDto(Game game);
}
