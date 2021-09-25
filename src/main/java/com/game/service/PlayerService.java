package com.game.service;

import com.game.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public interface PlayerService {

    Page<Player> getPlayers(PlayerSearchCriteria playerSearchCriteria, PlayerPage playerPage);

    Integer count(PlayerSearchCriteria playerSearchCriteria);

    Optional<Player> getPlayer(Long id);

    Player createPlayer(String name, String title, Race race, Profession profession, Date birthday, Boolean banned, Integer experience);

    Optional<Player> updatePlayer(Long id, String name, String title, Race race, Profession profession, Date birthday, Boolean banned, Integer experience);

    boolean deletePlayer(Long id);
}
