package ui.dialogs;

import model.*;
import service.GameDataManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MatchDetailDialog extends JDialog {
    private final MatchRecord record;
    private final GameDataManager dm;

    public MatchDetailDialog(JFrame owner, MatchRecord record, GameDataManager dm) {
        super(owner, "比赛详情 - " + record.getMatchId(), true);
        this.record = record;
        this.dm = dm;
        setSize(900, 600);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        initUI();
    }

    private void initUI() {
        JPanel summaryPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        String duration = (record.getDurationSeconds() / 60) + ":" + String.format("%02d", record.getDurationSeconds() % 60);
        summaryPanel.add(makeSummaryCard("比赛ID", record.getMatchId()));
        summaryPanel.add(makeSummaryCard("比赛模式", record.getMatchMode()));
        summaryPanel.add(makeSummaryCard("比赛时长", duration));
        summaryPanel.add(makeSummaryCard("比赛结果", record.getResult()));
        summaryPanel.add(makeSummaryCard("比赛日期", record.getMatchTime().toLocalDate().toString()));
        add(summaryPanel, BorderLayout.NORTH);

        JPanel teamsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        teamsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        teamsPanel.add(createTeamPanel("蓝方", "蓝方"));
        teamsPanel.add(createTeamPanel("红方", "红方"));
        add(teamsPanel, BorderLayout.CENTER);
    }

    private JPanel makeSummaryCard(String label, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        lbl.setForeground(Color.GRAY);
        JLabel val = new JLabel(value, SwingConstants.CENTER);
        val.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        card.add(lbl, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTeamPanel(String side, String displayName) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                displayName + " (" + side + ")"));
        JPanel playersPanel = new JPanel();
        playersPanel.setLayout(new BoxLayout(playersPanel, BoxLayout.Y_AXIS));
        List<MatchParticipant> participants = record.getParticipants();
        for (MatchParticipant p : participants) {
            if (side.equals(p.getTeamSide())) {
                playersPanel.add(createPlayerCard(p));
                playersPanel.add(Box.createVerticalStrut(6));
            }
        }
        JScrollPane scroll = new JScrollPane(playersPanel);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPlayerCard(MatchParticipant p) {
        JPanel card = new JPanel(new BorderLayout(5, 3));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        String teamName = "";
        if (p.getTeamId() != null) {
            Team t = dm.findTeamById(p.getTeamId());
            if (t != null) teamName = " [" + t.getShortName() + "]";
        }
        JLabel nameLabel = new JLabel(p.getPlayerName() + teamName + "  |  " + p.getHeroName()
                + "  |  " + p.getKills() + "/" + p.getDeaths() + "/" + p.getAssists());
        nameLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 11));
        card.add(nameLabel, BorderLayout.NORTH);

        JPanel equipPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        List<String> eqIds = p.getEquipmentIds();
        if (eqIds != null && !eqIds.isEmpty()) {
            for (String eqId : eqIds) {
                Equipment eq = dm.findEquipmentById(eqId);
                if (eq != null) {
                    JLabel eqLabel = new JLabel(eq.getEquipmentName());
                    eqLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 10));
                    eqLabel.setForeground(new Color(100, 100, 100));
                    eqLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
                    equipPanel.add(eqLabel);
                }
            }
        }
        card.add(equipPanel, BorderLayout.CENTER);
        return card;
    }
}
