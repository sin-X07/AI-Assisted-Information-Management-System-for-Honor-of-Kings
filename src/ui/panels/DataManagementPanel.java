package ui.panels;

import model.Equipment;
import model.Hero;
import model.Person;
import model.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.OperationLogService;
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
    private static final Logger log = LoggerFactory.getLogger(DataManagementPanel.class);

    private final MainFrame mainFrame;
    private final Person currentUser;
    private final OperationLogService opLogService;

    private JTable heroMgmtTable;
    private HeroMgmtTableModel heroMgmtModel;

    private JTable equipMgmtTable;
    private EquipMgmtTableModel equipMgmtModel;

    private JTable teamMgmtTable;
    private TeamMgmtTableModel teamMgmtModel;

    public DataManagementPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.currentUser = mainFrame.getCurrentUser();
        this.opLogService = mainFrame.getOperationLogService();
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));

        tabbedPane.addTab("英雄管理", createHeroTab());
        tabbedPane.addTab("装备管理", createEquipmentTab());
        tabbedPane.addTab("战队管理", createTeamTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createHeroTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("添加英雄");
        JButton editBtn = new JButton("编辑英雄");
        JButton deleteBtn = new JButton("删除英雄");
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
                Hero hero = dialog.getHero();
                mainFrame.getDataManager().addHero(hero);
                refreshHeroTable();
                opLogService.logSuccess(currentUser, "ADD", "HERO", hero.getHeroId(),
                        "添加英雄: " + hero.getHeroName());
            }
        });

        editBtn.addActionListener(e -> {
            int row = heroMgmtTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "请先选择要编辑的英雄",
                        "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Hero hero = heroMgmtModel.getHeroAt(row);
            HeroEditDialog dialog = new HeroEditDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    mainFrame.getDataManager(), hero);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                Hero updated = dialog.getHero();
                mainFrame.getDataManager().updateHero(updated);
                refreshHeroTable();
                opLogService.logSuccess(currentUser, "UPDATE", "HERO", updated.getHeroId(),
                        "更新英雄: " + updated.getHeroName());
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = heroMgmtTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "请先选择要删除的英雄",
                        "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Hero hero = heroMgmtModel.getHeroAt(row);
            int result = JOptionPane.showConfirmDialog(this,
                    "确定删除英雄 [" + hero.getHeroName() + "] 吗?",
                    "确认删除", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                String heroId = hero.getHeroId();
                String heroName = hero.getHeroName();
                mainFrame.getDataManager().removeHeroById(heroId);
                refreshHeroTable();
                opLogService.logSuccess(currentUser, "DELETE", "HERO", heroId,
                        "删除英雄: " + heroName);
            }
        });

        return panel;
    }

    private JPanel createEquipmentTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("添加装备");
        JButton editBtn = new JButton("编辑装备");
        JButton deleteBtn = new JButton("删除装备");
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
                Equipment eq = dialog.getEquipment();
                mainFrame.getDataManager().addEquipment(eq);
                refreshEquipTable();
                opLogService.logSuccess(currentUser, "ADD", "EQUIPMENT", eq.getEquipmentId(),
                        "添加装备: " + eq.getEquipmentName());
            }
        });

        editBtn.addActionListener(e -> {
            int row = equipMgmtTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "请先选择要编辑的装备",
                        "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Equipment eq = equipMgmtModel.getEquipmentAt(row);
            EquipmentEditDialog dialog = new EquipmentEditDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    mainFrame.getDataManager(), eq);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                Equipment updated = dialog.getEquipment();
                mainFrame.getDataManager().updateEquipment(updated);
                refreshEquipTable();
                opLogService.logSuccess(currentUser, "UPDATE", "EQUIPMENT", updated.getEquipmentId(),
                        "更新装备: " + updated.getEquipmentName());
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = equipMgmtTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "请先选择要删除的装备",
                        "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Equipment eq = equipMgmtModel.getEquipmentAt(row);
            int result = JOptionPane.showConfirmDialog(this,
                    "确定删除装备 [" + eq.getEquipmentName() + "] 吗?",
                    "确认删除", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                String eqId = eq.getEquipmentId();
                String eqName = eq.getEquipmentName();
                mainFrame.getDataManager().removeEquipmentById(eqId);
                refreshEquipTable();
                opLogService.logSuccess(currentUser, "DELETE", "EQUIPMENT", eqId,
                        "删除装备: " + eqName);
            }
        });

        return panel;
    }

    private JPanel createTeamTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("添加战队");
        JButton editBtn = new JButton("编辑战队");
        JButton deleteBtn = new JButton("删除战队");
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
                Team team = dialog.getTeam();
                mainFrame.getDataManager().addTeam(team);
                refreshTeamTable();
                opLogService.logSuccess(currentUser, "ADD", "TEAM", team.getTeamId(),
                        "添加战队: " + team.getTeamName());
            }
        });

        editBtn.addActionListener(e -> {
            int row = teamMgmtTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "请先选择要编辑的战队",
                        "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Team team = teamMgmtModel.getTeamAt(row);
            TeamEditDialog dialog = new TeamEditDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    mainFrame.getDataManager(), team);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                Team updated = dialog.getTeam();
                mainFrame.getDataManager().updateTeam(updated);
                refreshTeamTable();
                opLogService.logSuccess(currentUser, "UPDATE", "TEAM", updated.getTeamId(),
                        "更新战队: " + updated.getTeamName());
            }
        });

        deleteBtn.addActionListener(e -> {
            int row = teamMgmtTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "请先选择要删除的战队",
                        "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Team team = teamMgmtModel.getTeamAt(row);
            int result = JOptionPane.showConfirmDialog(this,
                    "确定删除战队 [" + team.getTeamName() + "] 吗?",
                    "确认删除", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                String teamId = team.getTeamId();
                String teamName = team.getTeamName();
                mainFrame.getDataManager().removeTeamById(teamId);
                refreshTeamTable();
                opLogService.logSuccess(currentUser, "DELETE", "TEAM", teamId,
                        "删除战队: " + teamName);
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
        private final String[] columns = {"编号", "名称", "称号", "定位", "类型", "难度"};
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
        private final String[] columns = {"编号", "名称", "类型", "价格"};
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
        private final String[] columns = {"编号", "名称", "简称", "地区", "胜率"};
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
