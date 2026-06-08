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

    public AuthenticationService() {
        this.users = new ArrayList<>();
        this.adminDao = new AdminDao();
        initializeDefaultUsers();
    }

    public Person login(String username, String password) {
        if (username == null || password == null) {
            log.warn("Login attempt with null credentials.");
            return null;
        }
        // Check in-memory users first (players, fallback)
        for (Person user : users) {
            if (username.equals(user.getUsername())) {
                if (password.equals(user.getPassword())) {
                    if (!"正常".equals(user.getStatus())) {
                        log.warn("User [{}] login blocked: status disabled.", username);
                        return null;
                    }
                    if (user instanceof Admin) {
                        ((Admin) user).setLastLoginTime(LocalDateTime.now());
                    }
                    log.info("User [{}] logged in successfully (in-memory).", username);
                    return user;
                }
                log.warn("Login failed for [{}]: incorrect password.", username);
                return null;
            }
        }

        // Check database for admin users
        Admin dbAdmin = adminDao.findByUsername(username);
        if (dbAdmin != null) {
            if (!password.equals(dbAdmin.getPassword())) {
                log.warn("Login failed for admin [{}]: incorrect password.", username);
                return null;
            }
            if (!"正常".equals(dbAdmin.getStatus())) {
                log.warn("Admin [{}] login blocked: status disabled.", username);
                return null;
            }
            dbAdmin.setLastLoginTime(LocalDateTime.now());
            adminDao.update(dbAdmin);
            log.info("Admin [{}] logged in successfully (database).", username);
            return dbAdmin;
        }

        log.warn("Login failed for [{}]: user not found.", username);
        return null;
    }

    public List<Person> getUsers() {
        return Collections.unmodifiableList(new ArrayList<>(users));
    }

    private void initializeDefaultUsers() {
        // Only seed default admin if the database is empty
        if (adminDao.count() == 0) {
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

    /** Load all admin users from database into the in-memory list for login checks. */
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
