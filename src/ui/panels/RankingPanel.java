package ui.panels;

import model.Player;
import model.Team;
import service.GameDataManager;
import service.RankingService;
import ui.MainFrame;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RankingPanel extends JPanel {
    private final MainFrame mainFrame;
    private JTable rankingTable;
    private RankingTableModel tableModel;
    private JSpinner topNSpinner;

    public RankingPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 10));
        initControls();
        initTable();
    }

    private void initControls() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("\u663E\u793A\u524D"));

        topNSpinner = new JSpinner(new SpinnerNumberModel(15, 1, 100, 1));
        topNSpinner.setPreferredSize(new Dimension(60, 24));
        topNSpinner.addChangeListener(e -> refreshData());
        topPanel.add(topNSpinner);

        topPanel.add(new JLabel("\u540D"));
        add(topPanel, BorderLayout.NORTH);
    }

    private void initTable() {
        tableModel = new RankingTableModel();
        rankingTable = new JTable(tableModel);

        rankingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rankingTable.setFillsViewportHeight(true);
        rankingTable.setRowHeight(26);
        rankingTable.getTableHeader().setReorderingAllowed(false);
        rankingTable.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        rankingTable.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));

        rankingTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        rankingTable.getColumnModel().getColumn(1).setPreferredWidth(60);
        rankingTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        rankingTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        rankingTable.getColumnModel().getColumn(4).setPreferredWidth(70);
        rankingTable.getColumnModel().getColumn(5).setPreferredWidth(70);

        JScrollPane scrollPane = new JScrollPane(rankingTable);
        add(scrollPane, BorderLayout.CENTER);

        refreshData();
    }

    public void refreshData() {
        int topN = (int) topNSpinner.getValue();
        GameDataManager dm = mainFrame.getDataManager();

        List<Player> players = new ArrayList<>(dm.getPlayers());
        players.sort(Comparator
                .comparingDouble((Player p) -> RankingService.calculateWinRate(p))
                .thenComparingInt((Player p) -> RankingService.calculateTotalMatches(p))
                .reversed());

        int limit = Math.min(topN, players.size());
        List<Player> topPlayers = players.subList(0, limit);

        tableModel.setData(topPlayers, dm);
    }

    static class RankingTableModel extends AbstractTableModel {
        private final String[] columns = {"\u6392\u540D", "ID", "\u6635\u79F0", "\u6218\u961F", "\u80DC\u7387", "\u603B\u573A\u6B21"};
        private List<Player> players = new ArrayList<>();
        private List<String> teamNames = new ArrayList<>();

        void setData(List<Player> players, GameDataManager dm) {
            this.players = players;
            this.teamNames = new ArrayList<>();
            for (Player p : players) {
                String tn = "\u65E0";
                for (Team t : dm.getTeams()) {
                    if (t.getMemberNames().contains(p.getNickname())) {
                        tn = t.getShortName();
                        break;
                    }
                }
                teamNames.add(tn);
            }
            fireTableDataChanged();
        }

        public int getRowCount() { return players.size(); }
        public int getColumnCount() { return columns.length; }
        public String getColumnName(int col) { return columns[col]; }

        public Object getValueAt(int row, int col) {
            Player p = players.get(row);
            switch (col) {
                case 0: return row + 1;
                case 1: return p.getId();
                case 2: return p.getNickname();
                case 3: return teamNames.get(row);
                case 4: return String.format("%.1f%%", RankingService.calculateWinRate(p) * 100);
                case 5: return RankingService.calculateTotalMatches(p);
                default: return "";
            }
        }

        @Override
        public Class<?> getColumnClass(int col) {
            if (col == 0 || col == 5) return Integer.class;
            return String.class;
        }
    }
}
