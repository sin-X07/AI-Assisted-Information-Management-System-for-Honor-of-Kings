package service;

import model.Admin;
import model.Person;
import model.Player;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuthenticationService {
    private final List<Person> users;

    public AuthenticationService() {
        this.users = new ArrayList<>();
        initializeDefaultUsers();
    }

    public Person login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        for (Person user : users) {
            if (username.equals(user.getUsername()) && password.equals(user.getPassword())) {
                if (!"正常".equals(user.getStatus())) {
                    return null;
                }
                if (user instanceof Admin) {
                    ((Admin) user).setLastLoginTime(LocalDateTime.now());
                }
                return user;
            }
        }
        return null;
    }

    public List<Person> getUsers() {
        return Collections.unmodifiableList(new ArrayList<>(users));
    }

    private void initializeDefaultUsers() {
        Admin admin = new Admin("A001", "admin", "123456", "系统管理员", "13800000000",
                "admin@example.com", "ADMIN", LocalDateTime.now(), "正常",
                "ADM001", 2, "系统管理部", "全部数据管理",
                null, true, true, true, "W001");

        Player player = new Player("P001", "player", "123456", "默认玩家", "13900000000",
                "player@example.com", "PLAYER", LocalDateTime.now(), "正常",
                "G001", "微信区", "王者", 30, "打野",
                "李白", 58.5, 320, 100,
                "上分推荐", "默认测试玩家", null);

        users.add(admin);
        users.add(player);
    }
}
