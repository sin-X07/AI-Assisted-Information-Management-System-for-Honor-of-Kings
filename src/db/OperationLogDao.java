package db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for operation logs.
 */
public class OperationLogDao {
    private static final Logger log = LoggerFactory.getLogger(OperationLogDao.class);
    private final DatabaseManager dbManager;

    public OperationLogDao() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public void insert(OperationLog record) {
        String sql = """
            INSERT INTO operation_logs (admin_person_id, admin_username, admin_nickname,
                                        operation_type, target_type, target_id, detail,
                                        result, operation_time)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, record.getAdminPersonId());
            ps.setString(2, record.getAdminUsername());
            ps.setString(3, record.getAdminNickname());
            ps.setString(4, record.getOperationType());
            ps.setString(5, record.getTargetType());
            ps.setString(6, record.getTargetId());
            ps.setString(7, record.getDetail());
            ps.setString(8, record.getResult());
            ps.setString(9, record.getOperationTime());
            ps.executeUpdate();
            log.debug("Operation log saved: {}", record);
        } catch (SQLException e) {
            log.error("Failed to insert operation log.", e);
        }
    }

    public List<OperationLog> findAll() {
        List<OperationLog> list = new ArrayList<>();
        String sql = "SELECT * FROM operation_logs ORDER BY operation_time DESC LIMIT 1000";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            log.error("Failed to query operation logs.", e);
        }
        return list;
    }

    public List<OperationLog> findByAdmin(String adminPersonId) {
        List<OperationLog> list = new ArrayList<>();
        String sql = "SELECT * FROM operation_logs WHERE admin_person_id=? ORDER BY operation_time DESC LIMIT 500";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, adminPersonId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Failed to query logs for admin [{}]", adminPersonId, e);
        }
        return list;
    }

    public List<OperationLog> findByType(String operationType, String targetType) {
        List<OperationLog> list = new ArrayList<>();
        String sql = "SELECT * FROM operation_logs WHERE operation_type=? AND target_type=? ORDER BY operation_time DESC LIMIT 500";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, operationType);
            ps.setString(2, targetType);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Failed to query logs by type [{}/{}]", operationType, targetType, e);
        }
        return list;
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM operation_logs";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            log.error("Failed to count operation logs.", e);
        }
        return 0;
    }

    private OperationLog mapRow(ResultSet rs) throws SQLException {
        OperationLog logEntry = new OperationLog();
        logEntry.setId(rs.getLong("id"));
        logEntry.setAdminPersonId(rs.getString("admin_person_id"));
        logEntry.setAdminUsername(rs.getString("admin_username"));
        logEntry.setAdminNickname(rs.getString("admin_nickname"));
        logEntry.setOperationType(rs.getString("operation_type"));
        logEntry.setTargetType(rs.getString("target_type"));
        logEntry.setTargetId(rs.getString("target_id"));
        logEntry.setDetail(rs.getString("detail"));
        logEntry.setResult(rs.getString("result"));
        logEntry.setOperationTime(rs.getString("operation_time"));
        return logEntry;
    }
}
