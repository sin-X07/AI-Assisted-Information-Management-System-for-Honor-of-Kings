package model;

import java.util.ArrayList;
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
