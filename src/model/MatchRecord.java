package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MatchRecord {
    private String matchId;
    private LocalDateTime matchTime;
    private String matchMode;
    private String result;
    private int durationSeconds;
    private List<MatchParticipant> participants;

    public MatchRecord() {
        this.participants = new ArrayList<>();
    }

    public MatchRecord(String matchId, LocalDateTime matchTime, String matchMode, String result) {
        this.matchId = matchId;
        this.matchTime = matchTime;
        this.matchMode = matchMode;
        this.result = result;
        this.participants = new ArrayList<>();
    }

    public MatchRecord(String matchId, LocalDateTime matchTime, String matchMode, String result,
                         int durationSeconds, List<MatchParticipant> participants) {
        this.matchId = matchId;
        this.matchTime = matchTime;
        this.matchMode = matchMode;
        this.result = result;
        this.durationSeconds = durationSeconds;
        this.participants = participants == null ? new ArrayList<>() : participants;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public LocalDateTime getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(LocalDateTime matchTime) {
        this.matchTime = matchTime;
    }

    public String getMatchMode() {
        return matchMode;
    }

    public void setMatchMode(String matchMode) {
        this.matchMode = matchMode;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public List<MatchParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<MatchParticipant> participants) {
        this.participants = participants == null ? new ArrayList<>() : participants;
    }
}
