package service;

import model.MatchRecord;
import model.Player;
import model.Team;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RankingService {

    public void displayTopPlayers(GameDataManager dataManager, int topN) {
        if (dataManager == null) {
            System.out.println("数据管理器为空。");
            return;
        }
        if (topN <= 0) {
            return;
        }

        List<Player> players = new ArrayList<>(dataManager.getPlayers());

        players.sort(Comparator
                .comparingDouble(RankingService::calculateWinRate).reversed()
                .thenComparingInt(RankingService::calculateTotalMatches).reversed());

        int limit = Math.min(topN, players.size());

        System.out.println("===== 玩家荣誉排行榜 TOP " + limit + " =====");
        System.out.printf("%-4s %-8s %-6s %-12s %-8s %-8s%n",
                "排名", "ID", "昵称", "战队", "胜率", "总场次");
        System.out.println("--------------------------------------------------");

        for (int i = 0; i < limit; i++) {
            Player p = players.get(i);
            String teamName = findTeamByPlayer(dataManager, p);
            System.out.printf("%-4d %-8s %-6s %-12s %7.1f%% %6d%n",
                    i + 1,
                    p.getId(),
                    p.getNickname(),
                    teamName,
                    calculateWinRate(p) * 100,
                    calculateTotalMatches(p));
        }
        System.out.println("==================================================");
    }

    static double calculateWinRate(Player player) {
        List<MatchRecord> records = player.getMatchOverviews();
        if (records == null || records.isEmpty()) {
            return 0.0;
        }
        long wins = 0;
        for (MatchRecord record : records) {
            if ("胜利".equals(record.getResult())) {
                wins++;
            }
        }
        return (double) wins / records.size();
    }

    static int calculateTotalMatches(Player player) {
        List<MatchRecord> records = player.getMatchOverviews();
        return records == null ? 0 : records.size();
    }

    private String findTeamByPlayer(GameDataManager dataManager, Player player) {
        for (Team team : dataManager.getTeams()) {
            if (team.getMemberNames().contains(player.getNickname())) {
                return team.getShortName();
            }
        }
        return "无战队";
    }
}