package service;

import model.Equipment;
import model.Hero;
import model.Player;
import model.Team;
import util.DataInitializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameDataManager implements Searchable {
    private final List<Hero> heroes;
    private final List<Equipment> equipments;
    private final List<Team> teams;
    private final List<Player> players;

    public GameDataManager() {
        this.heroes = new ArrayList<>(DataInitializer.initializeHeroes());
        this.equipments = new ArrayList<>(DataInitializer.initializeEquipments());
        this.teams = new ArrayList<>(DataInitializer.initializeTeams());
        this.players = new ArrayList<>(DataInitializer.initializePlayers());
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

    @Override
    public Player findPlainPlayerById(String id) {
        if (id == null) {
            return null;
        }
        for (Team team : teams) {
            for (String memberName : team.getMemberNames()) {
                if (id.equals(memberName)) {
                    Player player = new Player();
                    player.setId(id);
                    player.setNickname(memberName);
                    return player;
                }
            }
        }
        return null;
    }

    @Override
    public void displayPlayerDetails(String id) {
        if (id == null) {
            System.out.println("玩家ID不能为空。");
            return;
        }

        Player player = findPlainPlayerById(id);
        if (player == null) {
            System.out.println("未找到ID为 " + id + " 的玩家。");
            return;
        }

        Team playerTeam = null;
        for (Team team : teams) {
            if (team.getMemberNames().contains(id)) {
                playerTeam = team;
                break;
            }
        }

        System.out.println("===== 玩家详细信息 =====");
        System.out.println("玩家ID: " + player.getId());
        System.out.println("昵称: " + player.getNickname());

        if (playerTeam != null) {
            System.out.println("所属战队: " + playerTeam.getTeamName()
                    + " (" + playerTeam.getShortName() + ")");
            System.out.println("战队区域: " + playerTeam.getRegion());
            System.out.println("战队胜率: "
                    + String.format("%.1f%%", playerTeam.getWinRate() * 100));
            System.out.println("教练: " + playerTeam.getCoachName());
            System.out.println("队长: " + playerTeam.getCaptainName());

            System.out.println("\n----- 战队常用英雄 -----");
            for (String heroName : playerTeam.getMainHeroes()) {
                Hero hero = findHeroByName(heroName);
                if (hero != null) {
                    System.out.println("  " + hero.getHeroName()
                            + " [" + hero.getTitle() + "]");
                    System.out.println("    定位: " + hero.getPosition()
                            + " | 类型: " + hero.getHeroType()
                            + " | 难度: " + hero.getDifficulty());

                    if (!hero.getRecommendedEquipmentIds().isEmpty()) {
                        System.out.println("    推荐装备:");
                        for (String eqId : hero.getRecommendedEquipmentIds()) {
                            Equipment eq = findEquipmentById(eqId);
                            if (eq != null) {
                                System.out.println("      " + eq.getEquipmentName()
                                        + " (" + eq.getEquipmentType()
                                        + ", " + eq.getPrice() + "金币)");
                            }
                        }
                    }
                }
            }

            System.out.println("\n战队荣誉: "
                    + String.join(", ", playerTeam.getHonors()));
        }

        System.out.println("========================");
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(new ArrayList<>(players));
    }
}