package com.setcardgameserver.mapper;

import com.setcardgameserver.dto.GameDto;
import com.setcardgameserver.model.Game;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GameMapper {
    Game dtoToEntity(GameDto gameDto);

    GameDto entityToDto(Game game);
}
