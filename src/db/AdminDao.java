package db;

import model.Admin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Admin operations against SQLite.
 */
public class AdminDao {
    private static final Logger log = LoggerFactory.getLogger(AdminDao.class);
    private final DatabaseManager dbManager;

    public AdminDao() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public void insert(Admin admin) {
        String sql = """
            INSERT INTO admins (person_id, admin_id, username, password, nickname, phone, email,
                                permission_level, department, managed_area, can_manage_users,
                                can_manage_hero_data, can_manage_system_config, work_number,
                                status, create_time, last_login_time)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, admin.getId());
            ps.setString(2, admin.getAdminId());
            ps.setString(3, admin.getUsername());
            ps.setString(4, admin.getPassword());
            ps.setString(5, admin.getNickname());
            ps.setString(6, admin.getPhone() != null ? admin.getPhone() : "");
            ps.setString(7, admin.getEmail() != null ? admin.getEmail() : "");
            ps.setInt(8, admin.getPermissionLevel());
            ps.setString(9, admin.getDepartment() != null ? admin.getDepartment() : "");
            ps.setString(10, admin.getManagedArea() != null ? admin.getManagedArea() : "");
            ps.setInt(11, admin.isCanManageUsers() ? 1 : 0);
            ps.setInt(12, admin.isCanManageHeroData() ? 1 : 0);
            ps.setInt(13, admin.isCanManageSystemConfig() ? 1 : 0);
            ps.setString(14, admin.getWorkNumber() != null ? admin.getWorkNumber() : "");
            ps.setString(15, admin.getStatus() != null ? admin.getStatus() : "正常");
            ps.setString(16, admin.getCreateTime() != null
                    ? admin.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "");
            ps.setString(17, admin.getLastLoginTime() != null
                    ? admin.getLastLoginTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "");
            ps.executeUpdate();
            log.debug("Admin [{}] inserted into database.", admin.getUsername());
        } catch (SQLException e) {
            log.error("Failed to insert admin [{}]", admin.getUsername(), e);
        }
    }

    public void update(Admin admin) {
        String sql = """
            UPDATE admins SET admin_id=?, username=?, password=?, nickname=?, phone=?, email=?,
                              permission_level=?, department=?, managed_area=?, can_manage_users=?,
                              can_manage_hero_data=?, can_manage_system_config=?, work_number=?,
                              status=?, last_login_time=?
            WHERE person_id=?
        """;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, admin.getAdminId());
            ps.setString(2, admin.getUsername());
            ps.setString(3, admin.getPassword());
            ps.setString(4, admin.getNickname());
            ps.setString(5, admin.getPhone() != null ? admin.getPhone() : "");
            ps.setString(6, admin.getEmail() != null ? admin.getEmail() : "");
            ps.setInt(7, admin.getPermissionLevel());
            ps.setString(8, admin.getDepartment() != null ? admin.getDepartment() : "");
            ps.setString(9, admin.getManagedArea() != null ? admin.getManagedArea() : "");
            ps.setInt(10, admin.isCanManageUsers() ? 1 : 0);
            ps.setInt(11, admin.isCanManageHeroData() ? 1 : 0);
            ps.setInt(12, admin.isCanManageSystemConfig() ? 1 : 0);
            ps.setString(13, admin.getWorkNumber() != null ? admin.getWorkNumber() : "");
            ps.setString(14, admin.getStatus() != null ? admin.getStatus() : "正常");
            ps.setString(15, admin.getLastLoginTime() != null
                    ? admin.getLastLoginTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "");
            ps.setString(16, admin.getId());
            ps.executeUpdate();
            log.debug("Admin [{}] updated in database.", admin.getUsername());
        } catch (SQLException e) {
            log.error("Failed to update admin [{}]", admin.getUsername(), e);
        }
    }

    public void deleteByPersonId(String personId) {
        String sql = "DELETE FROM admins WHERE person_id=?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, personId);
            ps.executeUpdate();
            log.debug("Admin [person_id={}] deleted from database.", personId);
        } catch (SQLException e) {
            log.error("Failed to delete admin [{}]", personId, e);
        }
    }

    public Admin findByUsername(String username) {
        String sql = "SELECT * FROM admins WHERE username=?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            log.error("Failed to query admin by username [{}]", username, e);
        }
        return null;
    }

    public List<Admin> findAll() {
        List<Admin> list = new ArrayList<>();
        String sql = "SELECT * FROM admins ORDER BY create_time";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            log.error("Failed to query all admins.", e);
        }
        return list;
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM admins";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            log.error("Failed to count admins.", e);
        }
        return 0;
    }

    private Admin mapRow(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setId(rs.getString("person_id"));
        admin.setAdminId(rs.getString("admin_id"));
        admin.setUsername(rs.getString("username"));
        admin.setPassword(rs.getString("password"));
        admin.setNickname(rs.getString("nickname"));
        admin.setPhone(rs.getString("phone"));
        admin.setEmail(rs.getString("email"));
        admin.setRole("ADMIN");
        admin.setPermissionLevel(rs.getInt("permission_level"));
        admin.setDepartment(rs.getString("department"));
        admin.setManagedArea(rs.getString("managed_area"));
        admin.setCanManageUsers(rs.getInt("can_manage_users") == 1);
        admin.setCanManageHeroData(rs.getInt("can_manage_hero_data") == 1);
        admin.setCanManageSystemConfig(rs.getInt("can_manage_system_config") == 1);
        admin.setWorkNumber(rs.getString("work_number"));
        admin.setStatus(rs.getString("status"));
        tryParseDateTime(rs, "create_time").ifPresent(admin::setCreateTime);
        tryParseDateTime(rs, "last_login_time").ifPresent(admin::setLastLoginTime);
        return admin;
    }

    private java.util.Optional<LocalDateTime> tryParseDateTime(ResultSet rs, String column) throws SQLException {
        String val = rs.getString(column);
        if (val != null && !val.isEmpty()) {
            try {
                return java.util.Optional.of(LocalDateTime.parse(val,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } catch (Exception ignored) {
            }
        }
        return java.util.Optional.empty();
    }
}
