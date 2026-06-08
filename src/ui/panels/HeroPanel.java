package ui.panels;

import model.Equipment;
import model.Hero;
import service.GameDataManager;
import ui.MainFrame;
import ui.dialogs.HeroDetailDialog;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class HeroPanel extends JPanel {
    private final MainFrame mainFrame;
    private JTable heroTable;
    private HeroTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<HeroTableModel> sorter;

    public HeroPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 10));
        initSearchBar();
        initTable();
    }

    private void initSearchBar() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("\u641C\u7D22\u82F1\u96C4:"));

        searchField = new JTextField(20);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });
        topPanel.add(searchField);

        add(topPanel, BorderLayout.NORTH);
    }

    private void initTable() {
        GameDataManager dm = mainFrame.getDataManager();
        tableModel = new HeroTableModel(dm.getHeroes());
        heroTable = new JTable(tableModel);

        sorter = new TableRowSorter<>(tableModel);
        heroTable.setRowSorter(sorter);

        heroTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        heroTable.setFillsViewportHeight(true);
        heroTable.setRowHeight(26);
        heroTable.getTableHeader().setReorderingAllowed(false);
        heroTable.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        heroTable.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));

        heroTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        heroTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        heroTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        heroTable.getColumnModel().getColumn(3).setPreferredWidth(70);
        heroTable.getColumnModel().getColumn(4).setPreferredWidth(70);
        heroTable.getColumnModel().getColumn(5).setPreferredWidth(60);

        heroTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = heroTable.getSelectedRow();
                    if (row >= 0) {
                        int modelRow = heroTable.convertRowIndexToModel(row);
                        Hero hero = tableModel.getHeroAt(modelRow);
                        HeroDetailDialog dialog = new HeroDetailDialog(
                                (JFrame) SwingUtilities.getWindowAncestor(HeroPanel.this),
                                hero, mainFrame.getDataManager());
                        dialog.setVisible(true);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(heroTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void filterTable() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0, 1, 2, 3, 4, 5));
        }
    }

    public void refreshData() {
        tableModel.setHeroes(mainFrame.getDataManager().getHeroes());
    }

    static class HeroTableModel extends AbstractTableModel {
        private final String[] columns = {"\u7F16\u53F7", "\u540D\u79F0", "\u79F0\u53F7", "\u5B9A\u4F4D", "\u7C7B\u578B", "\u96BE\u5EA6"};
        private List<Hero> heroes;

        HeroTableModel(List<Hero> heroes) {
            this.heroes = heroes;
        }

        void setHeroes(List<Hero> heroes) {
            this.heroes = heroes;
            fireTableDataChanged();
        }

        Hero getHeroAt(int row) {
            return heroes.get(row);
        }

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
}
