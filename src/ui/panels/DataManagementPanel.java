package ui.panels;

import model.Equipment;
import model.Hero;
import model.Team;
import ui.MainFrame;
import ui.dialogs.EquipmentEditDialog;
import ui.dialogs.HeroEditDialog;
import ui.dialogs.TeamEditDialog;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DataManagementPanel extends JPanel {
    private final MainFrame mainFrame;

    private JTable heroMgmtTable;
    private HeroMgmtTableModel heroMgmtModel;

    private JTable equipMgmtTable;
    private EquipMgmtTableModel equipMgmtModel;

    private JTable teamMgmtTable;
    private TeamMgmtTableModel teamMgmtModel;

    public DataManagementPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));

        tabbedPane.addTab("\u82F1\u96C4\u7BA1\u7406", createHeroTab());
        tabbedPane.addTab("\u88C5\u5907\u7BA1\u7406", createEquipmentTab());
        tabbedPane.addTab("\u6218\u961F\u7BA1\u7406", createTeamTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createHeroTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("\u6DFB\u52A0\u82F1\u96C4");
        JButton editBtn = new JButton("\u7F16\u8F91\u82F1\u96C4");
        JButton deleteBtn = new JButton("\u5220\u9664\u82F1\u96C4");
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        panel.add(btnPanel, BorderLayout.NORTH);

        heroMgmtModel = new HeroMgmtTableModel(mainFrame.getDataManager().getHeroes());
        heroMgmtTable = new JTable(heroMgmtModel);
        heroMgmtTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        heroMgmtTable.setFillsViewportHeight(true);
        heroMgmtTable.setRowHeight(24);
        heroMgmtTable.getTableHeader().setReorderingAllowed(false);
        heroMgmtTable.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 11));
        heroMgmtTable.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        panel.add(new JScrollPane(heroMgmtTable), BorderLayout.CENTER);

        addBtn.addActionListener(e -> {
            HeroEditDialog dialog = new HeroEditDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    mainFrame.getDataManager(), null);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                mainFrame.getDataManager().addHero(dialog.getHero());
                refreshHeroTable();
            }
        });

        editBtn.addActionListener(e -> {
            int row = heroMgmtTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "\u8BF7\u5148\u9009\u62E9\u8981\u7F16\u8F91\u7684\u82F1\u96C4",
                        "\u63D0\u793A", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Hero hero = heroMgmtModel.getHeroAt(row);
            HeroEditDialog dialog = new HeroEditDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    mainFrame.getDataManager(), hero);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                mainFrame.getDataManager().updateHero(dialog.getHero());
                refreshHeroTable();
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = heroMgmtTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "\u8BF7\u5148\u9009\u62E9\u8981\u5220\u9664\u7684\u82F1\u96C4",
                        "\u63D0\u793A", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Hero hero = heroMgmtModel.getHeroAt(row);
            int result = JOptionPane.showConfirmDialog(this,
                    "\u786E\u5B9A\u5220\u9664\u82F1\u96C4 [" + hero.getHeroName() + "] \u5417?",
                    "\u786E\u8BA4\u5220\u9664", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                mainFrame.getDataManager().removeHeroById(hero.getHeroId());
                refreshHeroTable();
            }
        });

        return panel;
    }

    private JPanel createEquipmentTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("\u6DFB\u52A0\u88C5\u5907");
        JButton editBtn = new JButton("\u7F16\u8F91\u88C5\u5907");
        JButton deleteBtn = new JButton("\u5220\u9664\u88C5\u5907");
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        panel.add(btnPanel, BorderLayout.NORTH);

        equipMgmtModel = new EquipMgmtTableModel(mainFrame.getDataManager().getEquipments());
        equipMgmtTable = new JTable(equipMgmtModel);
        equipMgmtTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        equipMgmtTable.setFillsViewportHeight(true);
        equipMgmtTable.setRowHeight(24);
        equipMgmtTable.getTableHeader().setReorderingAllowed(false);
        equipMgmtTable.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 11));
        equipMgmtTable.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        panel.add(new JScrollPane(equipMgmtTable), BorderLayout.CENTER);

        addBtn.addActionListener(e -> {
            EquipmentEditDialog dialog = new EquipmentEditDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    mainFrame.getDataManager(), null);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                mainFrame.getDataManager().addEquipment(dialog.getEquipment());
                refreshEquipTable();
            }
        });

        editBtn.addActionListener(e -> {
            int row = equipMgmtTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "\u8BF7\u5148\u9009\u62E9\u8981\u7F16\u8F91\u7684\u88C5\u5907",
                        "\u63D0\u793A", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Equipment eq = equipMgmtModel.getEquipmentAt(row);
            EquipmentEditDialog dialog = new EquipmentEditDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    mainFrame.getDataManager(), eq);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                mainFrame.getDataManager().updateEquipment(dialog.getEquipment());
                refreshEquipTable();
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = equipMgmtTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "\u8BF7\u5148\u9009\u62E9\u8981\u5220\u9664\u7684\u88C5\u5907",
                        "\u63D0\u793A", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Equipment eq = equipMgmtModel.getEquipmentAt(row);
            int result = JOptionPane.showConfirmDialog(this,
                    "\u786E\u5B9A\u5220\u9664\u88C5\u5907 [" + eq.getEquipmentName() + "] \u5417?",
                    "\u786E\u8BA4\u5220\u9664", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                mainFrame.getDataManager().removeEquipmentById(eq.getEquipmentId());
                refreshEquipTable();
            }
        });

        return panel;
    }

    private JPanel createTeamTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("\u6DFB\u52A0\u6218\u961F");
        JButton editBtn = new JButton("\u7F16\u8F91\u6218\u961F");
        JButton deleteBtn = new JButton("\u5220\u9664\u6218\u961F");
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        panel.add(btnPanel, BorderLayout.NORTH);

        teamMgmtModel = new TeamMgmtTableModel(mainFrame.getDataManager().getTeams());
        teamMgmtTable = new JTable(teamMgmtModel);
        teamMgmtTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        teamMgmtTable.setFillsViewportHeight(true);
        teamMgmtTable.setRowHeight(24);
        teamMgmtTable.getTableHeader().setReorderingAllowed(false);
        teamMgmtTable.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 11));
        teamMgmtTable.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        panel.add(new JScrollPane(teamMgmtTable), BorderLayout.CENTER);

        addBtn.addActionListener(e -> {
            TeamEditDialog dialog = new TeamEditDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    mainFrame.getDataManager(), null);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                mainFrame.getDataManager().addTeam(dialog.getTeam());
                refreshTeamTable();
            }
        });

        editBtn.addActionListener(e -> {
            int row = teamMgmtTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "\u8BF7\u5148\u9009\u62E9\u8981\u7F16\u8F91\u7684\u6218\u961F",
                        "\u63D0\u793A", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Team team = teamMgmtModel.getTeamAt(row);
            TeamEditDialog dialog = new TeamEditDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    mainFrame.getDataManager(), team);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                mainFrame.getDataManager().updateTeam(dialog.getTeam());
                refreshTeamTable();
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = teamMgmtTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "\u8BF7\u5148\u9009\u62E9\u8981\u5220\u9664\u7684\u6218\u961F",
                        "\u63D0\u793A", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Team team = teamMgmtModel.getTeamAt(row);
            int result = JOptionPane.showConfirmDialog(this,
                    "\u786E\u5B9A\u5220\u9664\u6218\u961F [" + team.getTeamName() + "] \u5417?",
                    "\u786E\u8BA4\u5220\u9664", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                mainFrame.getDataManager().removeTeamById(team.getTeamId());
                refreshTeamTable();
            }
        });

        return panel;
    }

    private void refreshHeroTable() {
        heroMgmtModel.setHeroes(mainFrame.getDataManager().getHeroes());
    }

    private void refreshEquipTable() {
        equipMgmtModel.setEquipments(mainFrame.getDataManager().getEquipments());
    }

    private void refreshTeamTable() {
        teamMgmtModel.setTeams(mainFrame.getDataManager().getTeams());
    }

    public void refreshData() {
        refreshHeroTable();
        refreshEquipTable();
        refreshTeamTable();
    }

    static class HeroMgmtTableModel extends AbstractTableModel {
        private final String[] columns = {"\u7F16\u53F7", "\u540D\u79F0", "\u79F0\u53F7", "\u5B9A\u4F4D", "\u7C7B\u578B", "\u96BE\u5EA6"};
        private List<Hero> heroes = new ArrayList<>();

        HeroMgmtTableModel(List<Hero> heroes) { this.heroes = new ArrayList<>(heroes); }
        void setHeroes(List<Hero> heroes) { this.heroes = new ArrayList<>(heroes); fireTableDataChanged(); }
        Hero getHeroAt(int row) { return heroes.get(row); }
        public int getRowCount() { return heroes.size(); }
        public int getColumnCount() { return columns.length; }
        public String getColumnName(int col) { return columns[col]; }
        public Object getValueAt(int row, int col) {
            Hero h = heroes.get(row);
            switch (col) {
                case 0: return h.getHeroId();
                case 1: return h.getHeroName();
                case 2: return h.getTitle();
                case 3: return h.getPosition();
                case 4: return h.getHeroType();
                case 5: return h.getDifficulty();
                default: return "";
            }
        }
    }

    static class EquipMgmtTableModel extends AbstractTableModel {
        private final String[] columns = {"\u7F16\u53F7", "\u540D\u79F0", "\u7C7B\u578B", "\u4EF7\u683C"};
        private List<Equipment> equipments = new ArrayList<>();

        EquipMgmtTableModel(List<Equipment> equipments) { this.equipments = new ArrayList<>(equipments); }
        void setEquipments(List<Equipment> equipments) { this.equipments = new ArrayList<>(equipments); fireTableDataChanged(); }
        Equipment getEquipmentAt(int row) { return equipments.get(row); }
        public int getRowCount() { return equipments.size(); }
        public int getColumnCount() { return columns.length; }
        public String getColumnName(int col) { return columns[col]; }
        public Object getValueAt(int row, int col) {
            Equipment eq = equipments.get(row);
            switch (col) {
                case 0: return eq.getEquipmentId();
                case 1: return eq.getEquipmentName();
                case 2: return eq.getEquipmentType();
                case 3: return eq.getPrice();
                default: return "";
            }
        }
    }

    static class TeamMgmtTableModel extends AbstractTableModel {
        private final String[] columns = {"\u7F16\u53F7", "\u540D\u79F0", "\u7B80\u79F0", "\u5730\u533A", "\u80DC\u7387"};
        private List<Team> teams = new ArrayList<>();

        TeamMgmtTableModel(List<Team> teams) { this.teams = new ArrayList<>(teams); }
        void setTeams(List<Team> teams) { this.teams = new ArrayList<>(teams); fireTableDataChanged(); }
        Team getTeamAt(int row) { return teams.get(row); }
        public int getRowCount() { return teams.size(); }
        public int getColumnCount() { return columns.length; }
        public String getColumnName(int col) { return columns[col]; }
        public Object getValueAt(int row, int col) {
            Team t = teams.get(row);
            switch (col) {
                case 0: return t.getTeamId();
                case 1: return t.getTeamName();
                case 2: return t.getShortName();
                case 3: return t.getRegion();
                case 4: return String.format("%.1f%%", t.getWinRate() * 100);
                default: return "";
            }
        }
    }
}
