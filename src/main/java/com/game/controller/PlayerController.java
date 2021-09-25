package com.game.controller;

import com.game.entity.*;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("rest/players")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping
    public ResponseEntity<List<Player>> getAllPlayers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Race race,
            @RequestParam(required = false) Profession profession,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Boolean banned,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Integer minLevel,
            @RequestParam(required = false) Integer maxLevel,
            @RequestParam(required = false) PlayerOrder order,
            @RequestParam(required = false) Integer pageNumber,
            @RequestParam(required = false) Integer pageSize

    ) {
        order = isNull(order)? PlayerOrder.ID : order;
        pageNumber = isNull(pageNumber) ? 0 : pageNumber;
        pageSize = isNull(pageSize) ? 3 : pageSize;
        PlayerSearchCriteria playerSearchCriteria = new PlayerSearchCriteria(
                name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel
        );

        PlayerPage playerPage = new PlayerPage(pageNumber, pageSize, order.getFieldName());
        Page<Player> players = playerService.getPlayers(playerSearchCriteria, playerPage);
        return new ResponseEntity<>(players.getContent(), HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> count(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Race race,
            @RequestParam(required = false) Profession profession,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Boolean banned,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) Integer minLevel,
            @RequestParam(required = false) Integer maxLevel
    ) {
        PlayerSearchCriteria playerSearchCriteria = new PlayerSearchCriteria(
                name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel
        );
        return new ResponseEntity<>(playerService.count(playerSearchCriteria), HttpStatus.OK);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable Long id){
        if (isNotValidId(id)) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        Optional<Player> player = playerService.getPlayer(id);
        return player.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {

        String name = player.getName();
        String title = player.getTitle();
        Race race = player.getRace();
        Profession profession = player.getProfession();
        Boolean banned = player.getBanned();
        Date birthday = player.getBirthday();
        Integer experience = player.getExperience();
        if (isNull(name) || isNull(title) || isNull(race) || isNull(profession) || isNull(birthday) || isNull(experience))
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        if (isNotValidExperience(experience) || isNotValidTitle(title) || isNotValidName(name) || isNotValidBirthday(birthday))
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        player = playerService.createPlayer(name, title, race, profession, birthday, banned, experience);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    @PostMapping(path = "{id}")
    public ResponseEntity<Player> updatePlayer(
            @PathVariable Long id,
            @RequestBody Player player
    ) {
        String name = player.getName();
        String title = player.getTitle();
        Race race = player.getRace();
        Profession profession = player.getProfession();
        Date birthday = player.getBirthday();
        Boolean banned = player.getBanned();
        Integer experience = player.getExperience();
        if (isNotValidId(id)) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        if (name != null && isNotValidName(name)) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        if (title != null && isNotValidTitle(title)) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        if (birthday != null && isNotValidBirthday(birthday)) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        if (experience != null && isNotValidExperience(experience)) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        Optional<Player> player1 = playerService.updatePlayer(id, name, title, race, profession, birthday, banned, experience);
        return player1.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity deletePlayer(@PathVariable Long id){
        if (isNotValidId(id)) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        if (!playerService.deletePlayer(id)) return new ResponseEntity(HttpStatus.NOT_FOUND);
        return new ResponseEntity(HttpStatus.OK);
    }

    private static boolean isNotValidId(Long id) {
        return id == null || id <= 0;
    }

    private static boolean isNotValidName(String name){
        return name.isEmpty() || name.length() > 12;
    }

    private static boolean isNotValidTitle(String title){
        return title.length() > 30;
    }

    private static boolean isNotValidBirthday(Date birthday){
        Calendar calendar2000 = new GregorianCalendar(2000, Calendar.JANUARY, 0);
        Calendar calendar3000 = new GregorianCalendar(3001, Calendar.JANUARY, 0);
        return birthday.before(calendar2000.getTime()) || birthday.after(calendar3000.getTime());
    }

    private static boolean isNotValidExperience(Integer experience){
        return experience < 0 || experience > 10_000_000;
    }

    private static boolean isNull(Object o) {
        return Objects.isNull(o);
    }
}
