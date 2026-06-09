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
import java.util.stream.Collectors;

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
            log.info("No heroes found in DB, loaded {} from initializer.", DataInitializer.initializeHeroes().size());
        } else {
            heroes.addAll(dbHeroes);
            log.info("Loaded {} heroes from database.", dbHeroes.size());
        }

        List<Equipment> dbEquips = gameDataDao.findAllEquipments();
        if (dbEquips.isEmpty()) {
            equipments.addAll(DataInitializer.initializeEquipments());
            log.info("No equipments found in DB, loaded {} from initializer.", DataInitializer.initializeEquipments().size());
        } else {
            equipments.addAll(dbEquips);
            log.info("Loaded {} equipments from database.", dbEquips.size());
        }

        List<Team> dbTeams = gameDataDao.findAllTeams();
        if (dbTeams.isEmpty()) {
            teams.addAll(DataInitializer.initializeTeams());
            log.info("No teams found in DB, loaded {} from initializer.", DataInitializer.initializeTeams().size());
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

    // ===== Query operations =====

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
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(new ArrayList<>(players));
    }

    // ===== Fuzzy search implementations =====

    @Override
    public List<Hero> searchHeroes(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getHeroes();
        }
        String lower = keyword.toLowerCase().trim();
        return heroes.stream()
                .filter(h ->
                    (h.getHeroId() != null && h.getHeroId().toLowerCase().contains(lower)) ||
                    (h.getHeroName() != null && h.getHeroName().toLowerCase().contains(lower)) ||
                    (h.getTitle() != null && h.getTitle().toLowerCase().contains(lower)) ||
                    (h.getPosition() != null && h.getPosition().toLowerCase().contains(lower)) ||
                    (h.getHeroType() != null && h.getHeroType().toLowerCase().contains(lower)) ||
                    (h.getDifficulty() != null && h.getDifficulty().toLowerCase().contains(lower))
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<Equipment> searchEquipments(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getEquipments();
        }
        String lower = keyword.toLowerCase().trim();
        return equipments.stream()
                .filter(e ->
                    (e.getEquipmentId() != null && e.getEquipmentId().toLowerCase().contains(lower)) ||
                    (e.getEquipmentName() != null && e.getEquipmentName().toLowerCase().contains(lower)) ||
                    (e.getEquipmentType() != null && e.getEquipmentType().toLowerCase().contains(lower))
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<Team> searchTeams(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getTeams();
        }
        String lower = keyword.toLowerCase().trim();
        return teams.stream()
                .filter(t ->
                    (t.getTeamId() != null && t.getTeamId().toLowerCase().contains(lower)) ||
                    (t.getTeamName() != null && t.getTeamName().toLowerCase().contains(lower)) ||
                    (t.getShortName() != null && t.getShortName().toLowerCase().contains(lower)) ||
                    (t.getRegion() != null && t.getRegion().toLowerCase().contains(lower))
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<Player> searchPlayers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getPlayers();
        }
        String lower = keyword.toLowerCase().trim();
        return players.stream()
                .filter(p ->
                    (p.getId() != null && p.getId().toLowerCase().contains(lower)) ||
                    (p.getNickname() != null && p.getNickname().toLowerCase().contains(lower))
                )
                .collect(Collectors.toList());
    }

    // ===== Exact match methods =====

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
            if ("鑳滃埄".equals(record.getResult())) wins++;
        }
        return wins;
    }

    @Override
    public void displayPlayerDetails(String id) {
        if (id == null) {
            System.out.println("锟斤拷锟絀D锟斤拷锟角称诧拷锟斤拷为锟秸★拷");
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
            System.out.println("未找到ID或名称为 " + id + " 的用户。");
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

        System.out.println("===== 锟斤拷锟斤拷锟较革拷锟较?=====");
        System.out.println("锟斤拷锟絀D: " + displayPlayer.getId());
        System.out.println("锟角筹拷: " + displayPlayer.getNickname());

        if (playerTeam != null) {
            System.out.println("锟斤拷锟斤拷战璐? " + playerTeam.getTeamName()
                    + " (" + playerTeam.getShortName() + ")");
            System.out.println("战锟斤拷锟斤拷璐? " + playerTeam.getRegion());
            System.out.println("锟斤拷璐? " + playerTeam.getCoachName());
            System.out.println("锟接筹拷: " + playerTeam.getCaptainName());
            System.out.println("瀵规垬璁板綍: "
                    + String.format("%.1f%%", playerTeam.getWinRate() * 100));

            if (realPlayer != null) {
                int totalMatches = RankingService.calculateTotalMatches(realPlayer);
                double winRate = RankingService.calculateWinRate(realPlayer);
                long wins = countWins(realPlayer);
                long losses = totalMatches - wins;

                System.out.println("\n----- 锟斤拷锟斤拷战锟斤拷 -----");
                System.out.println("锟杰筹拷璐? " + totalMatches);
                System.out.println("鑳滅巼: " + String.format("%.1f%%", winRate * 100));
                System.out.println("鑳? " + wins + " | 璐? " + losses);

                List<MatchRecord> records = realPlayer.getMatchOverviews();
                if (records != null && !records.isEmpty()) {
                    System.out.println("\n----- 锟皆局硷拷录摘要 -----");
                    int showCount = Math.min(5, records.size());
                    for (int i = 0; i < showCount; i++) {
                        MatchRecord record = records.get(i);
                        System.out.println("  锟皆撅拷" + (i + 1) + ": "
                                + record.getMatchId()
                                + " | " + record.getMatchMode()
                                + " | " + record.getResult()
                                + " | 时璐? " + record.getDurationSeconds() + "锟斤拷"
                                + " | 时璐? " + record.getMatchTime());
                    }
                    if (records.size() > 5) {
                        System.out.println("  ... (锟斤拷" + records.size() + "锟斤拷锟皆撅拷)");
                    }
                }
            } else {
                System.out.println("\n----- 锟斤拷锟斤拷战锟斤拷 -----");
                System.out.println("该玩家暂无详细比赛数据。");
            }

            System.out.println("\n----- 战锟接筹拷锟斤拷英锟斤拷 -----");
            for (String heroName : playerTeam.getMainHeroes()) {
                Hero hero = findHeroByName(heroName);
                if (hero != null) {
                    System.out.println("  " + hero.getHeroName()
                            + " [" + hero.getTitle() + "]");
                    System.out.println("    锟斤拷位: " + hero.getPosition()
                            + " | 锟斤拷璐? " + hero.getHeroType());
                    System.out.println("    锟斤拷璐? " + hero.getPassiveSkill()
                            + " / " + hero.getSkillOne()
                            + " / " + hero.getSkillTwo()
                            + " / " + hero.getSkillThree());
                    System.out.println("    锟斤拷璐? 锟斤拷锟斤拷" + hero.getSurvivalAbility()
                            + " | 锟斤拷锟斤拷" + hero.getAttackAbility()
                            + " | 锟斤拷锟斤拷" + hero.getSkillAbility()
                            + " | 锟斤拷锟斤拷" + hero.getSupportAbility()
                            + " | 锟窖讹拷: " + hero.getDifficulty());

                    if (!hero.getRecommendedEquipmentIds().isEmpty()) {
                        System.out.println("    锟狡硷拷装锟斤拷:");
                        for (String eqId : hero.getRecommendedEquipmentIds()) {
                            Equipment eq = findEquipmentById(eqId);
                            if (eq != null) {
                                System.out.println("      " + eq.getEquipmentName()
                                        + " (" + eq.getEquipmentType()
                                        + ", " + eq.getPrice() + "锟斤拷锟?");
                            }
                        }
                    }
                }
            }

            System.out.println("\n战锟斤拷锟斤拷璐? "
                    + String.join(", ", playerTeam.getHonors()));
        } else {
            System.out.println("该玩家目前不属于任何战队。");
        }

        System.out.println("========================");
    }
}


