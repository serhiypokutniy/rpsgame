package com.spsgame;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.spsgame.model.Game;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin("${cross.origin}")
@Slf4j
@Tag(name = "GameController", description = "Game controller description")
public class GameController {

    private static final String HYSTRIX_GROUP = "RPS_GAME";
    private final GameService gameService;

    public GameController(GameService gameService){
        this.gameService = gameService;
    }

    @GetMapping(path ="init", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.OK)
    @HystrixCommand(groupKey = HYSTRIX_GROUP)
    @Timed(value = "init.time", description = "Time for the initialization")
    @Operation(summary = "Initiates the app")
    public Game init(@RequestParam String playerId){
        log.debug("Executing init function");
        return gameService.init(playerId);
    }

    @PostMapping(path = "play", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.OK)
    @HystrixCommand(groupKey = HYSTRIX_GROUP)
    @Timed(value = "play.time", description = "Time required to complete the game")
    @Operation(summary = "Play the game method")
    public Game play(@RequestParam String playerId, @RequestParam String playerChoice) {
        log.debug("Executing play function for player {} with choice {}", playerId, playerChoice);
        return gameService.play(playerId, GameChoice.valueOf(playerChoice));
    }

    @PostMapping(path = "reset", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(code = HttpStatus.OK)
    @Timed(value = "reset.time", description = "Time required for resetting the score")
    @HystrixCommand(groupKey = HYSTRIX_GROUP)
    @Operation(summary = "Method for resetting the score")
    public void reset(@RequestParam String playerId) {
        log.debug("Executing reset function for {}", playerId);
        gameService.reset(playerId);
    }
}
