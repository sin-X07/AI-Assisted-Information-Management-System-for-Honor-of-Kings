package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Player extends Person {
    private String gameId;
    private String serverArea;
    private String rank;
    private int level;
    private String mainPosition;
    private String favoriteHero;
    private double winRate;
    private int totalMatches;
    private int creditScore;
    private String aiRecommendationPreference;
    private String remark;
    private List<MatchRecord> matchOverviews;

    public Player() {
        this.matchOverviews = new ArrayList<>();
    }

    public Player(String id, String username, String password, String nickname) {
        super(id, username, password, nickname, "PLAYER");
        this.matchOverviews = new ArrayList<>();
    }

    public Player(String id, String username, String password, String nickname,
                  String gameId, String serverArea, String rank) {
        super(id, username, password, nickname, "PLAYER");
        this.gameId = gameId;
        this.serverArea = serverArea;
        this.rank = rank;
        this.matchOverviews = new ArrayList<>();
    }

    public Player(String id, String username, String password, String nickname, String phone,
                  String email, String role, LocalDateTime createTime, String status,
                  String gameId, String serverArea, String rank, int level, String mainPosition,
                  String favoriteHero, double winRate, int totalMatches, int creditScore,
                  String aiRecommendationPreference, String remark, List<MatchRecord> matchOverviews) {
        super(id, username, password, nickname, phone, email, role, createTime, status);
        this.gameId = gameId;
        this.serverArea = serverArea;
        this.rank = rank;
        this.level = level;
        this.mainPosition = mainPosition;
        this.favoriteHero = favoriteHero;
        this.winRate = winRate;
        this.totalMatches = totalMatches;
        this.creditScore = creditScore;
        this.aiRecommendationPreference = aiRecommendationPreference;
        this.remark = remark;
        this.matchOverviews = matchOverviews == null ? new ArrayList<>() : matchOverviews;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getServerArea() {
        return serverArea;
    }

    public void setServerArea(String serverArea) {
        this.serverArea = serverArea;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getMainPosition() {
        return mainPosition;
    }

    public void setMainPosition(String mainPosition) {
        this.mainPosition = mainPosition;
    }

    public String getFavoriteHero() {
        return favoriteHero;
    }

    public void setFavoriteHero(String favoriteHero) {
        this.favoriteHero = favoriteHero;
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

    public int getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public String getAiRecommendationPreference() {
        return aiRecommendationPreference;
    }

    public void setAiRecommendationPreference(String aiRecommendationPreference) {
        this.aiRecommendationPreference = aiRecommendationPreference;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<MatchRecord> getMatchOverviews() {
        return matchOverviews;
    }

    public void setMatchOverviews(List<MatchRecord> matchOverviews) {
        this.matchOverviews = matchOverviews == null ? new ArrayList<>() : matchOverviews;
    }
}
