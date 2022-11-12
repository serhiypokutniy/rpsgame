package com.spsgame.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Game {

    private Boolean playerWins;
    private String computerChoice;
    private int timesPlayed;
    private int lastWonScore;
    private int lastLostScore;
    private long distinctPlayers;
    private long playedByAll;
}