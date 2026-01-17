package com.setcardgameserver.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Scoreboard extends BaseEntity {
    @Column(name = "Username", nullable = false)
    private String username;

    @Column(name = "Difficulty", nullable = false)
    private String difficulty;

    @Column(name = "Score", nullable = false)
    private int score;

    @Column(name = "Time", nullable = false)
    private int time;
}