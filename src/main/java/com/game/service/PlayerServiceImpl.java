package com.game.service;

import com.game.entity.*;
import com.game.repository.PlayerCriteriaRepository;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerCriteriaRepository playerCriteriaRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository, PlayerCriteriaRepository playerCriteriaRepository) {
        this.playerRepository = playerRepository;
        this.playerCriteriaRepository = playerCriteriaRepository;
    }

    @Override
    public Page<Player> getPlayers(PlayerSearchCriteria playerSearchCriteria, PlayerPage playerPage) {
        return playerCriteriaRepository.findAllWithFilter(playerPage, playerSearchCriteria);
    }

    public Integer count (PlayerSearchCriteria playerSearchCriteria) {
        return (int) playerCriteriaRepository.findAllWithFilter(new PlayerPage(), playerSearchCriteria).getTotalElements();
    }

    @Override
    public Optional<Player> getPlayer(Long id) {
        return playerRepository.findById(id);
    }

    @Override
    public boolean deletePlayer(Long id) {
        boolean exists = playerRepository.existsById(id);
        if (!exists) {
            return false;
        }
        playerRepository.deleteById(id);
        return true;
    }

    @Override
    public Player createPlayer(String name, String title, Race race, Profession profession, Date birthday, Boolean banned, Integer experience) {
        Player player = new Player(name, title, race, profession, experience, birthday, banned);
        return playerRepository.save(player);
    }

    @Override
    @Transactional
    public Optional<Player> updatePlayer(Long id, String name, String title,
                                         Race race, Profession profession,
                                         Date birthday, Boolean banned, Integer experience) {
        Optional<Player> player = playerRepository.findById(id);
        if (!player.isPresent()) return player;
        if (name != null && !Objects.equals(player.get().getName(), name)) player.get().setName(name);
        if (title != null && !Objects.equals(player.get().getTitle(), title)) player.get().setTitle(title);
        if (race != null && !Objects.equals(player.get().getRace(), race)) player.get().setRace(race);
        if (profession != null && !Objects.equals(player.get().getProfession(), profession)) player.get().setProfession(profession);
        if (birthday != null && !Objects.equals(player.get().getBirthday(), birthday)) player.get().setBirthday(birthday);
        if (banned != null && !Objects.equals(player.get().getBanned(), banned)) player.get().setBanned(banned);
        if (experience != null && !Objects.equals(player.get().getExperience(), experience)) {
            player.get().setExperience(experience);
            player.get().setLevel(experience);
            player.get().setUntilNextLevel(experience);
        }
        return player;
    }
}
