package service;

import model.Player;
import model.Team;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FileStorageService {

    public void exportRankingToFile(GameDataManager dataManager, String filePath) {
        if (dataManager == null) {
            System.out.println("数据管理器为空，无法导出。");
            return;
        }
        if (filePath == null || filePath.trim().isEmpty()) {
            System.out.println("文件路径无效。");
            return;
        }

        List<Player> players = new ArrayList<>(dataManager.getPlayers());
        players.sort(Comparator
                .comparingDouble(RankingService::calculateWinRate)
                .thenComparingInt(RankingService::calculateTotalMatches)
                .reversed());

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(filePath), StandardCharsets.UTF_8))) {

            writer.write("排名\tID\t昵称\t战队\t胜率\t总场次");
            writer.newLine();
            writer.write("--------------------------------------------------");
            writer.newLine();

            for (int i = 0; i < players.size(); i++) {
                Player p = players.get(i);
                String teamName = findTeamByPlayer(dataManager, p);
                writer.write(String.format("%d\t%s\t%s\t%s\t%.1f%%\t%d",
                        i + 1,
                        p.getId(),
                        p.getNickname(),
                        teamName,
                        RankingService.calculateWinRate(p) * 100,
                        RankingService.calculateTotalMatches(p)));
                writer.newLine();
            }

            System.out.println("排行榜已成功导出到: " + filePath);
        } catch (IOException e) {
            System.out.println("文件写入失败: " + e.getMessage());
        }
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