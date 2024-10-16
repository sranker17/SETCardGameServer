package com.setcardgameserver.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
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
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "UserID", nullable = false)
    private UUID userId;

    @Column(name = "Difficulty", nullable = false)
    private String difficulty;

    @Column(name = "Score", nullable = false)
    private int score;

    @Column(name = "Time", nullable = false)
    private int time;

    @CreationTimestamp
    @Column(updatable = false, name = "CreatedAt")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "UpdatedAt")
    private Date updatedAt;
}