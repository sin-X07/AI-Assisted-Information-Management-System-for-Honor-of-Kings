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
        topPanel.add(new JLabel("\u73A9\u5BB6ID\u6216\u6635\u79F0:"));

        searchField = new JTextField(15);
        topPanel.add(searchField);

        JButton searchBtn = new JButton("\u67E5\u8BE2");
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
            JOptionPane.showMessageDialog(this, "\u8BF7\u8F93\u5165\u73A9\u5BB6ID\u6216\u6635\u79F0",
                    "\u63D0\u793A", JOptionPane.WARNING_MESSAGE);
            return;
        }

        GameDataManager dm = mainFrame.getDataManager();
        resultPanel.removeAll();

        Player foundPlayer = null;
        for (Player p : dm.getPlayers()) {
            if (keyword.equals(p.getId()) || keyword.equals(p.getNickname())) {
                foundPlayer = p;
                break;
            }
        }

        if (foundPlayer == null) {
            for (Team team : dm.getTeams()) {
                if (team.getMemberNames().contains(keyword)) {
                    foundPlayer = new Player();
                    foundPlayer.setId(keyword);
                    foundPlayer.setNickname(keyword);
                    foundPlayer.setRole("PLAYER");
                    break;
                }
            }
        }

        if (foundPlayer == null) {
            JLabel notFound = new JLabel("\u672A\u627E\u5230\u73A9\u5BB6: " + keyword);
            notFound.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
            notFound.setForeground(Color.RED);
            resultPanel.add(notFound);
        } else {
            buildResultView(foundPlayer);
        }

        resultPanel.revalidate();
        resultPanel.repaint();
    }

    private void buildResultView(Player player) {
        GameDataManager dm = mainFrame.getDataManager();

        resultPanel.add(createSectionLabel("\u25A0 \u73A9\u5BB6\u4FE1\u606F"));
        addInfoRow("ID: " + player.getId());
        addInfoRow("\u6635\u79F0: " + player.getNickname());
        if (player.getGameId() != null) {
            addInfoRow("\u6E38\u620FID: " + player.getGameId() + " | \u533A\u670D: " + player.getServerArea());
            addInfoRow("\u6BB5\u4F4D: " + player.getRank() + " | \u7B49\u7EA7: " + player.getLevel());
            addInfoRow("\u4E3B\u73A9\u4F4D\u7F6E: " + player.getMainPosition() + " | \u559C\u7231\u82F1\u96C4: " + player.getFavoriteHero());
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
            resultPanel.add(createSectionLabel("\u25A0 \u6240\u5C5E\u6218\u961F"));
            addInfoRow(playerTeam.getTeamName() + " (" + playerTeam.getShortName() + ")");
            addInfoRow("\u5730\u533A: " + playerTeam.getRegion() + " | \u6559\u7EC3: " + playerTeam.getCoachName());
            addInfoRow("\u961F\u957F: " + playerTeam.getCaptainName());
            addInfoRow("\u6218\u961F\u80DC\u7387: " + String.format("%.1f%%", playerTeam.getWinRate() * 100));
            addInfoRow("\u8363\u8A89: " + String.join(", ", playerTeam.getHonors()));

            if (player.getGameId() != null) {
                List<MatchRecord> records = player.getMatchOverviews();
                if (records != null && !records.isEmpty()) {
                    int totalMatches = RankingService.calculateTotalMatches(player);
                    double winRate = RankingService.calculateWinRate(player);
                    long wins = records.stream().filter(r -> "\u80DC\u5229".equals(r.getResult())).count();

                    resultPanel.add(Box.createVerticalStrut(8));
                    resultPanel.add(createSectionLabel("\u25A0 \u4E2A\u4EBA\u6218\u7EE9"));
                    addInfoRow("\u603B\u573A\u6B21: " + totalMatches + " | \u80DC\u7387: " + String.format("%.1f%%", winRate * 100));
                    addInfoRow("\u80DC: " + wins + " | \u8D1F: " + (totalMatches - wins));
                }
            }

            resultPanel.add(Box.createVerticalStrut(8));
            resultPanel.add(createSectionLabel("\u25A0 \u6218\u961F\u5E38\u7528\u82F1\u96C4\u53CA\u63A8\u8350\u88C5\u5907"));

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

                    addInfoRowToPanel(heroCard, "\u5B9A\u4F4D: " + hero.getPosition() + " | \u7C7B\u578B: " + hero.getHeroType()
                            + " | \u96BE\u5EA6: " + hero.getDifficulty());
                    addInfoRowToPanel(heroCard, "\u80FD\u529B: \u751F\u5B58" + hero.getSurvivalAbility()
                            + " | \u653B\u51FB" + hero.getAttackAbility()
                            + " | \u6280\u80FD" + hero.getSkillAbility()
                            + " | \u8F85\u52A9" + hero.getSupportAbility());

                    if (!hero.getRecommendedEquipmentIds().isEmpty()) {
                        StringBuilder eqStr = new StringBuilder("\u63A8\u8350\u88C5\u5907: ");
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
            addInfoRow("(\u8BE5\u73A9\u5BB6\u672A\u52A0\u5165\u4EFB\u4F55\u6218\u961F)");
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
