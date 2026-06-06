package service;

import model.Equipment;
import model.Hero;
import model.Team;
import util.DataInitializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameDataManager implements DataManageable {
    private final List<Hero> heroes;
    private final List<Equipment> equipments;
    private final List<Team> teams;

    public GameDataManager() {
        this.heroes = new ArrayList<>(DataInitializer.initializeHeroes());
        this.equipments = new ArrayList<>(DataInitializer.initializeEquipments());
        this.teams = new ArrayList<>(DataInitializer.initializeTeams());
    }

    @Override
    public List<Hero> getHeroes() {
        return Collections.unmodifiableList(new ArrayList<>(heroes));
    }

    @Override
    public List<Equipment> getEquipments() {
        return Collections.unmodifiableList(new ArrayList<>(equipments));
    }

    @Override
    public List<Team> getTeams() {
        return Collections.unmodifiableList(new ArrayList<>(teams));
    }

    @Override
    public Hero findHeroById(String heroId) {
        if (heroId == null) {
            return null;
        }
        for (Hero hero : heroes) {
            if (heroId.equals(hero.getHeroId())) {
                return hero;
            }
        }
        return null;
    }

    @Override
    public Equipment findEquipmentById(String equipmentId) {
        if (equipmentId == null) {
            return null;
        }
        for (Equipment equipment : equipments) {
            if (equipmentId.equals(equipment.getEquipmentId())) {
                return equipment;
            }
        }
        return null;
    }

    @Override
    public Team findTeamById(String teamId) {
        if (teamId == null) {
            return null;
        }
        for (Team team : teams) {
            if (teamId.equals(team.getTeamId())) {
                return team;
            }
        }
        return null;
    }

    @Override
    public Hero findHeroByName(String heroName) {
        if (heroName == null) {
            return null;
        }
        for (Hero hero : heroes) {
            if (heroName.equals(hero.getHeroName())) {
                return hero;
            }
        }
        return null;
    }

    @Override
    public Equipment findEquipmentByName(String equipmentName) {
        if (equipmentName == null) {
            return null;
        }
        for (Equipment equipment : equipments) {
            if (equipmentName.equals(equipment.getEquipmentName())) {
                return equipment;
            }
        }
        return null;
    }

    @Override
    public Team findTeamByName(String teamName) {
        if (teamName == null) {
            return null;
        }
        for (Team team : teams) {
            if (teamName.equals(team.getTeamName())) {
                return team;
            }
        }
        return null;
    }

    @Override
    public void addHero(Hero hero) {
        if (hero != null) {
            heroes.add(hero);
        }
    }

    @Override
    public void addEquipment(Equipment equipment) {
        if (equipment != null) {
            equipments.add(equipment);
        }
    }

    @Override
    public void addTeam(Team team) {
        if (team != null) {
            teams.add(team);
        }
    }

    @Override
    public boolean removeHeroById(String heroId) {
        if (heroId == null) {
            return false;
        }
        for (int i = 0; i < heroes.size(); i++) {
            if (heroId.equals(heroes.get(i).getHeroId())) {
                heroes.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removeEquipmentById(String equipmentId) {
        if (equipmentId == null) {
            return false;
        }
        for (int i = 0; i < equipments.size(); i++) {
            if (equipmentId.equals(equipments.get(i).getEquipmentId())) {
                equipments.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removeTeamById(String teamId) {
        if (teamId == null) {
            return false;
        }
        for (int i = 0; i < teams.size(); i++) {
            if (teamId.equals(teams.get(i).getTeamId())) {
                teams.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateHero(Hero hero) {
        if (hero == null || hero.getHeroId() == null) {
            return false;
        }
        for (int i = 0; i < heroes.size(); i++) {
            if (hero.getHeroId().equals(heroes.get(i).getHeroId())) {
                heroes.set(i, hero);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateEquipment(Equipment equipment) {
        if (equipment == null || equipment.getEquipmentId() == null) {
            return false;
        }
        for (int i = 0; i < equipments.size(); i++) {
            if (equipment.getEquipmentId().equals(equipments.get(i).getEquipmentId())) {
                equipments.set(i, equipment);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateTeam(Team team) {
        if (team == null || team.getTeamId() == null) {
            return false;
        }
        for (int i = 0; i < teams.size(); i++) {
            if (team.getTeamId().equals(teams.get(i).getTeamId())) {
                teams.set(i, team);
                return true;
            }
        }
        return false;
    }
}
