package com.setcardgameserver.timer;

import com.setcardgameserver.storage.GameStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Slf4j
public class DeleteGamesTimer {
    @Scheduled(cron = "0 0 1 * * ?", zone = "CET")
    public void deleteGames() {
        GameStorage.getInstance().removeAllGames();
        log.info("Deleted all the stuck games.");
    }
}