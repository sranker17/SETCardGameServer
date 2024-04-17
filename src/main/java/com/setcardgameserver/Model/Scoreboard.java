package com.setcardgameserver.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Scoreboard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scoreId;
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID playerId;
    private String difficulty;
    private int score;
    private int time;

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