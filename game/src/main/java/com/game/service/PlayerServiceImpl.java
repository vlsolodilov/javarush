package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.PlayerJson;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.function.Predicate;

@Service
@Transactional
public class PlayerServiceImpl implements PlayerService {
    private PlayerRepository playerRepository;
    @Autowired
    public void setPlayerRepository(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public PlayerJson create(String name, String title, Race race, Profession profession, Long birthday, Boolean banned, Integer experience) {

       if (name == null
                || title == null
                || race == null
                || profession == null
                || birthday == null
                || experience == null)
            return null;
        if (banned == null)
            banned = false;
        Date date = new Date(birthday);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        if (name.equals("")
                || name.length() > 12
                || title.length() > 30
                || experience < 0
                || experience > 10_000_000
                || year < 2000
                || year > 3000)
            return null;

        Player newPlayer = new Player(name,
                title,
                race,
                profession,
                experience,
                getLevelByExperience(experience),
                getUntilNextLevelByExperience(experience),
                date,
                banned);
        playerRepository.save(newPlayer);
        return convertToJson(newPlayer);
    }

    @Override
    public PlayerJson read(long id) {
        Player player = playerRepository.findById(id).orElse(null);
        return convertToJson(player);
    }

    @Override
    public List<Player> readAll() {
        return playerRepository.findAll();
    }

    @Override
    public PlayerJson update(long id, String name, String title, Race race, Profession profession, Long birthday, Boolean banned, Integer experience) {
        Player updatedPlayer = playerRepository.findById(id).orElse(null);
        if (updatedPlayer == null)
            return null;
        if (name != null)
            updatedPlayer.setName(name);
        if (title != null)
            updatedPlayer.setTitle(title);
        if (race != null)
            updatedPlayer.setRace(race);
        if (profession != null)
            updatedPlayer.setProfession(profession);
        if (experience != null) {
            updatedPlayer.setExperience(experience);
            updatedPlayer.setLevel(getLevelByExperience(experience));
            updatedPlayer.setUntilNextLevel(getUntilNextLevelByExperience(experience));
        }
        if (birthday != null)
            updatedPlayer.setBirthday(new Date(birthday));
        if (banned != null)
            updatedPlayer.setBanned(banned);

        playerRepository.save(updatedPlayer);
        return convertToJson(updatedPlayer);

    }

    @Override
    public boolean delete(long id) {
        if (playerRepository.existsById(id)){
            playerRepository.deleteById(id);
            return true;
        } else
            return false;
    }

    @Override
    public Set<Player> findBy(String name,
                               String title,
                               Race race,
                               Profession profession,
                               Long after,
                               Long before,
                               Boolean banned,
                               Integer minExperience,
                               Integer maxExperience,
                               Integer minLevel,
                               Integer maxLevel) {


        Set<Player> set = new HashSet<>(playerRepository.findAll());
        if (name != null) {
            set.retainAll(playerRepository.findByNameContaining(name));
        }
        if (title != null) {
            set.retainAll(playerRepository.findByTitleContaining(title));
        }
        if (race != null) {
            set.retainAll(playerRepository.findByRace(race));
        }
        if (profession != null) {
            set.retainAll(playerRepository.findByProfession(profession));
        }
        if (after != null) {
            set.retainAll(playerRepository.findByBirthdayAfter(new Date(after)));
        }
        if (before != null) {
            set.retainAll(playerRepository.findByBirthdayBefore(new Date(before)));
        }
        if (banned != null) {
            set.retainAll(playerRepository.findByBanned(banned));
        }
        if (minExperience != null) {
            set.retainAll(playerRepository.findByExperienceGreaterThanEqual(minExperience));
        }
        if (maxExperience != null) {
            set.retainAll(playerRepository.findByExperienceLessThanEqual(maxExperience));
        }
        if (minLevel != null) {
            set.retainAll(playerRepository.findByLevelGreaterThanEqual(minLevel));
        }
        if (maxLevel != null) {
            set.retainAll(playerRepository.findByLevelLessThanEqual(maxLevel));
        }

        return set;
    }

    @Override
    public List<PlayerJson> sort(Set<Player> set, PlayerOrder order, Integer pageNumber, Integer pageSize) {
        List<Player> list = new ArrayList<>(set);
        switch (order) {
            case ID:
                Collections.sort(list, new Comparator<Player>() {
                    public int compare(Player o1, Player o2) {
                        return o1.getId().compareTo(o2.getId());
                    }
                });
                break;
            case NAME:
                Collections.sort(list, new Comparator<Player>() {
                    public int compare(Player o1, Player o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                break;
            case EXPERIENCE:
                Collections.sort(list, new Comparator<Player>() {
                    public int compare(Player o1, Player o2) {
                        return o1.getExperience().compareTo(o2.getExperience());
                    }
                });
                break;
            case BIRTHDAY:
                Collections.sort(list, new Comparator<Player>() {
                    public int compare(Player o1, Player o2) {
                        return o1.getBirthday().compareTo(o2.getBirthday());
                    }
                });
                break;
        }
        List<PlayerJson> result = new ArrayList<>();
        for (int i = 0; i < pageSize; i++) {
            int index = pageNumber * pageSize + i;
            if (index < list.size())
                result.add(convertToJson(list.get(index)));
        }
        return result;
    }

    private int getLevelByExperience(int experience) {
        return (int) ((Math.sqrt(2500 + 200 * experience) - 50) / 100);
    }

    private int getUntilNextLevelByExperience(int experience) {
        return 50 * (getLevelByExperience(experience) + 1) * (getLevelByExperience(experience) + 2) - experience;
    }

    private PlayerJson convertToJson(Player player) {
        if (player == null)
            return null;
        PlayerJson playerJson = new PlayerJson();
        playerJson.setId(player.getId());
        playerJson.setName(player.getName());
        playerJson.setTitle(player.getTitle());
        playerJson.setRace(player.getRace());
        playerJson.setProfession(player.getProfession());
        playerJson.setBirthday(player.getBirthday().getTime());
        playerJson.setBanned(player.getBanned());
        playerJson.setExperience(player.getExperience());
        playerJson.setLevel(player.getLevel());
        playerJson.setUntilNextLevel(player.getUntilNextLevel());

        return playerJson;
    }


}
