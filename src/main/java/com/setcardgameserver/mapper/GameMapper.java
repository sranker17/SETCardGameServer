package com.setcardgameserver.mapper;

import com.setcardgameserver.model.dto.GameDto;
import com.setcardgameserver.model.redis.RedisGame;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GameMapper {
    GameDto redisGameToDto(RedisGame redisGame);
}
