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
    private JTextField searchField;
    private TableRowSorter<TeamTableModel> sorter;

    public TeamPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 10));
        initSearchBar();
        initTable();
    }

    private void initSearchBar() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("搜索战队:"));

        searchField = new JTextField(20);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterByKeyword(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterByKeyword(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterByKeyword(); }
        });
        topPanel.add(searchField);
        JButton refreshBtn = new JButton("刷新");
        refreshBtn.addActionListener(e -> refreshData());
        topPanel.add(refreshBtn);

        add(topPanel, BorderLayout.NORTH);
    }

    private void initTable() {
        GameDataManager dm = mainFrame.getDataManager();
        tableModel = new TeamTableModel(dm.getTeams());
        teamTable = new JTable(tableModel);

        sorter = new TableRowSorter<>(tableModel);
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

    private void filterByKeyword() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            String lower = text.toLowerCase();
            sorter.setRowFilter(new RowFilter<TeamTableModel, Integer>() {
                @Override
                public boolean include(Entry<? extends TeamTableModel, ? extends Integer> entry) {
                    for (int i = 0; i <= 4; i++) {
                        Object val = entry.getValue(i);
                        if (val != null && val.toString().toLowerCase().contains(lower)) {
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
    }

    public void refreshData() {
        tableModel.setTeams(mainFrame.getDataManager().getTeams());
    }

    static class TeamTableModel extends AbstractTableModel {
        private final String[] columns = {"编号", "名称", "简称", "地区", "胜率"};
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
