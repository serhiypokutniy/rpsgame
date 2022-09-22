package com.spsgame;

import com.spsgame.entity.Game;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SpsGameApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private GameController controller;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
        Assertions.assertThat(controller).isNotNull();
    }

    @Test
    public void testEnumLengths() {
        Assert.isTrue(Game.Choice.values().length == 3, "Game choice must not be greater than 3");
        Assert.isTrue(Game.Winner.values().length == 3, "Winner enum must not contain more than 3 elements");
    }

    @Test
    public void testWhoWinsStaticMethod() {
        Assert.isTrue(Game.whoWins(Game.Choice.ROCK, Game.Choice.ROCK) == Game.Winner.TIE, "Same choice should lead to a tie");
        Assert.isTrue(Game.whoWins(Game.Choice.ROCK, Game.Choice.PAPER) == Game.Winner.PLAYER, "Rock must win over paper");
        Assert.isTrue(Game.whoWins(Game.Choice.SCISSORS, Game.Choice.PAPER) == Game.Winner.COMPUTER, "Scissors must win over paper");
    }

    @Test
    public void testInitMethod() {
        String result = this.restTemplate.getForObject("http://localhost:" + port + "/api/init?playerId=123456789", String.class);
        Assertions.assertThat(result).isNotNull();
        Assert.isTrue(result.contains("gameId"), "Result must contain string gameId: " + result);
    }

    @Test
    public void testResetMethod() {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("playerId", "123456789");
        ResponseEntity<?> responseCode = this.restTemplate.postForEntity("http://localhost:" + port + "/api/reset", map, String.class);
        Assertions.assertThat(responseCode).isNotNull();
        Assert.isTrue(responseCode.getStatusCode() == HttpStatus.OK, "Response code must be 200");
    }

    @Test
    public void testPlayMethod() {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("playerId", "123456789");
        map.add("playerChoice", Game.Choice.PAPER.name());
        ResponseEntity<?> responseCode = this.restTemplate.postForEntity("http://localhost:" + port + "/api/play", map, String.class);
        Assertions.assertThat(responseCode).isNotNull();
        Assertions.assertThat(responseCode.getBody()).isNotNull();
        Assert.isTrue(responseCode.getBody().toString().contains("winner"), "Response must contain string winner");
    }

    public static void main(String... args){
        executeLoadTest();
    }

    private static void executeLoadTest() {
        var client = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder(URI.create("http://localhost:8080/api/init?playerId=3242332"))
                .header("accept", "application/json")
                .GET()
                .build();
        ExecutorService executor = Executors.newFixedThreadPool(20);
        for (int i = 0; i < 100_000; i++) {
            executor.execute(() -> {
                try{
                    client.send(request, HttpResponse.BodyHandlers.ofString());
                    Thread.sleep(5);
                } catch(Exception ex){
                    System.out.println("Error executing test: " + ex.getMessage());// ignore sysout for standalone test
                }
            });
        }
        executor.shutdown();
    }
}
