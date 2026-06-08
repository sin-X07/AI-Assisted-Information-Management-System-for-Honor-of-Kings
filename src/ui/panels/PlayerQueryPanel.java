package ui.panels;

import model.*;
import service.GameDataManager;
import service.RankingService;
import ui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PlayerQueryPanel extends JPanel {
    private final MainFrame mainFrame;
    private JTextField searchField;
    private JPanel resultPanel;
    private JScrollPane resultScroll;

    public PlayerQueryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 10));
        initSearchBar();
        initResultArea();
    }

    private void initSearchBar() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("玩家ID或昵称:"));

        searchField = new JTextField(15);
        topPanel.add(searchField);

        JButton searchBtn = new JButton("查询");
        searchBtn.addActionListener(e -> doSearch());
        topPanel.add(searchBtn);

        searchField.addActionListener(e -> doSearch());

        add(topPanel, BorderLayout.NORTH);
    }

    private void initResultArea() {
        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultScroll = new JScrollPane(resultPanel);
        resultScroll.setBorder(BorderFactory.createEmptyBorder());
        add(resultScroll, BorderLayout.CENTER);
    }

    private void doSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入玩家ID或昵称",
                    "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        GameDataManager dm = mainFrame.getDataManager();
        resultPanel.removeAll();

        // Fuzzy search for players (matches by ID or nickname, case-insensitive)
        List<Player> matchingPlayers = dm.searchPlayers(keyword);

        if (matchingPlayers.isEmpty()) {
            // Also check team members by name
            for (Team team : dm.getTeams()) {
                for (String memberName : team.getMemberNames()) {
                    if (memberName != null && memberName.toLowerCase().contains(keyword.toLowerCase())) {
                        Player p = new Player();
                        p.setId(memberName);
                        p.setNickname(memberName);
                        p.setRole("PLAYER");
                        matchingPlayers.add(p);
                    }
                }
            }
        }

        if (matchingPlayers.isEmpty()) {
            JLabel notFound = new JLabel("未找到匹配的玩家: " + keyword);
            notFound.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
            notFound.setForeground(Color.RED);
            resultPanel.add(notFound);
        } else {
            for (Player player : matchingPlayers) {
                buildResultView(player);
                resultPanel.add(Box.createVerticalStrut(12));
            }
        }

        resultPanel.revalidate();
        resultPanel.repaint();
    }

    private void buildResultView(Player player) {
        GameDataManager dm = mainFrame.getDataManager();

        resultPanel.add(createSectionLabel("■ 玩家信息"));
        addInfoRow("ID: " + player.getId());
        addInfoRow("昵称: " + player.getNickname());
        if (player.getGameId() != null) {
            addInfoRow("游戏ID: " + player.getGameId() + " | 区服: " + player.getServerArea());
            addInfoRow("段位: " + player.getRank() + " | 等级: " + player.getLevel());
            addInfoRow("主玩位置: " + player.getMainPosition() + " | 喜爱英雄: " + player.getFavoriteHero());
        }

        Team playerTeam = null;
        for (Team team : dm.getTeams()) {
            if (team.getMemberNames().contains(player.getNickname())) {
                playerTeam = team;
                break;
            }
        }

        if (playerTeam != null) {
            resultPanel.add(Box.createVerticalStrut(8));
            resultPanel.add(createSectionLabel("■ 所属战队"));
            addInfoRow(playerTeam.getTeamName() + " (" + playerTeam.getShortName() + ")");
            addInfoRow("地区: " + playerTeam.getRegion() + " | 教练: " + playerTeam.getCoachName());
            addInfoRow("队长: " + playerTeam.getCaptainName());
            addInfoRow("战队胜率: " + String.format("%.1f%%", playerTeam.getWinRate() * 100));
            addInfoRow("荣誉: " + String.join(", ", playerTeam.getHonors()));

            if (player.getGameId() != null) {
                List<MatchRecord> records = player.getMatchOverviews();
                if (records != null && !records.isEmpty()) {
                    int totalMatches = RankingService.calculateTotalMatches(player);
                    double winRate = RankingService.calculateWinRate(player);
                    long wins = records.stream().filter(r -> "胜利".equals(r.getResult())).count();

                    resultPanel.add(Box.createVerticalStrut(8));
                    resultPanel.add(createSectionLabel("■ 个人战绩"));
                    addInfoRow("总场次: " + totalMatches + " | 胜率: " + String.format("%.1f%%", winRate * 100));
                    addInfoRow("胜: " + wins + " | 负: " + (totalMatches - wins));
                }
            }

            resultPanel.add(Box.createVerticalStrut(8));
            resultPanel.add(createSectionLabel("■ 战队常用英雄及推荐装备"));

            for (String heroName : playerTeam.getMainHeroes()) {
                Hero hero = dm.findHeroByName(heroName);
                if (hero != null) {
                    JPanel heroCard = new JPanel();
                    heroCard.setLayout(new BoxLayout(heroCard, BoxLayout.Y_AXIS));
                    heroCard.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
                    heroCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
                    heroCard.setAlignmentX(Component.LEFT_ALIGNMENT);

                    JLabel heroNameLabel = new JLabel(hero.getHeroName() + " [" + hero.getTitle() + "]");
                    heroNameLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
                    heroCard.add(heroNameLabel);

                    addInfoRowToPanel(heroCard, "定位: " + hero.getPosition() + " | 类型: " + hero.getHeroType()
                            + " | 难度: " + hero.getDifficulty());
                    addInfoRowToPanel(heroCard, "能力: 生存" + hero.getSurvivalAbility()
                            + " | 攻击" + hero.getAttackAbility()
                            + " | 技能" + hero.getSkillAbility()
                            + " | 辅助" + hero.getSupportAbility());

                    if (!hero.getRecommendedEquipmentIds().isEmpty()) {
                        StringBuilder eqStr = new StringBuilder("推荐装备: ");
                        for (int i = 0; i < hero.getRecommendedEquipmentIds().size(); i++) {
                            Equipment eq = dm.findEquipmentById(hero.getRecommendedEquipmentIds().get(i));
                            if (eq != null) {
                                if (i > 0) eqStr.append(", ");
                                eqStr.append(eq.getEquipmentName());
                            }
                        }
                        addInfoRowToPanel(heroCard, eqStr.toString());
                    }

                    resultPanel.add(heroCard);
                    resultPanel.add(Box.createVerticalStrut(4));
                }
            }
        } else {
            resultPanel.add(Box.createVerticalStrut(8));
            addInfoRow("(该玩家未加入任何战队)");
        }

        resultPanel.add(Box.createVerticalGlue());
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        return label;
    }

    private void addInfoRow(String text) {
        JLabel label = new JLabel("    " + text);
        label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultPanel.add(label);
    }

    private void addInfoRowToPanel(JPanel panel, String text) {
        JLabel label = new JLabel("    " + text);
        label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
    }
}
