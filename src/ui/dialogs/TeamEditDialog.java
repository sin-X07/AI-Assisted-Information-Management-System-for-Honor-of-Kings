package ui.dialogs;

import model.Team;
import service.GameDataManager;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class TeamEditDialog extends JDialog {
    private final GameDataManager dm;
    private final Team existingTeam;
    private boolean confirmed = false;
    private Team resultTeam;

    private JTextField idField, nameField, shortField, regionField;
    private JTextField coachField, captainField;
    private JTextField membersField, heroesField, honorsField;
    private JTextField winRateField, matchesField, descField;

    public TeamEditDialog(JFrame parent, GameDataManager dm, Team existing) {
        super(parent, existing == null ? "\u6DFB\u52A0\u6218\u961F" : "\u7F16\u8F91\u6218\u961F", true);
        this.dm = dm;
        this.existingTeam = existing;
        setSize(430, 500);
        setLocationRelativeTo(parent);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 5, 3, 5);

        int row = 0;
        idField = addRow(mainPanel, gbc, row++, "\u7F16\u53F7:", sval(existingTeam != null ? existingTeam.getTeamId() : null));
        nameField = addRow(mainPanel, gbc, row++, "\u540D\u79F0:", sval(existingTeam != null ? existingTeam.getTeamName() : null));
        shortField = addRow(mainPanel, gbc, row++, "\u7B80\u79F0:", sval(existingTeam != null ? existingTeam.getShortName() : null));
        regionField = addRow(mainPanel, gbc, row++, "\u5730\u533A:", sval(existingTeam != null ? existingTeam.getRegion() : null));
        coachField = addRow(mainPanel, gbc, row++, "\u6559\u7EC3:", sval(existingTeam != null ? existingTeam.getCoachName() : null));
        captainField = addRow(mainPanel, gbc, row++, "\u961F\u957F:", sval(existingTeam != null ? existingTeam.getCaptainName() : null));

        membersField = addRow(mainPanel, gbc, row++, "\u6210\u5458(\u9017\u53F7\u5206\u9694):",
                existingTeam != null ? String.join(",", existingTeam.getMemberNames()) : "");
        heroesField = addRow(mainPanel, gbc, row++, "\u5E38\u7528\u82F1\u96C4(\u9017\u53F7\u5206\u9694):",
                existingTeam != null ? String.join(",", existingTeam.getMainHeroes()) : "");
        honorsField = addRow(mainPanel, gbc, row++, "\u8363\u8A89(\u9017\u53F7\u5206\u9694):",
                existingTeam != null ? String.join(",", existingTeam.getHonors()) : "");

        winRateField = addRow(mainPanel, gbc, row++, "\u80DC\u7387:", existingTeam != null ? String.valueOf(existingTeam.getWinRate()) : "0.0");
        matchesField = addRow(mainPanel, gbc, row++, "\u603B\u573A\u6B21:", existingTeam != null ? String.valueOf(existingTeam.getTotalMatches()) : "0");
        descField = addRow(mainPanel, gbc, row++, "\u63CF\u8FF0:", sval(existingTeam != null ? existingTeam.getDescription() : null));

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        JButton saveBtn = new JButton("\u4FDD\u5B58");
        JButton cancelBtn = new JButton("\u53D6\u6D88");
        saveBtn.addActionListener(e -> doSave());
        cancelBtn.addActionListener(e -> dispose());
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);
        mainPanel.add(btnPanel, gbc);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(scrollPane);
    }

    private JTextField addRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        lbl.setPreferredSize(new Dimension(140, 25));
        panel.add(lbl, gbc);
        gbc.gridx = 1;
        JTextField field = new JTextField(value, 18);
        panel.add(field, gbc);
        return field;
    }

    private void doSave() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "\u7F16\u53F7\u4E0D\u80FD\u4E3A\u7A7A");
            return;
        }

        Team existing = dm.findTeamById(id);
        if (existing != null && existingTeam == null) {
            JOptionPane.showMessageDialog(this, "\u7F16\u53F7\u5DF2\u5B58\u5728");
            return;
        }

        Team team = new Team();
        team.setTeamId(id);
        team.setTeamName(nameField.getText().trim());
        team.setShortName(shortField.getText().trim());
        team.setRegion(regionField.getText().trim());
        team.setCoachName(coachField.getText().trim());
        team.setCaptainName(captainField.getText().trim());
        team.setMemberNames(parseList(membersField.getText()));
        team.setMainHeroes(parseList(heroesField.getText()));
        team.setHonors(parseList(honorsField.getText()));
        team.setWinRate(parseDouble(winRateField.getText(), 0.0));
        team.setTotalMatches(parseInt(matchesField.getText(), 0));
        team.setDescription(descField.getText().trim());
        team.setStatus("\u6B63\u5E38");

        resultTeam = team;
        confirmed = true;
        dispose();
    }

    private List<String> parseList(String text) {
        String trimmed = text.trim();
        if (trimmed.isEmpty()) return new java.util.ArrayList<>();
        return new java.util.ArrayList<>(Arrays.asList(trimmed.split(",")));
    }

    private int parseInt(String s, int def) {
        try { return Integer.parseInt(s.trim()); } catch (NumberFormatException e) { return def; }
    }

    private double parseDouble(String s, double def) {
        try { return Double.parseDouble(s.trim()); } catch (NumberFormatException e) { return def; }
    }

    private String sval(String s) { return s != null ? s : ""; }
    public boolean isConfirmed() { return confirmed; }
    public Team getTeam() { return resultTeam; }
}
