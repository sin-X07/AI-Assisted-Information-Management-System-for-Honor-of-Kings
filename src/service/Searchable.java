package service;

import model.Equipment;
import model.Hero;
import model.Team;
import model.Player;

import java.util.List;

public interface Searchable {
    List<Hero> getHeroes();

    List<Equipment> getEquipments();

    List<Team> getTeams();

    List<Player> getPlayers();

    /** 模糊搜索英雄 (匹配编号、名称、称号、定位、类型、难度) */
    List<Hero> searchHeroes(String keyword);

    /** 模糊搜索装备 (匹配编号、名称、类型) */
    List<Equipment> searchEquipments(String keyword);

    /** 模糊搜索战队 (匹配编号、名称、简称、地区) */
    List<Team> searchTeams(String keyword);

    /** 模糊搜索玩家 (匹配ID、昵称) */
    List<Player> searchPlayers(String keyword);

    Hero findHeroById(String heroId);

    Equipment findEquipmentById(String equipmentId);

    Team findTeamById(String teamId);

    Hero findHeroByName(String heroName);

    Equipment findEquipmentByName(String equipmentName);

    Team findTeamByName(String teamName);

    void addHero(Hero hero);

    void addEquipment(Equipment equipment);

    void addTeam(Team team);

    boolean removeHeroById(String heroId);

    boolean removeEquipmentById(String equipmentId);

    boolean removeTeamById(String teamId);

    boolean updateHero(Hero hero);

    boolean updateEquipment(Equipment equipment);

    boolean updateTeam(Team team);

    Player findPlainPlayerById(String id);

    void displayPlayerDetails(String id);
}
