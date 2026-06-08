package service;

import db.AdminDao;
import model.Admin;
import model.Person;
import model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final List<Person> users;
    private final AdminDao adminDao;
    private final OperationLogService opLogService;

    public AuthenticationService() {
        this.users = new ArrayList<>();
        this.adminDao = new AdminDao();
        this.opLogService = OperationLogService.getInstance();
        initializeDefaultUsers();
    }

    public Person login(String username, String password) {
        if (username == null || password == null) {
            log.warn("Login attempt with null credentials.");
            opLogService.logFailure(null, "LOGIN", "USER", "",
                    "尝试使用空的用户名或密码登录");
            return null;
        }
        for (Person user : users) {
            if (username.equals(user.getUsername())) {
                if (password.equals(user.getPassword())) {
                    if (!"正常".equals(user.getStatus())) {
                        log.warn("User [{}] login blocked: status disabled.", username);
                        opLogService.logFailure(user, "LOGIN", "USER", user.getId(),
                                "用户 " + username + " 登录被拦截：账号状态异常");
                        return null;
                    }
                    if (user instanceof Admin) {
                        ((Admin) user).setLastLoginTime(LocalDateTime.now());
                    }
                    log.info("User [{}] logged in successfully (in-memory).", username);
                    opLogService.logSuccess(user, "LOGIN", "USER", user.getId(),
                            "用户 " + username + " 登录成功（内存）");
                    return user;
                }
                log.warn("Login failed for [{}]: incorrect password.", username);
                opLogService.logFailure(user, "LOGIN", "USER", user.getId(),
                        "用户 " + username + " 登录失败：密码错误");
                return null;
            }
        }

        Admin dbAdmin = adminDao.findByUsername(username);
        if (dbAdmin != null) {
            if (!password.equals(dbAdmin.getPassword())) {
                log.warn("Login failed for admin [{}]: incorrect password.", username);
                opLogService.logFailure(dbAdmin, "LOGIN", "ADMIN", dbAdmin.getId(),
                        "管理员 " + username + " 登录失败：密码错误");
                return null;
            }
            if (!"正常".equals(dbAdmin.getStatus())) {
                log.warn("Admin [{}] login blocked: status disabled.", username);
                opLogService.logFailure(dbAdmin, "LOGIN", "ADMIN", dbAdmin.getId(),
                        "管理员 " + username + " 登录被拦截：账号状态异常");
                return null;
            }
            dbAdmin.setLastLoginTime(LocalDateTime.now());
            adminDao.update(dbAdmin);
            opLogService.logSuccess(dbAdmin, "LOGIN", "ADMIN", dbAdmin.getId(),
                    "管理员 " + username + " 登录成功（数据库）");
            log.info("Admin [{}] logged in successfully (database).", username);
            return dbAdmin;
        }

        opLogService.logFailure(null, "LOGIN", "USER", "",
                "用户 " + username + " 登录失败：用户不存在");
        log.warn("Login failed for [{}]: user not found.", username);
        return null;
    }

    public List<Person> getUsers() {
        return Collections.unmodifiableList(new ArrayList<>(users));
    }

    private void initializeDefaultUsers() {
        if (adminDao.count() == 0) {
            opLogService.logSuccess(null, "INIT", "ADMIN", "DEFAULT",
                    "系统初始化：创建默认管理员账号 admin");
            Admin admin = new Admin("A001", "admin", "123456", "系统管理员", "13800000000",
                    "admin@example.com", "ADMIN", LocalDateTime.now(), "正常",
                    "ADM001", 2, "系统管理部", "全部数据管理",
                    null, true, true, true, "W001");
            adminDao.insert(admin);
            log.info("Default admin account seeded into database: admin/123456");
        } else {
            log.info("Admin accounts already exist in database, skipping seed.");
        }

        Player player = new Player("P001", "player", "123456", "默认玩家", "13900000000",
                "player@example.com", "PLAYER", LocalDateTime.now(), "正常",
                "G001", "微信区", "王者", 30, "打野",
                "李白", 58.5, 320, 100,
                "上分推荐", "默认测试玩家", null);
        users.add(player);
        log.info("Default player account initialized: player/123456");
    }

    public void loadAdminsFromDatabase() {
        List<Admin> dbAdmins = adminDao.findAll();
        for (Admin a : dbAdmins) {
            boolean exists = users.stream().anyMatch(u -> u.getId().equals(a.getId()));
            if (!exists) {
                users.add(a);
            }
        }
        log.info("Loaded {} admin(s) from database.", dbAdmins.size());
    }
}