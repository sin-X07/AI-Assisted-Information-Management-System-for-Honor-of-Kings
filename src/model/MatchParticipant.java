package model;

import java.util.ArrayList;
import java.util.List;

public class MatchParticipant {
    private String playerId;
    private String playerName;
    private String heroName;
    private String teamSide;
    private int kills;
    private int deaths;
    private int assists;
    private List<String> equipmentIds;
    private String teamId;

    public MatchParticipant() {
        this.equipmentIds = new ArrayList<>();
    }

    public MatchParticipant(String playerId, String playerName, String heroName, String teamSide,
                            int kills, int deaths, int assists) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.heroName = heroName;
        this.teamSide = teamSide;
        this.kills = kills;
        this.deaths = deaths;
        this.assists = assists;
        this.equipmentIds = new ArrayList<>();
    }

    public MatchParticipant(String playerId, String playerName, String heroName, String teamSide,
                            int kills, int deaths, int assists, List<String> equipmentIds, String teamId) {
        this(playerId, playerName, heroName, teamSide, kills, deaths, assists);
        this.equipmentIds = equipmentIds == null ? new ArrayList<>() : equipmentIds;
        this.teamId = teamId;
    }

    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public String getHeroName() { return heroName; }
    public void setHeroName(String heroName) { this.heroName = heroName; }
    public String getTeamSide() { return teamSide; }
    public void setTeamSide(String teamSide) { this.teamSide = teamSide; }
    public int getKills() { return kills; }
    public void setKills(int kills) { this.kills = kills; }
    public int getDeaths() { return deaths; }
    public void setDeaths(int deaths) { this.deaths = deaths; }
    public int getAssists() { return assists; }
    public void setAssists(int assists) { this.assists = assists; }
    public List<String> getEquipmentIds() { return equipmentIds; }
    public void setEquipmentIds(List<String> equipmentIds) { this.equipmentIds = equipmentIds == null ? new ArrayList<>() : equipmentIds; }
    public String getTeamId() { return teamId; }
    public void setTeamId(String teamId) { this.teamId = teamId; }
}
