package com.spsgame.entity;

import lombok.Getter;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;

@Getter
public class Game {
    private static final Random RANDOM = new SecureRandom();
    private final String gameId = UUID.randomUUID().toString();
    private final Choice computerChoice;
    private final Choice playerChoice;
    private final Winner winner;
    private final Player player;
    public enum Choice {
        ROCK, SCISSORS, PAPER
    }
    public enum Winner {
        PLAYER, COMPUTER, TIE
    }

    private Game(Choice playerChoice, Player player){
        this.playerChoice = playerChoice;
        if(playerChoice != null) {
            computerChoice = randomChoice();
            winner = whoWins(computerChoice, playerChoice);
            if (winner == Winner.COMPUTER) {
                player.setLastLostScore(player.getLastLostScore() + 1);
            } else if (winner == Winner.PLAYER) {
                player.setLastWonScore(player.getLastWonScore() + 1);
            }
            player.setTimesPlayed(player.getTimesPlayed() + 1);
            player.setPlayedByAll(player.getPlayedByAll() == null ? 1 : (player.getPlayedByAll()) + 1);
        } else {
            computerChoice = null;
            winner = null;
        }
        player.setLastUpdated(Instant.now());
        this.player = player;
    }

    private static Choice randomChoice(){
        return Choice.values()[RANDOM.nextInt(Choice.values().length)];
    }

    public static Game from(Player player){
        return new Game(null, player);
    }

    public static Game from(String playerChoice, Player player){
        return new Game(Choice.valueOf(playerChoice.toUpperCase()), player);
    }

    /** @return computes the winner based on his choice  */
    public static Winner whoWins(Choice pc, Choice user) {
        if(pc == user) {
            return Winner.TIE;
        }
        return (pc == Choice.ROCK && user == Choice.SCISSORS)
                    || (pc == Choice.PAPER && user == Choice.ROCK)
                    || (pc == Choice.SCISSORS && user == Choice.PAPER) ? Winner.COMPUTER : Winner.PLAYER;
    }
}