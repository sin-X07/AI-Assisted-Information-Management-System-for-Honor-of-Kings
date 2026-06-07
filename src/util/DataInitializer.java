package util;

import model.Equipment;
import model.Hero;
import model.MatchParticipant;
import model.MatchRecord;
import model.Player;
import model.Team;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataInitializer {
    private DataInitializer() {
    }

    public static List<Hero> initializeHeroes() {
        List<Hero> heroes = new ArrayList<>();
        heroes.add(new Hero("H001", "李白", "青莲剑仙", "打野", "刺客", "困难", 4, 9, 8, 3,
                "侠客行", "将进酒", "神来之笔", "青莲剑歌",
                Arrays.asList("E005", "E006", "E007"), "惩击", "高机动刺客, 适合切入后排。"));
        heroes.add(new Hero("H002", "韩信", "国士无双", "打野", "刺客", "困难", 4, 8, 8, 4,
                "杀意之枪", "无情冲锋", "背水一战", "国士无双",
                Arrays.asList("E005", "E006", "E021"), "惩击", "位移能力强, 适合带线和节奏压制。"));
        heroes.add(new Hero("H003", "孙悟空", "齐天大圣", "打野", "刺客/战士", "中等", 5, 9, 6, 3,
                "大圣神威", "护身咒法", "斗战冲锋", "如意金箍",
                Arrays.asList("E001", "E006", "E007"), "惩击", "爆发能力强, 依赖进场时机。"));
        heroes.add(new Hero("H004", "赵云", "苍天翔龙", "打野/对抗路", "战士", "中等", 7, 7, 6, 4,
                "龙鸣", "惊雷之龙", "破云之龙", "天翔之龙",
                Arrays.asList("E005", "E006", "E017"), "惩击", "兼具突进、控制和生存能力。"));
        heroes.add(new Hero("H005", "貂蝉", "绝世舞姬", "中路", "法师", "困难", 5, 8, 9, 4,
                "语花印", "落红雨", "缘心结", "绽风华",
                Arrays.asList("E011", "E012", "E013"), "净化", "持续输出型法师, 适合团战拉扯。"));
        heroes.add(new Hero("H006", "王昭君", "冰雪之华", "中路", "法师", "中等", 4, 7, 8, 6,
                "冰封之心", "凋零冰晶", "禁锢寒霜", "凛冬已至",
                Arrays.asList("E009", "E010", "E012"), "闪现", "控制能力突出, 适合阵地战。"));
        heroes.add(new Hero("H007", "妲己", "魅力之狐", "中路", "法师", "简单", 3, 8, 7, 3,
                "失心", "灵魂冲击", "偶像魅力", "女王崇拜",
                Arrays.asList("E009", "E010", "E011"), "闪现", "单体爆发强, 适合蹲草秒人。"));
        heroes.add(new Hero("H008", "小乔", "恋之微风", "中路", "法师", "简单", 3, 8, 7, 4,
                "治愈微笑", "绽放之舞", "甜蜜恋风", "星华缭乱",
                Arrays.asList("E009", "E010", "E014"), "闪现", "消耗和收割能力稳定。"));
        heroes.add(new Hero("H009", "后羿", "半神之弓", "发育路", "射手", "简单", 3, 9, 5, 4,
                "惩戒射击", "多重箭矢", "落日余晖", "灼日之矢",
                Arrays.asList("E001", "E002", "E003"), "闪现", "持续普攻输出高, 依赖保护。"));
        heroes.add(new Hero("H010", "鲁班七号", "机关造物", "发育路", "射手", "简单", 2, 9, 5, 3,
                "火力压制", "河豚手雷", "无敌鲨嘴炮", "空中支援",
                Arrays.asList("E001", "E003", "E008"), "闪现", "后期输出极高, 生存能力较弱。"));
        heroes.add(new Hero("H011", "孙尚香", "千金重弩", "发育路", "射手", "中等", 4, 9, 6, 3,
                "活力迸发", "翻滚突袭", "红莲爆弹", "究极弩炮",
                Arrays.asList("E001", "E006", "E007"), "闪现", "爆发型射手, 适合灵活拉扯。"));
        heroes.add(new Hero("H012", "马可波罗", "远游之枪", "发育路", "射手", "困难", 4, 8, 8, 4,
                "连锁反应", "华丽左轮", "漫游之枪", "狂热弹幕",
                Arrays.asList("E002", "E003", "E008"), "净化", "真实伤害能力强, 适合打前排。"));
        heroes.add(new Hero("H013", "吕布", "无双之魔", "对抗路", "战士", "中等", 8, 8, 6, 4,
                "饕餮血统", "方天画斩", "贪狼之握", "魔神降世",
                Arrays.asList("E006", "E015", "E017"), "闪现", "真实伤害和开团能力优秀。"));
        heroes.add(new Hero("H014", "亚瑟", "圣骑之力", "对抗路", "战士/坦克", "简单", 8, 6, 5, 5,
                "圣光守护", "誓约之盾", "回旋打击", "圣剑裁决",
                Arrays.asList("E005", "E016", "E017"), "斩杀", "上手简单, 沉默和追击能力稳定。"));
        heroes.add(new Hero("H015", "蔡文姬", "天籁弦音", "游走", "辅助", "简单", 5, 3, 6, 9,
                "长歌行", "思无邪", "胡笳乐", "忘忧曲",
                Arrays.asList("E020", "E022", "E016"), "治疗", "团队回复能力强, 适合保护核心输出。"));
        return heroes;
    }

    public static List<Equipment> initializeEquipments() {
        List<Equipment> equipments = new ArrayList<>();
        equipments.add(new Equipment("E001", "无尽战刃", "攻击", 2140, 110, 0, 0, 0, 0, 0, 0, 20, 0,
                "提升暴击效果。", Arrays.asList("射手", "刺客"), "适合依赖暴击输出的英雄。"));
        equipments.add(new Equipment("E002", "影刃", "攻击", 1950, 35, 0, 0, 0, 0, 0, 0, 25, 5,
                "暴击后提升攻速和移速。", Arrays.asList("射手"), "适合持续普攻输出。"));
        equipments.add(new Equipment("E003", "破晓", "攻击", 3400, 50, 0, 0, 0, 0, 0, 0, 10, 0,
                "提升物理穿透。", Arrays.asList("射手"), "射手后期核心装备。"));
        equipments.add(new Equipment("E004", "泣血之刃", "攻击", 1740, 100, 0, 0, 0, 0, 0, 0, 0, 0,
                "提供物理吸血。", Arrays.asList("射手", "刺客"), "增强续航能力。"));
        equipments.add(new Equipment("E005", "暗影战斧", "攻击", 2090, 85, 0, 500, 0, 0, 0, 15, 0, 0,
                "提升冷却并附带物理穿透。", Arrays.asList("战士", "刺客"), "战士和刺客常用前中期装备。"));
        equipments.add(new Equipment("E006", "破军", "攻击", 2950, 180, 0, 0, 0, 0, 0, 0, 0, 0,
                "对低生命目标造成更高伤害。", Arrays.asList("战士", "刺客"), "适合收割型英雄。"));
        equipments.add(new Equipment("E007", "宗师之力", "攻击", 2100, 80, 0, 500, 500, 0, 0, 0, 20, 0,
                "使用技能后强化普攻。", Arrays.asList("刺客", "战士"), "适合技能衔接普攻的英雄。"));
        equipments.add(new Equipment("E008", "闪电匕首", "攻击", 1840, 0, 0, 0, 0, 0, 0, 0, 15, 8,
                "普攻可触发连锁闪电。", Arrays.asList("射手"), "提升攻速、暴击和清线能力。"));
        equipments.add(new Equipment("E009", "博学者之怒", "法术", 2300, 0, 240, 0, 0, 0, 0, 0, 0, 0,
                "大幅提升法术攻击。", Arrays.asList("法师"), "法师爆发核心装备。"));
        equipments.add(new Equipment("E010", "回响之杖", "法术", 2100, 0, 240, 0, 0, 0, 0, 0, 0, 7,
                "技能命中造成小范围爆炸。", Arrays.asList("法师"), "适合消耗和爆发法师。"));
        equipments.add(new Equipment("E011", "虚无法杖", "法术", 2110, 0, 240, 500, 0, 0, 0, 0, 0, 0,
                "提升法术穿透。", Arrays.asList("法师"), "应对高法术防御目标。"));
        equipments.add(new Equipment("E012", "痛苦面具", "法术", 2040, 0, 120, 800, 0, 0, 0, 5, 0, 0,
                "技能命中附带持续伤害。", Arrays.asList("法师"), "适合持续消耗型法师。"));
        equipments.add(new Equipment("E013", "噬神之书", "法术", 2090, 0, 180, 800, 0, 0, 0, 10, 0, 0,
                "提供法术吸血。", Arrays.asList("法师"), "增强法师续航。"));
        equipments.add(new Equipment("E014", "贤者之书", "法术", 2990, 0, 400, 0, 0, 0, 0, 0, 0, 0,
                "提供高额法术攻击。", Arrays.asList("法师"), "后期法强装备。"));
        equipments.add(new Equipment("E015", "红莲斗篷", "防御", 1800, 0, 0, 1000, 0, 240, 0, 0, 0, 0,
                "对周围敌人造成持续伤害。", Arrays.asList("坦克", "战士"), "适合近身承伤英雄。"));
        equipments.add(new Equipment("E016", "不祥征兆", "防御", 2180, 0, 0, 1200, 0, 270, 0, 0, 0, 0,
                "受到攻击时降低攻击者攻速和移速。", Arrays.asList("坦克", "辅助"), "克制普攻型英雄。"));
        equipments.add(new Equipment("E017", "魔女斗篷", "防御", 2080, 0, 0, 1000, 0, 0, 200, 0, 0, 0,
                "获得抵挡法术伤害的护盾。", Arrays.asList("坦克", "战士"), "应对高法术伤害阵容。"));
        equipments.add(new Equipment("E018", "反伤刺甲", "防御", 1950, 30, 0, 0, 0, 360, 0, 0, 0, 0,
                "反弹部分受到的物理伤害。", Arrays.asList("坦克", "战士"), "克制物理输出英雄。"));
        equipments.add(new Equipment("E019", "抵抗之靴", "移动", 710, 0, 0, 0, 0, 0, 120, 0, 0, 60,
                "提升韧性。", Arrays.asList("通用"), "减少被控制时间。"));
        equipments.add(new Equipment("E020", "冷静之靴", "移动", 710, 0, 0, 0, 0, 0, 0, 15, 0, 60,
                "提升冷却缩减。", Arrays.asList("法师", "辅助"), "适合依赖技能频率的英雄。"));
        equipments.add(new Equipment("E021", "贪婪之噬", "打野", 2160, 60, 0, 0, 0, 0, 0, 0, 0, 8,
                "提升打野效率并随层数成长。", Arrays.asList("刺客", "战士"), "物理打野英雄常用装备。"));
        equipments.add(new Equipment("E022", "极影", "辅助", 1910, 0, 0, 1200, 0, 0, 0, 10, 0, 5,
                "为附近队友提供攻速和冷却收益。", Arrays.asList("游走", "辅助"), "适合团队增益型辅助。"));
        return equipments;
    }

    public static List<Team> initializeTeams() {
        List<Team> teams = new ArrayList<>();
        teams.add(new Team("T001", "重庆狼队", "Wolves", "重庆",
                "林", "Fly", Arrays.asList("Fly", "小胖", "向鱼", "妖刀", "一笙"),
                Arrays.asList("吕布", "赵云", "王昭君", "孙尚香", "蔡文姬"),
                Arrays.asList("KPL知名战队", "多次获得职业赛事冠军"), 0.72, 120,
                "KPL知名强队, 团队运营和团战能力突出。", LocalDateTime.now(), "正常"));
        teams.add(new Team("T002", "成都AG超玩会", "AG", "成都",
                "奶茶", "一诺", Arrays.asList("一诺", "长生", "轩染", "钟意", "Cat"),
                Arrays.asList("孙尚香", "马可波罗", "小乔", "亚瑟", "蔡文姬"),
                Arrays.asList("KPL人气战队", "职业赛事冠军队伍"), 0.68, 115,
                "KPL高人气战队, 选手个人能力和话题度较高。", LocalDateTime.now(), "正常"));
        teams.add(new Team("T003", "武汉eStarPro", "eStarPro", "武汉",
                "SK", "花海", Arrays.asList("花海", "清融", "坦然", "易峥", "子阳"),
                Arrays.asList("韩信", "貂蝉", "吕布", "后羿", "蔡文姬"),
                Arrays.asList("KPL知名战队", "多冠战队代表"), 0.74, 130,
                "KPL冠军战队代表, 体系成熟且执行力强。", LocalDateTime.now(), "正常"));
        return teams;
    }

    public static List<Player> initializePlayers() {
        List<Player> players = new ArrayList<>();

        // T001 重庆狼队
        players.add(createPlayer("Fly", "Fly", 12, 9));
        players.add(createPlayer("小胖", "小胖", 11, 7));
        players.add(createPlayer("向鱼", "向鱼", 10, 7));
        players.add(createPlayer("妖刀", "妖刀", 12, 8));
        players.add(createPlayer("一笙", "一笙", 9, 5));

        // T002 成都AG超玩会
        players.add(createPlayer("一诺", "一诺", 14, 11));
        players.add(createPlayer("长生", "长生", 10, 8));
        players.add(createPlayer("轩染", "轩染", 8, 4));
        players.add(createPlayer("钟意", "钟意", 11, 8));
        players.add(createPlayer("Cat", "Cat", 13, 10));

        // T003 武汉eStarPro
        players.add(createPlayer("花海", "花海", 15, 12));
        players.add(createPlayer("清融", "清融", 12, 7));
        players.add(createPlayer("坦然", "坦然", 10, 6));
        players.add(createPlayer("易峥", "易峥", 11, 6));
        players.add(createPlayer("子阳", "子阳", 9, 5));

        return players;
    }

    private static Player createPlayer(String id, String nickname, int totalMatches, int wins) {
        Player player = new Player(id, id, "", nickname);
        player.setNickname(nickname);
        List<MatchRecord> records = new ArrayList<>();
        for (int i = 0; i < totalMatches; i++) {
            String result = i < wins ? "胜利" : "失败";
            records.add(buildMatchRecord(id, nickname, i + 1, result));
        }
        player.setMatchOverviews(records);
        return player;
    }

    private static MatchRecord buildMatchRecord(String playerId, String playerName, int index, String result) {
        String matchId = "M" + String.format("%03d", index);
        LocalDateTime matchTime = LocalDateTime.now().minusDays(index * 3L);
        int durationSeconds = 900 + (index % 9) * 100;

        List<MatchParticipant> participants = new ArrayList<>();
        participants.add(new MatchParticipant(playerId, playerName, "亚瑟", "蓝方",
                5 + index % 8, 2 + index % 5, 6 + index % 7));
        participants.add(new MatchParticipant("OPP" + index, "对手" + index, "后羿", "红方",
                2 + index % 4, 4 + index % 6, 3 + index % 5));

        return new MatchRecord(matchId, matchTime, "排位赛", result, durationSeconds, participants);
    }
}