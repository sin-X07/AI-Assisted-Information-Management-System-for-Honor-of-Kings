package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Team {
    private String teamId;
    private String teamName;
    private String shortName;
    private String region;
    private String coachName;
    private String captainName;
    private List<String> memberNames;
    private List<String> mainHeroes;
    private List<String> honors;
    private double winRate;
    private int totalMatches;
    private String description;
    private LocalDateTime createTime;
    private String status;

    public Team() {
        this.memberNames = new ArrayList<>();
        this.mainHeroes = new ArrayList<>();
        this.honors = new ArrayList<>();
    }

    public Team(String teamId, String teamName, String shortName, String region) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.shortName = shortName;
        this.region = region;
        this.memberNames = new ArrayList<>();
        this.mainHeroes = new ArrayList<>();
        this.honors = new ArrayList<>();
    }

    public Team(String teamId, String teamName, String shortName, String region,
                String coachName, String captainName, List<String> memberNames) {
        this(teamId, teamName, shortName, region);
        this.coachName = coachName;
        this.captainName = captainName;
        this.memberNames = memberNames == null ? new ArrayList<>() : memberNames;
    }

    public Team(String teamName, String shortName, List<String> honors,
                double winRate, String description) {
        this.teamName = teamName;
        this.shortName = shortName;
        this.honors = honors == null ? new ArrayList<>() : honors;
        this.winRate = winRate;
        this.description = description;
        this.memberNames = new ArrayList<>();
        this.mainHeroes = new ArrayList<>();
    }

    public Team(String teamId, String teamName, String shortName, String region,
                String coachName, String captainName, List<String> memberNames,
                List<String> mainHeroes, List<String> honors, double winRate,
                int totalMatches, String description, LocalDateTime createTime, String status) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.shortName = shortName;
        this.region = region;
        this.coachName = coachName;
        this.captainName = captainName;
        this.memberNames = memberNames == null ? new ArrayList<>() : memberNames;
        this.mainHeroes = mainHeroes == null ? new ArrayList<>() : mainHeroes;
        this.honors = honors == null ? new ArrayList<>() : honors;
        this.winRate = winRate;
        this.totalMatches = totalMatches;
        this.description = description;
        this.createTime = createTime;
        this.status = status;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCoachName() {
        return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }

    public String getCaptainName() {
        return captainName;
    }

    public void setCaptainName(String captainName) {
        this.captainName = captainName;
    }

    public List<String> getMemberNames() {
        return memberNames;
    }

    public void setMemberNames(List<String> memberNames) {
        this.memberNames = memberNames == null ? new ArrayList<>() : memberNames;
    }

    public List<String> getMainHeroes() {
        return mainHeroes;
    }

    public void setMainHeroes(List<String> mainHeroes) {
        this.mainHeroes = mainHeroes == null ? new ArrayList<>() : mainHeroes;
    }

    public List<String> getHonors() {
        return honors;
    }

    public void setHonors(List<String> honors) {
        this.honors = honors == null ? new ArrayList<>() : honors;
    }

    public double getWinRate() {
        return winRate;
    }

    public void setWinRate(double winRate) {
        this.winRate = winRate;
    }

    public int getTotalMatches() {
        return totalMatches;
    }

    public void setTotalMatches(int totalMatches) {
        this.totalMatches = totalMatches;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
