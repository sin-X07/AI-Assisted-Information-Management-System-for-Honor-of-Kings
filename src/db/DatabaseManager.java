package db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages SQLite database connection and schema initialization.
 */
public class DatabaseManager {
    private static final Logger log = LoggerFactory.getLogger(DatabaseManager.class);
    private static final String DB_URL = "jdbc:sqlite:hok_data.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            log.info("SQLite database connection established: {}", DB_URL);
        }
        return connection;
    }

    public void initialize() {
        try {
            Class.forName("org.sqlite.JDBC");
            log.info("SQLite JDBC driver loaded successfully.");
            createTables();
        } catch (ClassNotFoundException e) {
            log.error("SQLite JDBC driver not found.", e);
            throw new RuntimeException("SQLite JDBC driver not found.", e);
        }
    }

    private void createTables() {
        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(ADMINS_TABLE_SQL);
            stmt.execute(OPERATION_LOGS_TABLE_SQL);
            stmt.execute(HEROES_TABLE_SQL);
            stmt.execute(EQUIPMENTS_TABLE_SQL);
            stmt.execute(TEAMS_TABLE_SQL);
            log.info("All database tables initialized successfully.");
        } catch (SQLException e) {
            log.error("Failed to create database tables.", e);
            throw new RuntimeException("Failed to initialize database schema.", e);
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                log.info("SQLite database connection closed.");
            }
        } catch (SQLException e) {
            log.error("Error closing database connection.", e);
        }
    }

    // ===== Table Schemas =====

    private static final String ADMINS_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS admins (
            person_id      TEXT PRIMARY KEY,
            admin_id       TEXT NOT NULL,
            username       TEXT NOT NULL UNIQUE,
            password       TEXT NOT NULL,
            nickname       TEXT NOT NULL,
            phone          TEXT DEFAULT '',
            email          TEXT DEFAULT '',
            permission_level INTEGER DEFAULT 1,
            department     TEXT DEFAULT '',
            managed_area   TEXT DEFAULT '',
            can_manage_users       INTEGER DEFAULT 0,
            can_manage_hero_data   INTEGER DEFAULT 1,
            can_manage_system_config INTEGER DEFAULT 0,
            work_number    TEXT DEFAULT '',
            status         TEXT DEFAULT '正常',
            create_time    TEXT DEFAULT '',
            last_login_time TEXT DEFAULT ''
        )
    """;

    private static final String OPERATION_LOGS_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS operation_logs (
            id               INTEGER PRIMARY KEY AUTOINCREMENT,
            admin_person_id  TEXT NOT NULL,
            admin_username   TEXT NOT NULL,
            admin_nickname   TEXT NOT NULL,
            operation_type   TEXT NOT NULL,
            target_type      TEXT NOT NULL,
            target_id        TEXT DEFAULT '',
            detail           TEXT DEFAULT '',
            result           TEXT DEFAULT '成功',
            operation_time   TEXT NOT NULL
        )
    """;

    private static final String HEROES_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS heroes (
            hero_id       TEXT PRIMARY KEY,
            hero_name     TEXT NOT NULL,
            title         TEXT DEFAULT '',
            position      TEXT DEFAULT '',
            hero_type     TEXT DEFAULT '',
            difficulty    TEXT DEFAULT '',
            survival_ability  INTEGER DEFAULT 0,
            attack_ability    INTEGER DEFAULT 0,
            skill_ability     INTEGER DEFAULT 0,
            support_ability   INTEGER DEFAULT 0,
            passive_skill     TEXT DEFAULT '',
            skill_one         TEXT DEFAULT '',
            skill_two         TEXT DEFAULT '',
            skill_three       TEXT DEFAULT '',
            recommended_equipment_ids TEXT DEFAULT '',
            recommended_summoner_skill TEXT DEFAULT '',
            description    TEXT DEFAULT ''
        )
    """;

    private static final String EQUIPMENTS_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS equipments (
            equipment_id   TEXT PRIMARY KEY,
            equipment_name TEXT NOT NULL,
            equipment_type TEXT DEFAULT '',
            price          INTEGER DEFAULT 0,
            attack_bonus   INTEGER DEFAULT 0,
            magic_attack_bonus INTEGER DEFAULT 0,
            health_bonus   INTEGER DEFAULT 0,
            mana_bonus     INTEGER DEFAULT 0,
            armor_bonus    INTEGER DEFAULT 0,
            magic_resistance_bonus INTEGER DEFAULT 0,
            cooldown_reduction REAL DEFAULT 0.0,
            critical_rate  REAL DEFAULT 0.0,
            movement_speed REAL DEFAULT 0.0,
            passive_effect TEXT DEFAULT '',
            suitable_hero_types TEXT DEFAULT '',
            description    TEXT DEFAULT ''
        )
    """;

    private static final String TEAMS_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS teams (
            team_id      TEXT PRIMARY KEY,
            team_name    TEXT NOT NULL,
            short_name   TEXT DEFAULT '',
            region       TEXT DEFAULT '',
            coach_name   TEXT DEFAULT '',
            captain_name TEXT DEFAULT '',
            member_names TEXT DEFAULT '',
            main_heroes  TEXT DEFAULT '',
            honors       TEXT DEFAULT '',
            win_rate     REAL DEFAULT 0.0,
            total_matches INTEGER DEFAULT 0,
            description  TEXT DEFAULT '',
            create_time  TEXT DEFAULT '',
            status       TEXT DEFAULT '正常'
        )
    """;
}
