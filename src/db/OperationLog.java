package db;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Model representing an admin operation log entry.
 */
public class OperationLog {
    private Long id;
    private String adminPersonId;
    private String adminUsername;
    private String adminNickname;
    private String operationType;   // ADD, UPDATE, DELETE, QUERY
    private String targetType;      // HERO, EQUIPMENT, TEAM, ADMIN
    private String targetId;
    private String detail;
    private String result;
    private String operationTime;

    public OperationLog() {
    }

    public OperationLog(String adminPersonId, String adminUsername, String adminNickname,
                        String operationType, String targetType, String targetId,
                        String detail, String result) {
        this.adminPersonId = adminPersonId;
        this.adminUsername = adminUsername;
        this.adminNickname = adminNickname;
        this.operationType = operationType;
        this.targetType = targetType;
        this.targetId = targetId;
        this.detail = detail;
        this.result = result;
        this.operationTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // ---- getters / setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAdminPersonId() { return adminPersonId; }
    public void setAdminPersonId(String adminPersonId) { this.adminPersonId = adminPersonId; }

    public String getAdminUsername() { return adminUsername; }
    public void setAdminUsername(String adminUsername) { this.adminUsername = adminUsername; }

    public String getAdminNickname() { return adminNickname; }
    public void setAdminNickname(String adminNickname) { this.adminNickname = adminNickname; }

    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }

    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getOperationTime() { return operationTime; }
    public void setOperationTime(String operationTime) { this.operationTime = operationTime; }

    @Override
    public String toString() {
        return String.format("[%s] %s %s %s(%s) | %s | %s",
                operationTime, adminNickname, operationType, targetType, targetId, detail, result);
    }
}
