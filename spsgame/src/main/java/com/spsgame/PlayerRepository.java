package com.spsgame;

import com.spsgame.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PlayerRepository extends JpaRepository<Player, String> {

    @Query(value = "SELECT SUM(p.timesPlayed) FROM Player p")
    Integer sumTimesPlayed();
}


