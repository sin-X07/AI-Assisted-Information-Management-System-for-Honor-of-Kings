package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Hero {
    private String heroId;
    private String heroName;
    private String title;
    private String position;
    private String heroType;
    private String difficulty;
    private int survivalAbility;
    private int attackAbility;
    private int skillAbility;
    private int supportAbility;
    private String passiveSkill;
    private String skillOne;
    private String skillTwo;
    private String skillThree;
    private List<String> recommendedEquipmentIds;
    private String recommendedSummonerSkill;
    private String description;

    public Hero() {
        this.recommendedEquipmentIds = new ArrayList<>();
    }

    public Hero(String heroId, String heroName, String title, String position, String heroType) {
        this.heroId = heroId;
        this.heroName = heroName;
        this.title = title;
        this.position = position;
        this.heroType = heroType;
        this.recommendedEquipmentIds = new ArrayList<>();
    }

    public Hero(String heroId, String heroName, String title, String position, String heroType,
                String recommendedSummonerSkill, List<String> recommendedEquipmentIds) {
        this.heroId = heroId;
        this.heroName = heroName;
        this.title = title;
        this.position = position;
        this.heroType = heroType;
        this.recommendedSummonerSkill = recommendedSummonerSkill;
        this.recommendedEquipmentIds = recommendedEquipmentIds == null ? new ArrayList<>() : recommendedEquipmentIds;
    }

    public Hero(String heroId, String heroName, String title, String position, String heroType,
                String difficulty, int survivalAbility, int attackAbility, int skillAbility,
                int supportAbility, String passiveSkill, String skillOne, String skillTwo,
                String skillThree, List<String> recommendedEquipmentIds,
                String recommendedSummonerSkill, String description) {
        this.heroId = heroId;
        this.heroName = heroName;
        this.title = title;
        this.position = position;
        this.heroType = heroType;
        this.difficulty = difficulty;
        this.survivalAbility = survivalAbility;
        this.attackAbility = attackAbility;
        this.skillAbility = skillAbility;
        this.supportAbility = supportAbility;
        this.passiveSkill = passiveSkill;
        this.skillOne = skillOne;
        this.skillTwo = skillTwo;
        this.skillThree = skillThree;
        this.recommendedEquipmentIds = recommendedEquipmentIds == null ? new ArrayList<>() : recommendedEquipmentIds;
        this.recommendedSummonerSkill = recommendedSummonerSkill;
        this.description = description;
    }

    public static List<Hero> createDefaultHeroes() {
        List<Hero> heroes = new ArrayList<>();
        heroes.add(new Hero("H001", "李白", "青莲剑仙", "打野", "刺客", "困难", 4, 9, 8, 3,
                "侠客行", "将进酒", "神来之笔", "青莲剑歌",
                Arrays.asList("E005", "E006", "E007"), "惩击", "高机动刺客，适合切入后排。"));
        heroes.add(new Hero("H002", "韩信", "国士无双", "打野", "刺客", "困难", 4, 8, 8, 4,
                "杀意之枪", "无情冲锋", "背水一战", "国士无双",
                Arrays.asList("E005", "E006", "E021"), "惩击", "位移能力强，适合带线和节奏压制。"));
        heroes.add(new Hero("H003", "孙悟空", "齐天大圣", "打野", "刺客/战士", "中等", 5, 9, 6, 3,
                "大圣神威", "护身咒法", "斗战冲锋", "如意金箍",
                Arrays.asList("E001", "E006", "E007"), "惩击", "爆发能力强，依赖进场时机。"));
        heroes.add(new Hero("H004", "赵云", "苍天翔龙", "打野/对抗路", "战士", "中等", 7, 7, 6, 4,
                "龙鸣", "惊雷之龙", "破云之龙", "天翔之龙",
                Arrays.asList("E005", "E006", "E017"), "惩击", "兼具突进、控制和生存能力。"));
        heroes.add(new Hero("H005", "貂蝉", "绝世舞姬", "中路", "法师", "困难", 5, 8, 9, 4,
                "语花印", "落红雨", "缘心结", "绽风华",
                Arrays.asList("E011", "E012", "E013"), "净化", "持续输出型法师，适合团战拉扯。"));
        heroes.add(new Hero("H006", "王昭君", "冰雪之华", "中路", "法师", "中等", 4, 7, 8, 6,
                "冰封之心", "凋零冰晶", "禁锢寒霜", "凛冬已至",
                Arrays.asList("E009", "E010", "E012"), "闪现", "控制能力突出，适合阵地战。"));
        heroes.add(new Hero("H007", "妲己", "魅力之狐", "中路", "法师", "简单", 3, 8, 7, 3,
                "失心", "灵魂冲击", "偶像魅力", "女王崇拜",
                Arrays.asList("E009", "E010", "E011"), "闪现", "单体爆发强，适合蹲草秒人。"));
        heroes.add(new Hero("H008", "小乔", "恋之微风", "中路", "法师", "简单", 3, 8, 7, 4,
                "治愈微笑", "绽放之舞", "甜蜜恋风", "星华缭乱",
                Arrays.asList("E009", "E010", "E014"), "闪现", "消耗和收割能力稳定。"));
        heroes.add(new Hero("H009", "后羿", "半神之弓", "发育路", "射手", "简单", 3, 9, 5, 4,
                "惩戒射击", "多重箭矢", "落日余晖", "灼日之矢",
                Arrays.asList("E001", "E002", "E003"), "闪现", "持续普攻输出高，依赖保护。"));
        heroes.add(new Hero("H010", "鲁班七号", "机关造物", "发育路", "射手", "简单", 2, 9, 5, 3,
                "火力压制", "河豚手雷", "无敌鲨嘴炮", "空中支援",
                Arrays.asList("E001", "E003", "E008"), "闪现", "后期输出极高，生存能力较弱。"));
        heroes.add(new Hero("H011", "孙尚香", "千金重弩", "发育路", "射手", "中等", 4, 9, 6, 3,
                "活力迸发", "翻滚突袭", "红莲爆弹", "究极弩炮",
                Arrays.asList("E001", "E006", "E007"), "闪现", "爆发型射手，适合灵活拉扯。"));
        heroes.add(new Hero("H012", "马可波罗", "远游之枪", "发育路", "射手", "困难", 4, 8, 8, 4,
                "连锁反应", "华丽左轮", "漫游之枪", "狂热弹幕",
                Arrays.asList("E002", "E003", "E008"), "净化", "真实伤害能力强，适合打前排。"));
        heroes.add(new Hero("H013", "吕布", "无双之魔", "对抗路", "战士", "中等", 8, 8, 6, 4,
                "饕餮血统", "方天画斩", "贪狼之握", "魔神降世",
                Arrays.asList("E006", "E015", "E017"), "闪现", "真实伤害和开团能力优秀。"));
        heroes.add(new Hero("H014", "亚瑟", "圣骑之力", "对抗路", "战士/坦克", "简单", 8, 6, 5, 5,
                "圣光守护", "誓约之盾", "回旋打击", "圣剑裁决",
                Arrays.asList("E005", "E016", "E017"), "斩杀", "上手简单，沉默和追击能力稳定。"));
        heroes.add(new Hero("H015", "蔡文姬", "天籁弦音", "游走", "辅助", "简单", 5, 3, 6, 9,
                "长歌行", "思无邪", "胡笳乐", "忘忧曲",
                Arrays.asList("E020", "E022", "E016"), "治疗", "团队回复能力强，适合保护核心输出。"));
        return heroes;
    }

    public String getHeroId() {
        return heroId;
    }

    public void setHeroId(String heroId) {
        this.heroId = heroId;
    }

    public String getHeroName() {
        return heroName;
    }

    public void setHeroName(String heroName) {
        this.heroName = heroName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getHeroType() {
        return heroType;
    }

    public void setHeroType(String heroType) {
        this.heroType = heroType;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getSurvivalAbility() {
        return survivalAbility;
    }

    public void setSurvivalAbility(int survivalAbility) {
        this.survivalAbility = survivalAbility;
    }

    public int getAttackAbility() {
        return attackAbility;
    }

    public void setAttackAbility(int attackAbility) {
        this.attackAbility = attackAbility;
    }

    public int getSkillAbility() {
        return skillAbility;
    }

    public void setSkillAbility(int skillAbility) {
        this.skillAbility = skillAbility;
    }

    public int getSupportAbility() {
        return supportAbility;
    }

    public void setSupportAbility(int supportAbility) {
        this.supportAbility = supportAbility;
    }

    public String getPassiveSkill() {
        return passiveSkill;
    }

    public void setPassiveSkill(String passiveSkill) {
        this.passiveSkill = passiveSkill;
    }

    public String getSkillOne() {
        return skillOne;
    }

    public void setSkillOne(String skillOne) {
        this.skillOne = skillOne;
    }

    public String getSkillTwo() {
        return skillTwo;
    }

    public void setSkillTwo(String skillTwo) {
        this.skillTwo = skillTwo;
    }

    public String getSkillThree() {
        return skillThree;
    }

    public void setSkillThree(String skillThree) {
        this.skillThree = skillThree;
    }

    public List<String> getRecommendedEquipmentIds() {
        return recommendedEquipmentIds;
    }

    public void setRecommendedEquipmentIds(List<String> recommendedEquipmentIds) {
        this.recommendedEquipmentIds = recommendedEquipmentIds == null ? new ArrayList<>() : recommendedEquipmentIds;
    }

    public String getRecommendedSummonerSkill() {
        return recommendedSummonerSkill;
    }

    public void setRecommendedSummonerSkill(String recommendedSummonerSkill) {
        this.recommendedSummonerSkill = recommendedSummonerSkill;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
