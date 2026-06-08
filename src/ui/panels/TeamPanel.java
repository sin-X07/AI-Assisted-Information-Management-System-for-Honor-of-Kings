package ui.panels;

import model.Team;
import service.GameDataManager;
import ui.MainFrame;
import ui.dialogs.TeamDetailDialog;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class TeamPanel extends JPanel {
    private final MainFrame mainFrame;
    private JTable teamTable;
    private TeamTableModel tableModel;

    public TeamPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 10));
        initTable();
    }

    private void initTable() {
        GameDataManager dm = mainFrame.getDataManager();
        tableModel = new TeamTableModel(dm.getTeams());
        teamTable = new JTable(tableModel);

        TableRowSorter<TeamTableModel> sorter = new TableRowSorter<>(tableModel);
        teamTable.setRowSorter(sorter);

        teamTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        teamTable.setFillsViewportHeight(true);
        teamTable.setRowHeight(26);
        teamTable.getTableHeader().setReorderingAllowed(false);
        teamTable.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        teamTable.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));

        teamTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        teamTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        teamTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        teamTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        teamTable.getColumnModel().getColumn(4).setPreferredWidth(60);

        teamTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = teamTable.getSelectedRow();
                    if (row >= 0) {
                        int modelRow = teamTable.convertRowIndexToModel(row);
                        Team team = tableModel.getTeamAt(modelRow);
                        TeamDetailDialog dialog = new TeamDetailDialog(
                                (JFrame) SwingUtilities.getWindowAncestor(TeamPanel.this),
                                team);
                        dialog.setVisible(true);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(teamTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshData() {
        tableModel.setTeams(mainFrame.getDataManager().getTeams());
    }

    static class TeamTableModel extends AbstractTableModel {
        private final String[] columns = {"\u7F16\u53F7", "\u540D\u79F0", "\u7B80\u79F0", "\u5730\u533A", "\u80DC\u7387"};
        private List<Team> teams;

        TeamTableModel(List<Team> teams) {
            this.teams = teams;
        }

        void setTeams(List<Team> teams) {
            this.teams = teams;
            fireTableDataChanged();
        }

        Team getTeamAt(int row) {
            return teams.get(row);
        }

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
