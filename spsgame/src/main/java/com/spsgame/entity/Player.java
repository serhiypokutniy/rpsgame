package com.spsgame.entity;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "player")
public class Player {

    @Id
    @Column(name = "player_id", nullable = false)
    private String playerId;

    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    @Column(name = "times_played", nullable = false)
    private int timesPlayed;

    @Column(name = "last_won_score", nullable = false)
    private int lastWonScore;

    @Column(name = "last_lost_score", nullable = false)
    private int lastLostScore;

    @Transient
    private Integer distinctPlayers;

    @Transient
    private Integer playedByAll;
}