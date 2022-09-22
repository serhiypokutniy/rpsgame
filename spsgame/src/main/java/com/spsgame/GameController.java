package com.spsgame;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.spsgame.entity.Game;
import com.spsgame.entity.Player;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api")
@CrossOrigin("http://localhost:4200/")
@Slf4j
public class GameController {

    private static final String HYSTRIX_GROUP = "RPS_GAME";
    private static final long TOTAL_GAMES_CACHING_ID = 100000001L;
    private static final long DISTINCT_PLAYERS_CACHING_ID = 100000002L;

    @Autowired
    private RedisTemplate<Long, Object> redisTemplate;

    @Autowired
    private PlayerRepository playerRepository;

    @GetMapping(path ="init", produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(groupKey = HYSTRIX_GROUP)
    @Timed(value = "init.time", description = "Time for the initialization")
    public Game initGame(@RequestParam String playerId){
        return execute(() -> {
            log.debug("Executing init function for {}", playerId);
            Player player = findOrCreatePlayer(playerId);
            return Game.from(player);
        });
    }

    @PostMapping(path = "play", produces = MediaType.APPLICATION_JSON_VALUE)
    @HystrixCommand(groupKey = HYSTRIX_GROUP)
    @Timed(value = "play.time", description = "Time required to complete the game")
    public Game play(@RequestParam String playerId, @RequestParam String playerChoice) {
        return execute(() -> {
            log.debug("Executing play function for player {} with choice {}", playerId, playerChoice);
            Player player = findOrCreatePlayer(playerId);
            Game game = Game.from(playerChoice, player);
            playerRepository.save(player);
            return game;
        });
    }

    @PostMapping(path = "reset", produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed(value = "reset.time", description = "Time required for resetting the score")
    @HystrixCommand(groupKey = HYSTRIX_GROUP)
    public void reset(@RequestParam String playerId) {
        execute(() -> {
            log.debug("Executing reset function for {}", playerId);
            Player player = findOrCreatePlayer(playerId);
            player.setLastLostScore(0);
            player.setLastWonScore(0);
            playerRepository.save(player);
            return null;
        });
    }

    /** Loads computationally intensive values to the cache. These values can later be read by the application. */
    @Scheduled(initialDelay = 0, fixedDelay = 10_000)
    public void fetchStatistics() {
        try {
            log.debug("Loading values to the cache");
            redisTemplate.opsForValue().set(TOTAL_GAMES_CACHING_ID, playerRepository.totalGames());
            redisTemplate.opsForValue().set(DISTINCT_PLAYERS_CACHING_ID, playerRepository.distinctPlayers());
        } catch(Exception ex){
            log.error("Error fetching statistics caused by {}, the scheduling will be stopped", ex.getMessage(), ex);
            throw new IllegalStateException(ex); // stop automatic execution
        }
    }

    @Bean
    public RedisTemplate<Long, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<Long, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    /** Encapsulates the code inside of try and catch block */
    private<T> T execute(Supplier<T> supplier){
        try {
            return supplier.get();
        } catch (Exception exc) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error processing request caused by " + exc.getMessage(), exc);
        }
    }

    /** @return Find an existing player using the provided id or creates a new one */
    private Player findOrCreatePlayer(String playerId){
        if(playerId != null && !playerId.isEmpty()){
            Player player = playerRepository.findById(playerId)
                    .orElseGet(() -> {
                        log.debug("User {} does not exist, creating a new user", playerId);
                        return playerRepository.save(Player.builder().playerId(playerId).lastUpdated(Instant.now()).build());
                    });
            //Set computationally intensive values from cache
            player.setPlayedByAll((Integer) redisTemplate.opsForValue().get(TOTAL_GAMES_CACHING_ID));
            player.setDistinctPlayers((Integer) redisTemplate.opsForValue().get(DISTINCT_PLAYERS_CACHING_ID));
            return player;
        }
        throw new IllegalArgumentException("No session id provided for using as user id");
    }
}
