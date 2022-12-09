package com.setcardgameserver.Timer;

import com.setcardgameserver.Storage.GameStorage;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class DeleteGamesTimer {

    @Scheduled(cron="0 0 1 * * ?", zone="CET")
    public void deleteGames(){
        GameStorage.getInstance().removeAllGames();
        System.out.println("Deleted all the stuck games.");
    }
}
