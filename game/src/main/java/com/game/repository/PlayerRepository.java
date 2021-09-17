package com.game.repository;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByNameContaining(String name);
    List<Player> findByTitleContaining(String title);
    List<Player> findByRace(Race race);
    List<Player> findByProfession(Profession profession);
    List<Player> findByBirthdayAfter(Date birthday);
    List<Player> findByBirthdayBefore(Date birthday);
    List<Player> findByBanned(Boolean banned);
    List<Player> findByExperienceLessThanEqual (Integer experience);
    List<Player> findByExperienceGreaterThanEqual (Integer experience);
    List<Player> findByLevelLessThanEqual (Integer level);
    List<Player> findByLevelGreaterThanEqual (Integer level);

}
