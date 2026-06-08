package ui.dialogs;

import model.Team;

import javax.swing.*;
import java.awt.*;

public class TeamDetailDialog extends JDialog {
    public TeamDetailDialog(JFrame parent, Team team) {
        super(parent, "\u6218\u961F\u8BE6\u60C5 - " + team.getTeamName(), true);
        setSize(400, 500);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        addField(mainPanel, "\u7F16\u53F7", team.getTeamId());
        addField(mainPanel, "\u540D\u79F0", team.getTeamName());
        addField(mainPanel, "\u7B80\u79F0", team.getShortName());
        addField(mainPanel, "\u5730\u533A", team.getRegion());
        addField(mainPanel, "\u6559\u7EC3", orEmpty(team.getCoachName()));
        addField(mainPanel, "\u961F\u957F", orEmpty(team.getCaptainName()));
        addField(mainPanel, "\u80DC\u7387", String.format("%.1f%%", team.getWinRate() * 100));
        addField(mainPanel, "\u603B\u573A\u6B21", String.valueOf(team.getTotalMatches()));

        mainPanel.add(Box.createVerticalStrut(8));
        addSection(mainPanel, "\u6218\u961F\u6210\u5458");
        if (team.getMemberNames().isEmpty()) {
            addValue(mainPanel, "  \u65E0");
        } else {
            addValue(mainPanel, "  " + String.join(", ", team.getMemberNames()));
        }

        mainPanel.add(Box.createVerticalStrut(8));
        addSection(mainPanel, "\u5E38\u7528\u82F1\u96C4");
        if (team.getMainHeroes().isEmpty()) {
            addValue(mainPanel, "  \u65E0");
        } else {
            addValue(mainPanel, "  " + String.join(", ", team.getMainHeroes()));
        }

        mainPanel.add(Box.createVerticalStrut(8));
        addSection(mainPanel, "\u8363\u8A89");
        if (team.getHonors().isEmpty()) {
            addValue(mainPanel, "  \u65E0");
        } else {
            addValue(mainPanel, "  " + String.join(", ", team.getHonors()));
        }

        if (team.getDescription() != null && !team.getDescription().isEmpty()) {
            mainPanel.add(Box.createVerticalStrut(8));
            addSection(mainPanel, "\u63CF\u8FF0");
            addValue(mainPanel, "  " + team.getDescription());
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(scrollPane);
    }

    private void addField(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        lbl.setPreferredSize(new Dimension(80, 25));
        row.add(lbl, BorderLayout.WEST);
        JLabel val = new JLabel(value);
        val.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        row.add(val, BorderLayout.CENTER);
        panel.add(row);
    }

    private void addSection(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
    }

    private void addValue(JPanel panel, String value) {
        JLabel val = new JLabel(value);
        val.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        val.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(val);
    }

    private String orEmpty(String s) { return s != null ? s : ""; }
}
