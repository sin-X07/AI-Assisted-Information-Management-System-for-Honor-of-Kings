package service;

import db.OperationLog;
import db.OperationLogDao;
import model.Admin;
import model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Centralized service for recording admin operation logs.
 * Logs are persisted to SQLite via OperationLogDao.
 */
public class OperationLogService {
    private static final Logger log = LoggerFactory.getLogger(OperationLogService.class);
    private final OperationLogDao logDao;
    private static OperationLogService instance;

    private OperationLogService() {
        this.logDao = new OperationLogDao();
    }

    public static synchronized OperationLogService getInstance() {
        if (instance == null) {
            instance = new OperationLogService();
        }
        return instance;
    }

    /**
     * Record an operation performed by an admin.
     *
     * @param admin   the admin who performed the operation (can be null for system operations)
     * @param optType  ADD / UPDATE / DELETE / QUERY
     * @param targetType HERO / EQUIPMENT / TEAM / ADMIN
     * @param targetId   the ID of the target object
     * @param detail     human-readable description
     * @param result     success / failure message
     */
    public void log(Person admin, String optType, String targetType, String targetId, String detail, String result) {
        String personId = (admin != null) ? admin.getId() : "SYSTEM";
        String username = (admin != null) ? admin.getUsername() : "SYSTEM";
        String nickname = (admin != null) ? admin.getNickname() : "系统";

        OperationLog record = new OperationLog(personId, username, nickname,
                optType, targetType, targetId, detail, result);
        logDao.insert(record);

        // Also log via SLF4J
        log.info("操作日志: [{}] {} - {} {} ({}): {} -> {}",
                record.getOperationTime(), nickname, optType, targetType, targetId, detail, result);
    }

    /** Convenience overload accepting Admin directly. */
    public void log(Admin admin, String optType, String targetType, String targetId, String detail, String result) {
        log((Person) admin, optType, targetType, targetId, detail, result);
    }

    /** Convenience: log a successful operation. */
    public void logSuccess(Person admin, String optType, String targetType, String targetId, String detail) {
        log(admin, optType, targetType, targetId, detail, "成功");
    }

    /** Convenience: log a failed operation. */
    public void logFailure(Person admin, String optType, String targetType, String targetId, String detail) {
        log(admin, optType, targetType, targetId, detail, "失败");
    }

    // ===== Query methods =====

    public List<OperationLog> getAllLogs() {
        return logDao.findAll();
    }

    public List<OperationLog> getLogsByAdmin(String adminPersonId) {
        return logDao.findByAdmin(adminPersonId);
    }

    public List<OperationLog> getLogsByType(String operationType, String targetType) {
        return logDao.findByType(operationType, targetType);
    }

    public long getLogCount() {
        return logDao.count();
    }
}
