package db;

import model.Equipment;
import model.Hero;
import model.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Access Object for game data (heroes, equipment, teams).
 */
public class GameDataDao {
    private static final Logger log = LoggerFactory.getLogger(GameDataDao.class);
    private final DatabaseManager dbManager;

    public GameDataDao() {
        this.dbManager = DatabaseManager.getInstance();
    }

    // ===== Heroes =====

    public void insertHero(Hero hero) {
        String sql = """
            INSERT OR REPLACE INTO heroes (hero_id, hero_name, title, position, hero_type, difficulty,
                survival_ability, attack_ability, skill_ability, support_ability,
                passive_skill, skill_one, skill_two, skill_three,
                recommended_equipment_ids, recommended_summoner_skill, description)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hero.getHeroId());
            ps.setString(2, hero.getHeroName());
            ps.setString(3, hero.getTitle());
            ps.setString(4, hero.getPosition());
            ps.setString(5, hero.getHeroType());
            ps.setString(6, hero.getDifficulty());
            ps.setInt(7, hero.getSurvivalAbility());
            ps.setInt(8, hero.getAttackAbility());
            ps.setInt(9, hero.getSkillAbility());
            ps.setInt(10, hero.getSupportAbility());
            ps.setString(11, hero.getPassiveSkill());
            ps.setString(12, hero.getSkillOne());
            ps.setString(13, hero.getSkillTwo());
            ps.setString(14, hero.getSkillThree());
            ps.setString(15, String.join(",", hero.getRecommendedEquipmentIds()));
            ps.setString(16, hero.getRecommendedSummonerSkill());
            ps.setString(17, hero.getDescription());
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to insert hero [{}]", hero.getHeroId(), e);
        }
    }

    public void deleteHeroById(String heroId) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM heroes WHERE hero_id=?")) {
            ps.setString(1, heroId);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to delete hero [{}]", heroId, e);
        }
    }

    public List<Hero> findAllHeroes() {
        List<Hero> list = new ArrayList<>();
        String sql = "SELECT * FROM heroes ORDER BY hero_id";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapHero(rs));
        } catch (SQLException e) {
            log.error("Failed to query heroes.", e);
        }
        return list;
    }

    // ===== Equipments =====

    public void insertEquipment(Equipment eq) {
        String sql = """
            INSERT OR REPLACE INTO equipments (equipment_id, equipment_name, equipment_type, price,
                attack_bonus, magic_attack_bonus, health_bonus, mana_bonus,
                armor_bonus, magic_resistance_bonus, cooldown_reduction, critical_rate, movement_speed,
                passive_effect, suitable_hero_types, description)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eq.getEquipmentId());
            ps.setString(2, eq.getEquipmentName());
            ps.setString(3, eq.getEquipmentType());
            ps.setInt(4, eq.getPrice());
            ps.setInt(5, eq.getAttackBonus());
            ps.setInt(6, eq.getMagicAttackBonus());
            ps.setInt(7, eq.getHealthBonus());
            ps.setInt(8, eq.getManaBonus());
            ps.setInt(9, eq.getArmorBonus());
            ps.setInt(10, eq.getMagicResistanceBonus());
            ps.setDouble(11, eq.getCooldownReduction());
            ps.setDouble(12, eq.getCriticalRate());
            ps.setDouble(13, eq.getMovementSpeed());
            ps.setString(14, eq.getPassiveEffect());
            ps.setString(15, String.join(",", eq.getSuitableHeroTypes()));
            ps.setString(16, eq.getDescription());
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to insert equipment [{}]", eq.getEquipmentId(), e);
        }
    }

    public void deleteEquipmentById(String equipmentId) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM equipments WHERE equipment_id=?")) {
            ps.setString(1, equipmentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to delete equipment [{}]", equipmentId, e);
        }
    }

    public List<Equipment> findAllEquipments() {
        List<Equipment> list = new ArrayList<>();
        String sql = "SELECT * FROM equipments ORDER BY equipment_id";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapEquipment(rs));
        } catch (SQLException e) {
            log.error("Failed to query equipments.", e);
        }
        return list;
    }

    // ===== Teams =====

    public void insertTeam(Team team) {
        String sql = """
            INSERT OR REPLACE INTO teams (team_id, team_name, short_name, region,
                coach_name, captain_name, member_names, main_heroes, honors,
                win_rate, total_matches, description, create_time, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, team.getTeamId());
            ps.setString(2, team.getTeamName());
            ps.setString(3, team.getShortName());
            ps.setString(4, team.getRegion());
            ps.setString(5, team.getCoachName());
            ps.setString(6, team.getCaptainName());
            ps.setString(7, String.join(",", team.getMemberNames()));
            ps.setString(8, String.join(",", team.getMainHeroes()));
            ps.setString(9, String.join(",", team.getHonors()));
            ps.setDouble(10, team.getWinRate());
            ps.setInt(11, team.getTotalMatches());
            ps.setString(12, team.getDescription());
            ps.setString(13, team.getCreateTime() != null
                    ? team.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "");
            ps.setString(14, team.getStatus());
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to insert team [{}]", team.getTeamId(), e);
        }
    }

    public void deleteTeamById(String teamId) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM teams WHERE team_id=?")) {
            ps.setString(1, teamId);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("Failed to delete team [{}]", teamId, e);
        }
    }

    public List<Team> findAllTeams() {
        List<Team> list = new ArrayList<>();
        String sql = "SELECT * FROM teams ORDER BY team_id";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapTeam(rs));
        } catch (SQLException e) {
            log.error("Failed to query teams.", e);
        }
        return list;
    }

    // ===== Mapping helpers =====

    private Hero mapHero(ResultSet rs) throws SQLException {
        Hero hero = new Hero();
        hero.setHeroId(rs.getString("hero_id"));
        hero.setHeroName(rs.getString("hero_name"));
        hero.setTitle(rs.getString("title"));
        hero.setPosition(rs.getString("position"));
        hero.setHeroType(rs.getString("hero_type"));
        hero.setDifficulty(rs.getString("difficulty"));
        hero.setSurvivalAbility(rs.getInt("survival_ability"));
        hero.setAttackAbility(rs.getInt("attack_ability"));
        hero.setSkillAbility(rs.getInt("skill_ability"));
        hero.setSupportAbility(rs.getInt("support_ability"));
        hero.setPassiveSkill(rs.getString("passive_skill"));
        hero.setSkillOne(rs.getString("skill_one"));
        hero.setSkillTwo(rs.getString("skill_two"));
        hero.setSkillThree(rs.getString("skill_three"));
        String eqIds = rs.getString("recommended_equipment_ids");
        hero.setRecommendedEquipmentIds(eqIds != null && !eqIds.isEmpty()
                ? Arrays.asList(eqIds.split(",")) : new ArrayList<>());
        hero.setRecommendedSummonerSkill(rs.getString("recommended_summoner_skill"));
        hero.setDescription(rs.getString("description"));
        return hero;
    }

    private Equipment mapEquipment(ResultSet rs) throws SQLException {
        Equipment eq = new Equipment();
        eq.setEquipmentId(rs.getString("equipment_id"));
        eq.setEquipmentName(rs.getString("equipment_name"));
        eq.setEquipmentType(rs.getString("equipment_type"));
        eq.setPrice(rs.getInt("price"));
        eq.setAttackBonus(rs.getInt("attack_bonus"));
        eq.setMagicAttackBonus(rs.getInt("magic_attack_bonus"));
        eq.setHealthBonus(rs.getInt("health_bonus"));
        eq.setManaBonus(rs.getInt("mana_bonus"));
        eq.setArmorBonus(rs.getInt("armor_bonus"));
        eq.setMagicResistanceBonus(rs.getInt("magic_resistance_bonus"));
        eq.setCooldownReduction(rs.getDouble("cooldown_reduction"));
        eq.setCriticalRate(rs.getDouble("critical_rate"));
        eq.setMovementSpeed(rs.getDouble("movement_speed"));
        eq.setPassiveEffect(rs.getString("passive_effect"));
        String types = rs.getString("suitable_hero_types");
        eq.setSuitableHeroTypes(types != null && !types.isEmpty()
                ? Arrays.asList(types.split(",")) : new ArrayList<>());
        eq.setDescription(rs.getString("description"));
        return eq;
    }

    private Team mapTeam(ResultSet rs) throws SQLException {
        Team team = new Team();
        team.setTeamId(rs.getString("team_id"));
        team.setTeamName(rs.getString("team_name"));
        team.setShortName(rs.getString("short_name"));
        team.setRegion(rs.getString("region"));
        team.setCoachName(rs.getString("coach_name"));
        team.setCaptainName(rs.getString("captain_name"));
        team.setMemberNames(splitList(rs.getString("member_names")));
        team.setMainHeroes(splitList(rs.getString("main_heroes")));
        team.setHonors(splitList(rs.getString("honors")));
        team.setWinRate(rs.getDouble("win_rate"));
        team.setTotalMatches(rs.getInt("total_matches"));
        team.setDescription(rs.getString("description"));
        try {
            String ct = rs.getString("create_time");
            if (ct != null && !ct.isEmpty())
                team.setCreateTime(LocalDateTime.parse(ct, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        } catch (Exception ignored) {}
        team.setStatus(rs.getString("status"));
        return team;
    }

    private List<String> splitList(String value) {
        if (value == null || value.isEmpty()) return new ArrayList<>();
        return Arrays.asList(value.split(","));
    }
}
