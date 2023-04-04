package com.spsgame;


import com.spsgame.model.Game;
import com.spsgame.entity.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
public class GameService {
    private static final Random RANDOM = new SecureRandom();
    static final long TOTAL_GAMES_CACHING_ID = 100000001L;
    static final long DISTINCT_PLAYERS_CACHING_ID = 100000002L;
    private final PlayerRepository playerRepository;
    private final RedisTemplate<Long, Object> redisTemplate;

    public GameService(PlayerRepository playerRepository, RedisTemplate<Long, Object> redisTemplate){
        this.playerRepository = playerRepository;
        this.redisTemplate = redisTemplate;
    }

    /** Loads computationally intensive values to the cache. These values can later be read by the application. */
    @Scheduled(initialDelay = 0, fixedDelay = 10_000)
    public void fetchStatistics() {
        try {
            log.debug("Loading values to the cache");
            redisTemplate.opsForValue().set(GameService.TOTAL_GAMES_CACHING_ID, playerRepository.sumTimesPlayed());
            redisTemplate.opsForValue().set(GameService.DISTINCT_PLAYERS_CACHING_ID, playerRepository.count());
        } catch(Exception ex){
            log.error("Error fetching statistics caused by {}, the scheduling will be stopped", ex.getMessage(), ex);
            throw new IllegalStateException(ex); // stop automatic execution
        }
    }

    public Game init(String playerId){
        Optional<Player> player = playerRepository.findById(playerId);
        return Game.builder()
                .distinctPlayers(getDistinctPlayers())
                .timesPlayed(player.map(Player::getTimesPlayed).orElse(0))
                .lastWonScore(player.map(Player::getLastLostScore).orElse(0))
                .lastWonScore(player.map(Player::getLastWonScore).orElse(0))
                .playedByAll(getPlayedByAll())
                .build();
    }

    public void reset(String playerId){
        playerRepository.findById(playerId).ifPresent(player -> {
            player.setLastLostScore(0);
            player.setLastWonScore(0);
            player.setLastUpdated(Instant.now());
            playerRepository.save(player);
        });
    }

    public Game play(String playerId, GameChoice playerChoice){
        GameChoice pcChoice = randomChoice();
        Boolean playerWins = userWins(pcChoice, playerChoice);
        Player player = updatePlayerStatistics(playerId, playerWins);
        return Game.builder().playerWins(playerWins)
                .distinctPlayers(getDistinctPlayers())
                .timesPlayed(player.getTimesPlayed())
                .lastLostScore(player.getLastLostScore())
                .lastWonScore(player.getLastWonScore())
                .computerChoice(pcChoice.name())
                .playedByAll(getPlayedByAll() + 1) //increment the counter here as the cache is updated with delay
                .build();
    }

    private Player updatePlayerStatistics(String playerId, Boolean playerWins){
        Player player = playerRepository.findById(playerId).orElseGet(() -> Player.builder().playerId(playerId).build());
        player.setTimesPlayed(player.getTimesPlayed() + 1);
        if (playerWins == Boolean.FALSE) {
            player.setLastLostScore(player.getLastLostScore() + 1);
        } else if (playerWins == Boolean.TRUE) {
            player.setLastWonScore(player.getLastWonScore() + 1);
        }
        player.setLastUpdated(Instant.now());
        playerRepository.save(player);
        return player;
    }

    private Boolean userWins(GameChoice pcChoice, GameChoice user) {
        if(pcChoice == user) {
            return null;
        }
        return (user == GameChoice.ROCK && pcChoice == GameChoice.SCISSORS)
                || (user == GameChoice.PAPER && pcChoice == GameChoice.ROCK)
                || (user == GameChoice.SCISSORS && pcChoice == GameChoice.PAPER);
    }

    private int getPlayedByAll(){
        Integer playedByAll = (Integer) redisTemplate.opsForValue().get(TOTAL_GAMES_CACHING_ID);
        return playedByAll == null ? 0 : playedByAll;
    }

    private Long getDistinctPlayers(){
        return (Long) redisTemplate.opsForValue().get(DISTINCT_PLAYERS_CACHING_ID);
    }

    private static GameChoice randomChoice(){
        return GameChoice.values()[RANDOM.nextInt(GameChoice.values().length)];
    }
}
