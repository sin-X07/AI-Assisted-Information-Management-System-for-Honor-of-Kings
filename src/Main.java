import model.Equipment;
import model.Hero;
import model.Person;
import model.Team;
import service.AuthenticationService;
import service.GameDataManager;
import util.InputHelper;

public class Main {
    private static final InputHelper inputHelper = new InputHelper();
    private static final AuthenticationService authenticationService = new AuthenticationService();
    private static final GameDataManager gameDataManager = new GameDataManager();

    public static void main(String[] args) {
        System.out.println("欢迎使用 AI 辅助的《王者荣耀》信息管理系统");
        while (true) {
            Person currentUser = loginLoop();
            mainMenuLoop(currentUser);
        }
    }

    private static Person loginLoop() {
        while (true) {
            System.out.println();
            System.out.println("===== 用户登录 =====");
            System.out.println("默认管理员：admin / 123456");
            System.out.println("默认玩家：player / 123456");
            String username = inputHelper.readRequiredString("请输入用户名：");
            String password = inputHelper.readRequiredString("请输入密码：");
            Person user = authenticationService.login(username, password);
            if (user != null) {
                System.out.println("登录成功，欢迎 " + user.getNickname() + "！");
                return user;
            }
            System.out.println("用户名或密码错误，或账号状态不可用，请重试。");
        }
    }

    private static void mainMenuLoop(Person currentUser) {
        boolean loggedIn = true;
        while (loggedIn) {
            printMainMenu(currentUser);
            int choice = inputHelper.readIntInRange("请选择功能：", 0, 6);
            switch (choice) {
                case 1:
                    showHeroes();
                    break;
                case 2:
                    showEquipments();
                    break;
                case 3:
                    showTeams();
                    break;
                case 4:
                    searchHero();
                    break;
                case 5:
                    searchEquipment();
                    break;
                case 6:
                    searchTeam();
                    break;
                case 0:
                    loggedIn = false;
                    System.out.println("已退出当前账号。");
                    break;
                default:
                    System.out.println("未知选项，请重新选择。");
            }
        }
    }

    private static void printMainMenu(Person currentUser) {
        System.out.println();
        System.out.println("===== 主菜单 [" + currentUser.getRole() + "] =====");
        System.out.println("1. 查看英雄列表");
        System.out.println("2. 查看装备列表");
        System.out.println("3. 查看战队列表");
        System.out.println("4. 查询英雄");
        System.out.println("5. 查询装备");
        System.out.println("6. 查询战队");
        System.out.println("0. 退出登录");
    }

    private static void showHeroes() {
        System.out.println();
        System.out.println("===== 英雄列表 =====");
        for (Hero hero : gameDataManager.getHeroes()) {
            System.out.println(hero.getHeroId() + " | " + hero.getHeroName()
                    + " | " + hero.getTitle() + " | " + hero.getPosition()
                    + " | " + hero.getHeroType());
        }
    }

    private static void showEquipments() {
        System.out.println();
        System.out.println("===== 装备列表 =====");
        for (Equipment equipment : gameDataManager.getEquipments()) {
            System.out.println(equipment.getEquipmentId() + " | " + equipment.getEquipmentName()
                    + " | " + equipment.getEquipmentType() + " | " + equipment.getPrice() + "金币");
        }
    }

    private static void showTeams() {
        System.out.println();
        System.out.println("===== 战队列表 =====");
        for (Team team : gameDataManager.getTeams()) {
            System.out.println(team.getTeamId() + " | " + team.getTeamName()
                    + " | " + team.getShortName() + " | " + team.getRegion()
                    + " | 胜率：" + team.getWinRate());
        }
    }

    private static void searchHero() {
        String keyword = inputHelper.readRequiredString("请输入英雄名称或编号：");
        Hero hero = gameDataManager.findHeroByName(keyword);
        if (hero == null) {
            hero = gameDataManager.findHeroById(keyword);
        }
        if (hero == null) {
            System.out.println("未找到该英雄。");
            return;
        }
        System.out.println(hero.getHeroName() + "（" + hero.getTitle() + "）");
        System.out.println("定位：" + hero.getPosition() + "，类型：" + hero.getHeroType());
        System.out.println("推荐召唤师技能：" + hero.getRecommendedSummonerSkill());
        System.out.println("简介：" + hero.getDescription());
    }

    private static void searchEquipment() {
        String keyword = inputHelper.readRequiredString("请输入装备名称或编号：");
        Equipment equipment = gameDataManager.findEquipmentByName(keyword);
        if (equipment == null) {
            equipment = gameDataManager.findEquipmentById(keyword);
        }
        if (equipment == null) {
            System.out.println("未找到该装备。");
            return;
        }
        System.out.println(equipment.getEquipmentName() + " | " + equipment.getEquipmentType()
                + " | " + equipment.getPrice() + "金币");
        System.out.println("被动效果：" + equipment.getPassiveEffect());
        System.out.println("说明：" + equipment.getDescription());
    }

    private static void searchTeam() {
        String keyword = inputHelper.readRequiredString("请输入战队名称或编号：");
        Team team = gameDataManager.findTeamByName(keyword);
        if (team == null) {
            team = gameDataManager.findTeamById(keyword);
        }
        if (team == null) {
            System.out.println("未找到该战队。");
            return;
        }
        System.out.println(team.getTeamName() + "（" + team.getShortName() + "）");
        System.out.println("地区：" + team.getRegion() + "，教练：" + team.getCoachName()
                + "，队长：" + team.getCaptainName());
        System.out.println("简介：" + team.getDescription());
    }
}
