package com.setcardgameserver.model;

import lombok.Getter;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Entity
@Table
public class Scoreboard {
    @Id
    @SequenceGenerator(
            name = "scoreboard_sequence",
            sequenceName = "scoreboard_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "scoreboard_sequence"
    )
    private Long scoreId;
    private UUID playerId;
    private String difficulty;
    private int score;
    private int time;

    public Scoreboard() {
    }

    public Scoreboard(Long scoreId, UUID playerId, String difficulty, int score, int time) {
        this.scoreId = scoreId;
        this.playerId = playerId;
        this.difficulty = difficulty;
        this.score = score;
        this.time = time;
    }

    public Scoreboard(String playerId, String difficulty, String score, String time) {
        this.playerId = UUID.fromString(playerId);
        this.difficulty = difficulty;
        this.score = Integer.parseInt(score);
        this.time = Integer.parseInt(time);
    }

    public Scoreboard(UUID playerId, String difficulty, int score, int time) {
        this.playerId = playerId;
        this.difficulty = difficulty;
        this.score = score;
        this.time = time;
    }

    @Override
    public String toString() {
        return "Scoreboard{" +
                "playerId=" + playerId +
                ", difficulty='" + difficulty + '\'' +
                ", score=" + score +
                ", time=" + time +
                '}';
    }
}
