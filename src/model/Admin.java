package model;

import java.time.LocalDateTime;

public class Admin extends Person {
    private String adminId;
    private int permissionLevel;
    private String department;
    private String managedArea;
    private LocalDateTime lastLoginTime;
    private boolean canManageUsers;
    private boolean canManageHeroData;
    private boolean canManageSystemConfig;
    private String workNumber;

    public Admin() {
    }

    public Admin(String id, String username, String password, String nickname,
                 String adminId, int permissionLevel) {
        super(id, username, password, nickname, "ADMIN");
        this.adminId = adminId;
        this.permissionLevel = permissionLevel;
    }

    public Admin(String id, String username, String password, String nickname, String phone,
                 String email, String role, LocalDateTime createTime, String status,
                 String adminId, int permissionLevel, String department, String managedArea,
                 LocalDateTime lastLoginTime, boolean canManageUsers, boolean canManageHeroData,
                 boolean canManageSystemConfig, String workNumber) {
        super(id, username, password, nickname, phone, email, role, createTime, status);
        this.adminId = adminId;
        this.permissionLevel = permissionLevel;
        this.department = department;
        this.managedArea = managedArea;
        this.lastLoginTime = lastLoginTime;
        this.canManageUsers = canManageUsers;
        this.canManageHeroData = canManageHeroData;
        this.canManageSystemConfig = canManageSystemConfig;
        this.workNumber = workNumber;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public int getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(int permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getManagedArea() {
        return managedArea;
    }

    public void setManagedArea(String managedArea) {
        this.managedArea = managedArea;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public boolean isCanManageUsers() {
        return canManageUsers;
    }

    public void setCanManageUsers(boolean canManageUsers) {
        this.canManageUsers = canManageUsers;
    }

    public boolean isCanManageHeroData() {
        return canManageHeroData;
    }

    public void setCanManageHeroData(boolean canManageHeroData) {
        this.canManageHeroData = canManageHeroData;
    }

    public boolean isCanManageSystemConfig() {
        return canManageSystemConfig;
    }

    public void setCanManageSystemConfig(boolean canManageSystemConfig) {
        this.canManageSystemConfig = canManageSystemConfig;
    }

    public String getWorkNumber() {
        return workNumber;
    }

    public void setWorkNumber(String workNumber) {
        this.workNumber = workNumber;
    }
}
