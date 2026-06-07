import model.Equipment;
import model.Hero;
import model.Person;
import model.Team;
import service.AuthenticationService;
import service.FileStorageService;
import service.GameDataManager;
import service.RankingService;
import util.InputHelper;

public class Main {
    private static final InputHelper inputHelper = new InputHelper();
    private static final AuthenticationService authenticationService = new AuthenticationService();
    private static final GameDataManager gameDataManager = new GameDataManager();
    private static final RankingService rankingService = new RankingService();
    private static final FileStorageService fileStorageService = new FileStorageService();

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
                System.out.println("登录成功，欢迎" + user.getNickname() + "！");
                return user;
            }
            System.out.println("用户名或密码错误，或账号状态不可用，请重试。");
        }
    }

    private static void mainMenuLoop(Person currentUser) {
        boolean loggedIn = true;
        boolean isAdmin = "ADMIN".equals(currentUser.getRole());
        while (loggedIn) {
            printMainMenu(currentUser);
            int choice = inputHelper.readIntInRange("请选择功能：", 0, isAdmin ? 10 : 8);
            switch (choice) {
                case 1: showHeroes(); break;
                case 2: showEquipments(); break;
                case 3: showTeams(); break;
                case 4: searchHero(); break;
                case 5: searchEquipment(); break;
                case 6: searchTeam(); break;
                case 7: queryPlayerDetails(); break;
                case 8: showPlayerRanking(); break;
                case 9: dataManagementMenu(); break;
                case 10: exportRanking(); break;
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
        System.out.println("7. 玩家详情查询（级联）");
        System.out.println("8. 玩家荣誉排行榜");
        if ("ADMIN".equals(currentUser.getRole())) {
            System.out.println("9. 数据管理（增删改）");
            System.out.println("10. 导出排行榜到文件");
        }
        System.out.println("0. 退出登录");
    }

    private static void dataManagementMenu() {
        while (true) {
            System.out.println();
            System.out.println("===== 数据管理（增删改） =====");
            System.out.println("1. 添加英雄    2. 添加装备    3. 添加战队");
            System.out.println("4. 删除英雄    5. 删除装备    6. 删除战队");
            System.out.println("7. 更新英雄    8. 更新装备    9. 更新战队");
            System.out.println("0. 返回主菜单");
            int choice = inputHelper.readIntInRange("请选择操作：", 0, 9);
            switch (choice) {
                case 1: addHero(); break;
                case 2: addEquipment(); break;
                case 3: addTeam(); break;
                case 4: removeHero(); break;
                case 5: removeEquipment(); break;
                case 6: removeTeam(); break;
                case 7: updateHero(); break;
                case 8: updateEquipment(); break;
                case 9: updateTeam(); break;
                case 0: return;
            }
        }
    }

    private static void addHero() {
        System.out.println();
        System.out.println("----- 添加英雄 -----");
        String heroId = inputHelper.readRequiredString("英雄编号：");
        if (gameDataManager.findHeroById(heroId) != null) {
            System.out.println("该编号已存在，添加失败。");
            return;
        }
        String heroName = inputHelper.readRequiredString("英雄名称：");
        String title = inputHelper.readRequiredString("称号：");
        String position = inputHelper.readRequiredString("定位：");
        String heroType = inputHelper.readRequiredString("类型（刺客/法师/战士/射手/辅助/坦克）：");
        Hero hero = new Hero(heroId, heroName, title, position, heroType);
        gameDataManager.addHero(hero);
        System.out.println("英雄 [" + heroName + "] 添加成功。");
    }

    private static void addEquipment() {
        System.out.println();
        System.out.println("----- 添加装备 -----");
        String eqId = inputHelper.readRequiredString("装备编号：");
        if (gameDataManager.findEquipmentById(eqId) != null) {
            System.out.println("该编号已存在，添加失败。");
            return;
        }
        String eqName = inputHelper.readRequiredString("装备名称：");
        String eqType = inputHelper.readRequiredString("装备类型（攻击/法术/防御/移动/打野/辅助）：");
        int price = inputHelper.readInt("价格（金币）：");
        Equipment eq = new Equipment(eqId, eqName, eqType, price);
        gameDataManager.addEquipment(eq);
        System.out.println("装备 [" + eqName + "] 添加成功。");
    }

    private static void addTeam() {
        System.out.println();
        System.out.println("----- 添加战队 -----");
        String teamId = inputHelper.readRequiredString("战队编号：");
        if (gameDataManager.findTeamById(teamId) != null) {
            System.out.println("该编号已存在，添加失败。");
            return;
        }
        String teamName = inputHelper.readRequiredString("战队名称：");
        String shortName = inputHelper.readRequiredString("简称：");
        String region = inputHelper.readRequiredString("所属地区：");
        Team team = new Team(teamId, teamName, shortName, region);
        gameDataManager.addTeam(team);
        System.out.println("战队 [" + teamName + "] 添加成功。");
    }

    private static void removeHero() {
        System.out.println();
        System.out.println("----- 删除英雄 -----");
        String id = inputHelper.readRequiredString("请输入要删除的英雄编号：");
        boolean ok = gameDataManager.removeHeroById(id);
        System.out.println(ok ? "删除成功。" : "未找到编号为 " + id + " 的英雄。");
    }

    private static void removeEquipment() {
        System.out.println();
        System.out.println("----- 删除装备 -----");
        String id = inputHelper.readRequiredString("请输入要删除的装备编号：");
        boolean ok = gameDataManager.removeEquipmentById(id);
        System.out.println(ok ? "删除成功。" : "未找到编号为 " + id + " 的装备。");
    }

    private static void removeTeam() {
        System.out.println();
        System.out.println("----- 删除战队 -----");
        String id = inputHelper.readRequiredString("请输入要删除的战队编号：");
        boolean ok = gameDataManager.removeTeamById(id);
        System.out.println(ok ? "删除成功。" : "未找到编号为 " + id + " 的战队。");
    }

    private static void updateHero() {
        System.out.println();
        System.out.println("----- 更新英雄 -----");
        String id = inputHelper.readRequiredString("请输入要更新的英雄编号：");
        Hero old = gameDataManager.findHeroById(id);
        if (old == null) {
            System.out.println("未找到编号为 " + id + " 的英雄。");
            return;
        }
        System.out.println("（直接回车保留原值）");
        String name = inputLineWithDefault("英雄名称 [" + old.getHeroName() + "]：", old.getHeroName());
        String title = inputLineWithDefault("称号 [" + old.getTitle() + "]：", old.getTitle());
        String position = inputLineWithDefault("定位 [" + old.getPosition() + "]：", old.getPosition());
        String heroType = inputLineWithDefault("类型 [" + old.getHeroType() + "]：", old.getHeroType());
        String diff = inputLineWithDefault("难度 [" + old.getDifficulty() + "]：", old.getDifficulty());
        String summoner = inputLineWithDefault("推荐召唤师技能 [" + old.getRecommendedSummonerSkill() + "]：", old.getRecommendedSummonerSkill());
        String desc = inputLineWithDefault("简介 [" + old.getDescription() + "]：", old.getDescription());

        Hero updated = new Hero();
        updated.setHeroId(old.getHeroId());
        updated.setHeroName(name);
        updated.setTitle(title);
        updated.setPosition(position);
        updated.setHeroType(heroType);
        updated.setDifficulty(diff);
        updated.setRecommendedSummonerSkill(summoner);
        updated.setDescription(desc);

        boolean ok = gameDataManager.updateHero(updated);
        System.out.println(ok ? "更新成功。" : "更新失败。");
    }

    private static void updateEquipment() {
        System.out.println();
        System.out.println("----- 更新装备 -----");
        String id = inputHelper.readRequiredString("请输入要更新的装备编号：");
        Equipment old = gameDataManager.findEquipmentById(id);
        if (old == null) {
            System.out.println("未找到编号为 " + id + " 的装备。");
            return;
        }
        System.out.println("（直接回车保留原值）");
        String name = inputLineWithDefault("装备名称 [" + old.getEquipmentName() + "]：", old.getEquipmentName());
        String type = inputLineWithDefault("类型 [" + old.getEquipmentType() + "]：", old.getEquipmentType());
        int price = inputIntWithDefault("价格 [" + old.getPrice() + "]：", old.getPrice());
        String passive = inputLineWithDefault("被动效果 [" + old.getPassiveEffect() + "]：", old.getPassiveEffect());
        String desc = inputLineWithDefault("说明 [" + old.getDescription() + "]：", old.getDescription());

        Equipment updated = new Equipment();
        updated.setEquipmentId(old.getEquipmentId());
        updated.setEquipmentName(name);
        updated.setEquipmentType(type);
        updated.setPrice(price);
        updated.setPassiveEffect(passive);
        updated.setDescription(desc);

        boolean ok = gameDataManager.updateEquipment(updated);
        System.out.println(ok ? "更新成功。" : "更新失败。");
    }

    private static void updateTeam() {
        System.out.println();
        System.out.println("----- 更新战队 -----");
        String id = inputHelper.readRequiredString("请输入要更新的战队编号：");
        Team old = gameDataManager.findTeamById(id);
        if (old == null) {
            System.out.println("未找到编号为 " + id + " 的战队。");
            return;
        }
        System.out.println("（直接回车保留原值）");
        String name = inputLineWithDefault("战队名称 [" + old.getTeamName() + "]：", old.getTeamName());
        String shortName = inputLineWithDefault("简称 [" + old.getShortName() + "]：", old.getShortName());
        String region = inputLineWithDefault("地区 [" + old.getRegion() + "]：", old.getRegion());
        String coach = inputLineWithDefault("教练 [" + old.getCoachName() + "]：", old.getCoachName());
        String captain = inputLineWithDefault("队长 [" + old.getCaptainName() + "]：", old.getCaptainName());
        String desc = inputLineWithDefault("简介 [" + old.getDescription() + "]：", old.getDescription());

        Team updated = new Team();
        updated.setTeamId(old.getTeamId());
        updated.setTeamName(name);
        updated.setShortName(shortName);
        updated.setRegion(region);
        updated.setCoachName(coach);
        updated.setCaptainName(captain);
        updated.setDescription(desc);

        boolean ok = gameDataManager.updateTeam(updated);
        System.out.println(ok ? "更新成功。" : "更新失败。");
    }

    private static void queryPlayerDetails() {
        System.out.println();
        System.out.println("===== 玩家详情查询（级联） =====");
        String id = inputHelper.readRequiredString("请输入玩家ID（或昵称）：");
        gameDataManager.displayPlayerDetails(id);
    }

    private static void showPlayerRanking() {
        System.out.println();
        System.out.println("===== 玩家荣誉排行榜 =====");
        int topN = inputHelper.readInt("请输入显示前多少名：");
        rankingService.displayTopPlayers(gameDataManager, topN);
    }

    private static void exportRanking() {
        System.out.println();
        System.out.println("===== 导出排行榜到文件 =====");
        String filePath = inputHelper.readRequiredString("请输入文件路径（如 ranking.txt）：");
        fileStorageService.exportRankingToFile(gameDataManager, filePath);
    }

    private static String inputLineWithDefault(String prompt, String defaultValue) {
        String input = inputHelper.readLine(prompt);
        if (input.isEmpty()) {
            return defaultValue;
        }
        return input;
    }

    private static int inputIntWithDefault(String prompt, int defaultValue) {
        String input = inputHelper.readLine(prompt);
        if (input.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("输入无效，保留原值。");
            return defaultValue;
        }
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
        System.out.println(hero.getHeroName() + "：" + hero.getTitle() + "；");
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
        System.out.println(team.getTeamName() + "：" + team.getShortName() + "；");
        System.out.println("地区：" + team.getRegion() + "，教练：" + team.getCoachName()
                + "，队长：" + team.getCaptainName());
        System.out.println("简介：" + team.getDescription());
    }
}