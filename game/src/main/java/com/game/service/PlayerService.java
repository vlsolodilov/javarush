package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.PlayerJson;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface PlayerService {
    PlayerJson create(String name, String title, Race race, Profession profession, Long birthday, Boolean banned, Integer experience);
    PlayerJson read(long id);
    List<Player> readAll();
    PlayerJson update(long id, String name, String title, Race race, Profession profession, Long birthday, Boolean banned, Integer experience);
    boolean delete(long id);

    Set<Player> findBy(String name,
                       String title,
                       Race race,
                       Profession profession,
                       Long after,
                       Long before,
                       Boolean banned,
                       Integer minExperience,
                       Integer maxExperience,
                       Integer minLevel,
                       Integer maxLevel);

    List<PlayerJson> sort(Set<Player> set, PlayerOrder order, Integer pageNumber, Integer pageSize);
}
