package com.setcardgameserver.timer;

import com.setcardgameserver.service.RedisGameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class DeleteGamesTimer {
    
    private final RedisGameService redisGameService;
    
    @Scheduled(cron = "0 0 1 * * ?", zone = "CET")
    public void deleteGames() {
        redisGameService.removeAllGames();
        log.info("Deleted all the stuck games from Redis.");
    }
}