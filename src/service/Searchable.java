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
