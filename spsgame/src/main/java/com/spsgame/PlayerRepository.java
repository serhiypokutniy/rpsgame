package com.spsgame;

import com.spsgame.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    @Query(value = "SELECT p FROM Player p where p.playerId = ?1")
    Optional<Player> findById(String playerId);

    @Query(value = "SELECT COUNT(DISTINCT p.playerId) FROM Player p")
    int distinctPlayers();

    @Query(value = "SELECT SUM(p.timesPlayed) FROM Player p")
    Integer totalGames();
}
