package model;

import java.util.ArrayList;
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
