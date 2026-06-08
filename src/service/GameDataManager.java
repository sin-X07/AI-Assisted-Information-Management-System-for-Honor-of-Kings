package service;

import db.GameDataDao;
import model.Equipment;
import model.Hero;
import model.MatchRecord;
import model.Person;
import model.Player;
import model.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.DataInitializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameDataManager implements Searchable {
    private static final Logger log = LoggerFactory.getLogger(GameDataManager.class);

    private final List<Hero> heroes;
    private final List<Equipment> equipments;
    private final List<Team> teams;
    private final List<Player> players;
    private final GameDataDao gameDataDao;
    private final OperationLogService opLogService;

    public GameDataManager() {
        this.gameDataDao = new GameDataDao();
        this.opLogService = OperationLogService.getInstance();
        this.heroes = new ArrayList<>();
        this.equipments = new ArrayList<>();
        this.teams = new ArrayList<>();
        this.players = new ArrayList<>(DataInitializer.initializePlayers());

        loadDataFromDatabase();
    }

    /**
     * Load data from SQLite. If the database is empty, seed from DataInitializer.
     */
    private void loadDataFromDatabase() {
        List<Hero> dbHeroes = gameDataDao.findAllHeroes();
        if (dbHeroes.isEmpty()) {
            heroes.addAll(DataInitializer.initializeHeroes());
            log.info("No heroes found in DB, loaded {} from initializer.", heroes.size());
        } else {
            heroes.addAll(dbHeroes);
            log.info("Loaded {} heroes from database.", dbHeroes.size());
        }

        List<Equipment> dbEquips = gameDataDao.findAllEquipments();
        if (dbEquips.isEmpty()) {
            equipments.addAll(DataInitializer.initializeEquipments());
            log.info("No equipments found in DB, loaded {} from initializer.", equipments.size());
        } else {
            equipments.addAll(dbEquips);
            log.info("Loaded {} equipments from database.", dbEquips.size());
        }

        List<Team> dbTeams = gameDataDao.findAllTeams();
        if (dbTeams.isEmpty()) {
            teams.addAll(DataInitializer.initializeTeams());
            log.info("No teams found in DB, loaded {} from initializer.", teams.size());
        } else {
            teams.addAll(dbTeams);
            log.info("Loaded {} teams from database.", dbTeams.size());
        }
    }

    // ===== Persist all game data to DB =====

    /** Save all in-memory game data to SQLite. Called on app startup for seeding. */
    public void persistAllToDatabase() {
        for (Hero h : heroes) gameDataDao.insertHero(h);
        for (Equipment e : equipments) gameDataDao.insertEquipment(e);
        for (Team t : teams) gameDataDao.insertTeam(t);
        log.info("All game data persisted to database. Heroes={}, Equipments={}, Teams={}",
                heroes.size(), equipments.size(), teams.size());
    }

    // ===== Hero operations =====

    @Override
    public void addHero(Hero hero) {
        if (hero != null) {
            heroes.add(hero);
            gameDataDao.insertHero(hero);
            log.debug("Hero [{}] added.", hero.getHeroName());
        }
    }

    @Override
    public boolean removeHeroById(String heroId) {
        if (heroId == null) return false;
        for (int i = 0; i < heroes.size(); i++) {
            if (heroId.equals(heroes.get(i).getHeroId())) {
                Hero removed = heroes.remove(i);
                gameDataDao.deleteHeroById(heroId);
                log.debug("Hero [{}] removed.", removed.getHeroName());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateHero(Hero hero) {
        if (hero == null || hero.getHeroId() == null) return false;
        for (int i = 0; i < heroes.size(); i++) {
            if (hero.getHeroId().equals(heroes.get(i).getHeroId())) {
                heroes.set(i, hero);
                gameDataDao.insertHero(hero); // INSERT OR REPLACE
                log.debug("Hero [{}] updated.", hero.getHeroName());
                return true;
            }
        }
        return false;
    }

    // ===== Equipment operations =====

    @Override
    public void addEquipment(Equipment equipment) {
        if (equipment != null) {
            equipments.add(equipment);
            gameDataDao.insertEquipment(equipment);
            log.debug("Equipment [{}] added.", equipment.getEquipmentName());
        }
    }

    @Override
    public boolean removeEquipmentById(String equipmentId) {
        if (equipmentId == null) return false;
        for (int i = 0; i < equipments.size(); i++) {
            if (equipmentId.equals(equipments.get(i).getEquipmentId())) {
                Equipment removed = equipments.remove(i);
                gameDataDao.deleteEquipmentById(equipmentId);
                log.debug("Equipment [{}] removed.", removed.getEquipmentName());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateEquipment(Equipment equipment) {
        if (equipment == null || equipment.getEquipmentId() == null) return false;
        for (int i = 0; i < equipments.size(); i++) {
            if (equipment.getEquipmentId().equals(equipments.get(i).getEquipmentId())) {
                equipments.set(i, equipment);
                gameDataDao.insertEquipment(equipment);
                log.debug("Equipment [{}] updated.", equipment.getEquipmentName());
                return true;
            }
        }
        return false;
    }

    // ===== Team operations =====

    @Override
    public void addTeam(Team team) {
        if (team != null) {
            teams.add(team);
            gameDataDao.insertTeam(team);
            log.debug("Team [{}] added.", team.getTeamName());
        }
    }

    @Override
    public boolean removeTeamById(String teamId) {
        if (teamId == null) return false;
        for (int i = 0; i < teams.size(); i++) {
            if (teamId.equals(teams.get(i).getTeamId())) {
                Team removed = teams.remove(i);
                gameDataDao.deleteTeamById(teamId);
                log.debug("Team [{}] removed.", removed.getTeamName());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateTeam(Team team) {
        if (team == null || team.getTeamId() == null) return false;
        for (int i = 0; i < teams.size(); i++) {
            if (team.getTeamId().equals(teams.get(i).getTeamId())) {
                teams.set(i, team);
                gameDataDao.insertTeam(team);
                log.debug("Team [{}] updated.", team.getTeamName());
                return true;
            }
        }
        return false;
    }

    // ===== Query operations (unchanged but with logging) =====

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
        if (heroId == null) return null;
        for (Hero hero : heroes) {
            if (heroId.equals(hero.getHeroId())) return hero;
        }
        return null;
    }

    @Override
    public Equipment findEquipmentById(String equipmentId) {
        if (equipmentId == null) return null;
        for (Equipment equipment : equipments) {
            if (equipmentId.equals(equipment.getEquipmentId())) return equipment;
        }
        return null;
    }

    @Override
    public Team findTeamById(String teamId) {
        if (teamId == null) return null;
        for (Team team : teams) {
            if (teamId.equals(team.getTeamId())) return team;
        }
        return null;
    }

    @Override
    public Hero findHeroByName(String heroName) {
        if (heroName == null) return null;
        for (Hero hero : heroes) {
            if (heroName.equals(hero.getHeroName())) return hero;
        }
        return null;
    }

    @Override
    public Equipment findEquipmentByName(String equipmentName) {
        if (equipmentName == null) return null;
        for (Equipment equipment : equipments) {
            if (equipmentName.equals(equipment.getEquipmentName())) return equipment;
        }
        return null;
    }

    @Override
    public Team findTeamByName(String teamName) {
        if (teamName == null) return null;
        for (Team team : teams) {
            if (teamName.equals(team.getTeamName())) return team;
        }
        return null;
    }

    @Override
    public Player findPlainPlayerById(String id) {
        if (id == null) return null;
        Player realPlayer = findRealPlayerById(id);
        if (realPlayer != null) return realPlayer;
        for (Team team : teams) {
            for (String memberName : team.getMemberNames()) {
                if (id.equals(memberName)) {
                    Player player = new Player();
                    player.setId(id);
                    player.setNickname(memberName);
                    player.setRole("PLAYER");
                    return player;
                }
            }
        }
        return null;
    }

    private Player findRealPlayerById(String id) {
        if (id == null) return null;
        for (Player player : players) {
            if (id.equals(player.getId()) || id.equals(player.getNickname())) return player;
        }
        return null;
    }

    private long countWins(Player player) {
        if (player == null) return 0;
        List<MatchRecord> records = player.getMatchOverviews();
        if (records == null || records.isEmpty()) return 0;
        long wins = 0;
        for (MatchRecord record : records) {
            if ("胜利".equals(record.getResult())) wins++;
        }
        return wins;
    }

    @Override
    public void displayPlayerDetails(String id) {
        if (id == null) {
            System.out.println("玩家ID或昵称不能为空。");
            return;
        }

        Player realPlayer = findRealPlayerById(id);
        Player plainPlayer = null;
        if (realPlayer == null) {
            for (Team team : teams) {
                if (team.getMemberNames().contains(id)) {
                    plainPlayer = new Player();
                    plainPlayer.setId(id);
                    plainPlayer.setNickname(id);
                    plainPlayer.setRole("PLAYER");
                    break;
                }
            }
        }

        Player displayPlayer = realPlayer != null ? realPlayer : plainPlayer;
        if (displayPlayer == null) {
            System.out.println("未找到ID或昵称为 " + id + " 的玩家。");
            return;
        }

        Team playerTeam = null;
        for (Team team : teams) {
            String playerNickname = displayPlayer.getNickname();
            if (playerNickname != null && team.getMemberNames().contains(playerNickname)) {
                playerTeam = team;
                break;
            }
        }

        System.out.println("===== 玩家详细信息 =====");
        System.out.println("玩家ID: " + displayPlayer.getId());
        System.out.println("昵称: " + displayPlayer.getNickname());

        if (playerTeam != null) {
            System.out.println("所属战队: " + playerTeam.getTeamName()
                    + " (" + playerTeam.getShortName() + ")");
            System.out.println("战队区域: " + playerTeam.getRegion());
            System.out.println("教练: " + playerTeam.getCoachName());
            System.out.println("队长: " + playerTeam.getCaptainName());
            System.out.println("战队胜率: "
                    + String.format("%.1f%%", playerTeam.getWinRate() * 100));

            if (realPlayer != null) {
                int totalMatches = RankingService.calculateTotalMatches(realPlayer);
                double winRate = RankingService.calculateWinRate(realPlayer);
                long wins = countWins(realPlayer);
                long losses = totalMatches - wins;

                System.out.println("\n----- 个人战绩 -----");
                System.out.println("总场次: " + totalMatches);
                System.out.println("胜率: " + String.format("%.1f%%", winRate * 100));
                System.out.println("胜: " + wins + " | 负: " + losses);

                List<MatchRecord> records = realPlayer.getMatchOverviews();
                if (records != null && !records.isEmpty()) {
                    System.out.println("\n----- 对局记录摘要 -----");
                    int showCount = Math.min(5, records.size());
                    for (int i = 0; i < showCount; i++) {
                        MatchRecord record = records.get(i);
                        System.out.println("  对局" + (i + 1) + ": "
                                + record.getMatchId()
                                + " | " + record.getMatchMode()
                                + " | " + record.getResult()
                                + " | 时长: " + record.getDurationSeconds() + "秒"
                                + " | 时间: " + record.getMatchTime());
                    }
                    if (records.size() > 5) {
                        System.out.println("  ... (共" + records.size() + "场对局)");
                    }
                }
            } else {
                System.out.println("\n----- 个人战绩 -----");
                System.out.println("（该玩家暂无详细战绩数据）");
            }

            System.out.println("\n----- 战队常用英雄 -----");
            for (String heroName : playerTeam.getMainHeroes()) {
                Hero hero = findHeroByName(heroName);
                if (hero != null) {
                    System.out.println("  " + hero.getHeroName()
                            + " [" + hero.getTitle() + "]");
                    System.out.println("    定位: " + hero.getPosition()
                            + " | 类型: " + hero.getHeroType());
                    System.out.println("    技能: " + hero.getPassiveSkill()
                            + " / " + hero.getSkillOne()
                            + " / " + hero.getSkillTwo()
                            + " / " + hero.getSkillThree());
                    System.out.println("    能力: 生存" + hero.getSurvivalAbility()
                            + " | 攻击" + hero.getAttackAbility()
                            + " | 技能" + hero.getSkillAbility()
                            + " | 辅助" + hero.getSupportAbility()
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
        } else {
            System.out.println("（该玩家未加入任何战队）");
        }

        System.out.println("========================");
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(new ArrayList<>(players));
    }
}
