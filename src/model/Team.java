package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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

    public static List<Team> createDefaultTeams() {
        List<Team> teams = new ArrayList<>();
        teams.add(new Team("T001", "重庆狼队", "Wolves", "重庆",
                "林", "Fly", Arrays.asList("Fly", "小胖", "向鱼", "妖刀", "一笙"),
                Arrays.asList("吕布", "赵云", "王昭君", "孙尚香", "蔡文姬"),
                Arrays.asList("KPL知名战队", "多次获得职业赛事冠军"), 0.72, 120,
                "KPL 知名强队，团队运营和团战能力突出。", LocalDateTime.now(), "正常"));
        teams.add(new Team("T002", "成都AG超玩会", "AG", "成都",
                "奶茶", "一诺", Arrays.asList("一诺", "长生", "轩染", "钟意", "Cat"),
                Arrays.asList("孙尚香", "马可波罗", "小乔", "亚瑟", "蔡文姬"),
                Arrays.asList("KPL人气战队", "职业赛事冠军队伍"), 0.68, 115,
                "KPL 高人气战队，选手个人能力和话题度较高。", LocalDateTime.now(), "正常"));
        teams.add(new Team("T003", "武汉eStarPro", "eStarPro", "武汉",
                "SK", "花海", Arrays.asList("花海", "清融", "坦然", "易峥", "子阳"),
                Arrays.asList("韩信", "貂蝉", "吕布", "后羿", "蔡文姬"),
                Arrays.asList("KPL知名战队", "多冠战队代表"), 0.74, 130,
                "KPL 冠军战队代表，体系成熟且执行力强。", LocalDateTime.now(), "正常"));
        return teams;
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
