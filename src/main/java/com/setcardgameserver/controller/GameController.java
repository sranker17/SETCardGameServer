package com.setcardgameserver.controller;

import com.setcardgameserver.model.dto.*;
import com.setcardgameserver.exception.InvalidGameException;
import com.setcardgameserver.exception.NotFoundException;
import com.setcardgameserver.mapper.GameMapper;
import com.setcardgameserver.service.GameService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Controller
@AllArgsConstructor
@Slf4j
public class GameController {
    private final GameService gameService;
    private final GameMapper gameMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private static final String TOPIC_WAITING = "/topic/waiting";
    private static final String TOPIC_GAME_PROGRESS = "/topic/game-progress/";
    private static final String TOPIC_DESTROYED = "/topic/destroyed/";

    @MessageMapping("/create")
    public GameDto create(@RequestBody UsernameDto usernameDto) {
        log.debug("create private game request: {}", usernameDto.getUsername());

        GameDto gameDto = null;
        try {
            gameDto = gameMapper.entityToDto(gameService.createGame(UUID.fromString(usernameDto.getUsername())));
            simpMessagingTemplate.convertAndSend(TOPIC_WAITING, gameDto);
        } catch (NotFoundException e) {
            log.error(e.getMessage());
        }
        return gameDto;
    }

    @MessageMapping("/connect")
    public GameDto connect(@RequestBody ConnectRequestDto request) {
        log.debug("connect to private game request: {} {}", request.getGameId(), request.getPlayerId());

        GameDto gameDto = gameMapper.entityToDto(gameService.connectToGame(request.getPlayerId(), request.getGameId()));
        simpMessagingTemplate.convertAndSend(TOPIC_WAITING, gameDto);
        return gameDto;
    }

    @MessageMapping("/connect/random")
    public GameDto connectRandom(@RequestBody UsernameDto usernameDto) {
        log.debug("connect random {}", usernameDto.getUsername());

        GameDto gameDto = null;
        try {
            gameDto = gameMapper.entityToDto(gameService.connectToRandomGame(UUID.fromString(usernameDto.getUsername())));
            simpMessagingTemplate.convertAndSend(TOPIC_WAITING, gameDto);
        } catch (NotFoundException e) {
            log.error(e.getMessage());
        }

        return gameDto;
    }

    @MessageMapping("/start")
    public GameDto startGame(@RequestBody GameIdDto gameIdDto) {
        log.debug("game started: {}", gameIdDto.getGameId());

        GameDto gameDto = null;
        try {
            gameDto = gameMapper.entityToDto(gameService.getGameById(gameIdDto.getGameId()));
            simpMessagingTemplate.convertAndSend(TOPIC_GAME_PROGRESS + gameIdDto.getGameId(), gameDto);
        } catch (NotFoundException e) {
            log.error(e.getMessage());
        }

        return gameDto;
    }

    @MessageMapping("/gameplay")
    public GameDto gameplay(@RequestBody GameplayDto gameplayDto) {
        log.debug("gameplay: {} {}", gameplayDto.getGameId(), gameplayDto.getPlayerId());

        GameDto gameDto = null;
        try {
            gameDto = gameMapper.entityToDto(gameService.gameplay(gameplayDto));
            simpMessagingTemplate.convertAndSend(TOPIC_GAME_PROGRESS + gameDto.getGameId(), gameDto);
        } catch (InvalidGameException | NotFoundException e) {
            log.error(e.getMessage());
        }
        return gameDto;
    }

    @MessageMapping("/gameplay/button")
    public GameDto buttonPress(@RequestBody GameplayButtonPress buttonPress) {
        log.debug("buttonPress: {} {}", buttonPress.getGameId(), buttonPress.getPlayerId());

        GameDto gameDto = null;
        try {
            gameDto = gameMapper.entityToDto(gameService.buttonPress(buttonPress));
            simpMessagingTemplate.convertAndSend(TOPIC_GAME_PROGRESS + gameDto.getGameId(), gameDto);
        } catch (InvalidGameException e) {
            log.error(e.getMessage());
        } catch (NotFoundException e) {
            log.error(e.getMessage());
        }
        return gameDto;
    }

    @MessageMapping("/game/destroy")
    public String destroyGame(@RequestBody GameIdDto gameIdDto) {
        log.debug("destroy game {}", gameIdDto.getGameId());

        try {
            gameService.removeGame(gameIdDto.getGameId());
        } catch (NotFoundException e) {
            log.error(e.getMessage());
        }
        simpMessagingTemplate.convertAndSend(TOPIC_DESTROYED + gameIdDto.getGameId(), "Done");
        return "Done";
    }

    @MessageMapping("/game/destroy/all")
    public void destroyAllGames(@RequestBody UsernameDto usernameDto) {
        log.debug("destroy all games by {}", usernameDto.getUsername());

        gameService.destroyAllGames();
    }
}
