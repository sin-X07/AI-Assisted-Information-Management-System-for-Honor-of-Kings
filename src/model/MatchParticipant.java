package model;

public class MatchParticipant {
    private String playerId;
    private String playerName;
    private String heroName;
    private String teamSide;
    private int kills;
    private int deaths;
    private int assists;

    public MatchParticipant() {
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
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getHeroName() {
        return heroName;
    }

    public void setHeroName(String heroName) {
        this.heroName = heroName;
    }

    public String getTeamSide() {
        return teamSide;
    }

    public void setTeamSide(String teamSide) {
        this.teamSide = teamSide;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }
}
