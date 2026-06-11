package ui.panels;

import model.MatchRecord;
import service.GameDataManager;
import ui.MainFrame;
import ui.dialogs.MatchDetailDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MatchHistoryPanel extends JPanel {
    private final MainFrame mainFrame;
    private JTable matchTable;
    private DefaultTableModel tableModel;

    public MatchHistoryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel title = new JLabel("战绩查询");
        title.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);
        initTable();
        loadMatchData();
    }

    private void initTable() {
        String[] columns = {"比赛ID", "模式", "日期", "时长", "结果", "红方", "蓝方"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        matchTable = new JTable(tableModel);
        matchTable.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        matchTable.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        matchTable.setRowHeight(28);
        matchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        matchTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = matchTable.getSelectedRow();
                    if (row >= 0) {
                        String matchId = (String) tableModel.getValueAt(row, 0);
                        GameDataManager dm = mainFrame.getDataManager();
                        MatchRecord record = dm.findMatchById(matchId);
                        if (record != null) {
                            MatchDetailDialog dialog = new MatchDetailDialog(mainFrame, record, dm);
                            dialog.setVisible(true);
                        }
                    }
                }
            }
        });
        JScrollPane scroll = new JScrollPane(matchTable);
        add(scroll, BorderLayout.CENTER);
    }

    public void loadMatchData() {
        GameDataManager dm = mainFrame.getDataManager();
        List<MatchRecord> records = dm.getMatchRecords();
        tableModel.setRowCount(0);
        for (MatchRecord r : records) {
            String duration = (r.getDurationSeconds() / 60) + ":" + String.format("%02d", r.getDurationSeconds() % 60);
            String redTeam = getTeamNameForSide(r, "红方");
            String blueTeam = getTeamNameForSide(r, "蓝方");
            tableModel.addRow(new Object[]{
                r.getMatchId(), r.getMatchMode(),
                r.getMatchTime().toLocalDate().toString(),
                duration, r.getResult(), redTeam, blueTeam
            });
        }
    }

    private String getTeamNameForSide(MatchRecord r, String side) {
        GameDataManager dm = mainFrame.getDataManager();
        for (model.MatchParticipant p : r.getParticipants()) {
            if (side.equals(p.getTeamSide()) && p.getTeamId() != null) {
                model.Team t = dm.findTeamById(p.getTeamId());
                if (t != null) return t.getShortName();
            }
        }
        return side;
    }

    public void refreshData() { loadMatchData(); }
}
