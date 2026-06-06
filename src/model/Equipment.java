package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Equipment {
    private String equipmentId;
    private String equipmentName;
    private String equipmentType;
    private int price;
    private int attackBonus;
    private int magicAttackBonus;
    private int healthBonus;
    private int manaBonus;
    private int armorBonus;
    private int magicResistanceBonus;
    private double cooldownReduction;
    private double criticalRate;
    private double movementSpeed;
    private String passiveEffect;
    private List<String> suitableHeroTypes;
    private String description;

    public Equipment() {
        this.suitableHeroTypes = new ArrayList<>();
    }

    public Equipment(String equipmentId, String equipmentName, String equipmentType, int price) {
        this.equipmentId = equipmentId;
        this.equipmentName = equipmentName;
        this.equipmentType = equipmentType;
        this.price = price;
        this.suitableHeroTypes = new ArrayList<>();
    }

    public Equipment(String equipmentId, String equipmentName, String equipmentType, int price,
                     int attackBonus, int magicAttackBonus, int healthBonus, int manaBonus,
                     int armorBonus, int magicResistanceBonus) {
        this(equipmentId, equipmentName, equipmentType, price);
        this.attackBonus = attackBonus;
        this.magicAttackBonus = magicAttackBonus;
        this.healthBonus = healthBonus;
        this.manaBonus = manaBonus;
        this.armorBonus = armorBonus;
        this.magicResistanceBonus = magicResistanceBonus;
    }

    public Equipment(String equipmentId, String equipmentName, String equipmentType, int price,
                     List<String> suitableHeroTypes, String passiveEffect) {
        this(equipmentId, equipmentName, equipmentType, price);
        this.suitableHeroTypes = suitableHeroTypes == null ? new ArrayList<>() : suitableHeroTypes;
        this.passiveEffect = passiveEffect;
    }

    public Equipment(String equipmentId, String equipmentName, String equipmentType, int price,
                     int attackBonus, int magicAttackBonus, int healthBonus, int manaBonus,
                     int armorBonus, int magicResistanceBonus, double cooldownReduction,
                     double criticalRate, double movementSpeed, String passiveEffect,
                     List<String> suitableHeroTypes, String description) {
        this.equipmentId = equipmentId;
        this.equipmentName = equipmentName;
        this.equipmentType = equipmentType;
        this.price = price;
        this.attackBonus = attackBonus;
        this.magicAttackBonus = magicAttackBonus;
        this.healthBonus = healthBonus;
        this.manaBonus = manaBonus;
        this.armorBonus = armorBonus;
        this.magicResistanceBonus = magicResistanceBonus;
        this.cooldownReduction = cooldownReduction;
        this.criticalRate = criticalRate;
        this.movementSpeed = movementSpeed;
        this.passiveEffect = passiveEffect;
        this.suitableHeroTypes = suitableHeroTypes == null ? new ArrayList<>() : suitableHeroTypes;
        this.description = description;
    }

    public static List<Equipment> createDefaultEquipments() {
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

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getAttackBonus() {
        return attackBonus;
    }

    public void setAttackBonus(int attackBonus) {
        this.attackBonus = attackBonus;
    }

    public int getMagicAttackBonus() {
        return magicAttackBonus;
    }

    public void setMagicAttackBonus(int magicAttackBonus) {
        this.magicAttackBonus = magicAttackBonus;
    }

    public int getHealthBonus() {
        return healthBonus;
    }

    public void setHealthBonus(int healthBonus) {
        this.healthBonus = healthBonus;
    }

    public int getManaBonus() {
        return manaBonus;
    }

    public void setManaBonus(int manaBonus) {
        this.manaBonus = manaBonus;
    }

    public int getArmorBonus() {
        return armorBonus;
    }

    public void setArmorBonus(int armorBonus) {
        this.armorBonus = armorBonus;
    }

    public int getMagicResistanceBonus() {
        return magicResistanceBonus;
    }

    public void setMagicResistanceBonus(int magicResistanceBonus) {
        this.magicResistanceBonus = magicResistanceBonus;
    }

    public double getCooldownReduction() {
        return cooldownReduction;
    }

    public void setCooldownReduction(double cooldownReduction) {
        this.cooldownReduction = cooldownReduction;
    }

    public double getCriticalRate() {
        return criticalRate;
    }

    public void setCriticalRate(double criticalRate) {
        this.criticalRate = criticalRate;
    }

    public double getMovementSpeed() {
        return movementSpeed;
    }

    public void setMovementSpeed(double movementSpeed) {
        this.movementSpeed = movementSpeed;
    }

    public String getPassiveEffect() {
        return passiveEffect;
    }

    public void setPassiveEffect(String passiveEffect) {
        this.passiveEffect = passiveEffect;
    }

    public List<String> getSuitableHeroTypes() {
        return suitableHeroTypes;
    }

    public void setSuitableHeroTypes(List<String> suitableHeroTypes) {
        this.suitableHeroTypes = suitableHeroTypes == null ? new ArrayList<>() : suitableHeroTypes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
