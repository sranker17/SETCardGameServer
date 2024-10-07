package com.setcardgameserver.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Scoreboard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCORE_ID")
    private Long scoreId;
    @Column(name = "PLAYER_ID")
    private UUID playerId;
    @Column(name = "DIFFICULTY")
    private String difficulty;
    @Column(name = "SCORE")
    private int score;
    @Column(name = "TIME")
    private int time;
}