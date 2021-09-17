package com.game.controller;

import com.game.entity.Player;
import com.game.entity.PlayerJson;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/rest")
public class PlayerController {
    private PlayerService playerService;

    @Autowired
    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping(value = "/players")
    public ResponseEntity<List<PlayerJson>> getPlayerList(@RequestParam(required = false) String name,
                                                @RequestParam(required = false) String title,
                                                @RequestParam(required = false) Race race,
                                                @RequestParam(required = false) Profession profession,
                                                @RequestParam(required = false) Boolean banned,
                                                @RequestParam(required = false) Long after,
                                                @RequestParam(required = false) Long before,
                                                @RequestParam(required = false) Integer minExperience,
                                                @RequestParam(required = false) Integer maxExperience,
                                                @RequestParam(required = false) Integer minLevel,
                                                @RequestParam(required = false) Integer maxLevel,
                                                @RequestParam(defaultValue = "ID") PlayerOrder order,
                                                @RequestParam(defaultValue = "0") Integer pageNumber,
                                                @RequestParam(defaultValue = "3") Integer pageSize) {

        Set<Player> set = playerService.findBy(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel);
        List<PlayerJson> players = playerService.sort(set, order, pageNumber, pageSize);
        return new ResponseEntity<>(players, HttpStatus.OK);

    }

    @GetMapping(value = "players/count")
    public ResponseEntity<Integer> getPlayerCount(@RequestParam(required = false) String name,
                                         @RequestParam(required = false) String title,
                                         @RequestParam(required = false) Race race,
                                         @RequestParam(required = false) Profession profession,
                                         @RequestParam(required = false) Long after,
                                         @RequestParam(required = false) Long before,
                                         @RequestParam(required = false) Boolean banned,
                                         @RequestParam(required = false) Integer minExperience,
                                         @RequestParam(required = false) Integer maxExperience,
                                         @RequestParam(required = false) Integer minLevel,
                                         @RequestParam(required = false) Integer maxLevel) {
        Set<Player> players = playerService.findBy(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel);
        final Integer count = players.size();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @PostMapping(value = "/players")
    public ResponseEntity<PlayerJson> createPlayer(@RequestBody PlayerJson playerJson) {

        final PlayerJson player  = playerService.create(playerJson.getName(), playerJson.getTitle(), playerJson.getRace(), playerJson.getProfession(), playerJson.getBirthday(), playerJson.getBanned(), playerJson.getExperience());
        return player != null
                ? new ResponseEntity<>(player, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/players/{id}")
    public ResponseEntity<PlayerJson> getPlayer(@PathVariable(name = "id") long id) {
        if (id <= 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        final PlayerJson player = playerService.read(id);

        return player != null
                ? new ResponseEntity<>(player, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/players/{id}")
    public ResponseEntity<PlayerJson> updatePlayer(@PathVariable(name = "id") long id, @RequestBody PlayerJson playerJson) {
        if (playerJson.getExperience() != null && (playerJson.getExperience() < 0 || playerJson.getExperience() > 10_000_000)
               || playerJson.getBirthday() != null && playerJson.getBirthday() < 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (id <= 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        final PlayerJson player = playerService.update(id, playerJson.getName(), playerJson.getTitle(), playerJson.getRace(), playerJson.getProfession(), playerJson.getBirthday(), playerJson.getBanned(), playerJson.getExperience());
        return player != null
                ? new ResponseEntity<>(player, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/players/{id}")
    public ResponseEntity<?> deletePlayer(@PathVariable(name = "id") long id) {
        if (id <= 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        final boolean deleted = playerService.delete(id);

        return deleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
