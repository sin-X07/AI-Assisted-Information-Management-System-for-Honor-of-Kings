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
                .comparingDouble(RankingService::calculateWinRate)
                .thenComparingInt(RankingService::calculateTotalMatches)
                .reversed());

        int limit = Math.min(topN, players.size());

        System.out.println("===== 玩家荣誉排行榜 TOP " + limit + " =====");
        System.out.printf("%s %s %s %s %s %s%n",
                formatWithChinese("排名", 4),
                formatWithChinese("ID", 8),
                formatWithChinese("昵称", 6),
                formatWithChinese("战队", 12),
                formatWithChinese("胜率", 7),
                formatWithChinese("总场次", 6));
        System.out.println("------------------------------------------------");

        for (int i = 0; i < limit; i++) {
            Player p = players.get(i);
            String teamName = findTeamByPlayer(dataManager, p);
            String alignedRank = formatWithChinese(String.valueOf(i + 1), 4);
            String alignedId = formatWithChinese(p.getId(), 8);
            String alignedNickname = formatWithChinese(p.getNickname(), 6);
            String alignedTeamName = formatWithChinese(teamName, 12);
            System.out.printf("%s %s %s %s %7.1f%% %6d%n",
                    alignedRank,
                    alignedId,
                    alignedNickname,
                    alignedTeamName,
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

    private String formatWithChinese(String str, int totalLen) {
        if (str == null) str = "无";
        
        int currentDisplayWidth = 0;
        for (char c : str.toCharArray()) {
            if (Character.toString(c).matches("[\\u4e00-\\u9fa5]")) {
                currentDisplayWidth += 2;
            } else {
                currentDisplayWidth += 1;
            }
        }
        
        int paddingSpaces = totalLen - currentDisplayWidth;
        
        if (paddingSpaces <= 0) {
            return str;
        }
        return str + " ".repeat(paddingSpaces);
    }
}