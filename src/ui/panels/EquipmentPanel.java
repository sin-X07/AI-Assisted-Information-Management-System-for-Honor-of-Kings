package ui.panels;

import model.Equipment;
import service.GameDataManager;
import ui.MainFrame;
import ui.dialogs.EquipmentDetailDialog;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentPanel extends JPanel {
    private final MainFrame mainFrame;
    private JTable equipTable;
    private EquipmentTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> typeFilter;
    private TableRowSorter<EquipmentTableModel> sorter;

    private static final String[] TYPES = {"全部", "攻击", "法术", "防御", "移动", "打野", "辅助"};

    public EquipmentPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 10));
        initSearchBar();
        initTable();
    }

    private void initSearchBar() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("搜索装备:"));

        searchField = new JTextField(15);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
        });
        topPanel.add(searchField);

        topPanel.add(new JLabel("类型:"));
        typeFilter = new JComboBox<>(TYPES);
        typeFilter.addActionListener(e -> applyFilter());
        topPanel.add(typeFilter);
        JButton refreshBtn = new JButton("刷新");
        refreshBtn.addActionListener(e -> refreshData());
        topPanel.add(refreshBtn);

        add(topPanel, BorderLayout.NORTH);
    }

    private void initTable() {
        GameDataManager dm = mainFrame.getDataManager();
        tableModel = new EquipmentTableModel(new ArrayList<>(dm.getEquipments()));
        equipTable = new JTable(tableModel);

        sorter = new TableRowSorter<>(tableModel);
        equipTable.setRowSorter(sorter);

        equipTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        equipTable.setFillsViewportHeight(true);
        equipTable.setRowHeight(26);
        equipTable.getTableHeader().setReorderingAllowed(false);
        equipTable.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        equipTable.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));

        equipTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        equipTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        equipTable.getColumnModel().getColumn(2).setPreferredWidth(70);
        equipTable.getColumnModel().getColumn(3).setPreferredWidth(80);

        equipTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = equipTable.getSelectedRow();
                    if (row >= 0) {
                        int modelRow = equipTable.convertRowIndexToModel(row);
                        Equipment eq = tableModel.getEquipmentAt(modelRow);
                        EquipmentDetailDialog dialog = new EquipmentDetailDialog(
                                (JFrame) SwingUtilities.getWindowAncestor(EquipmentPanel.this),
                                eq);
                        dialog.setVisible(true);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(equipTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void applyFilter() {
        String text = searchField.getText().trim();
        String selectedType = (String) typeFilter.getSelectedItem();

        List<RowFilter<EquipmentTableModel, Integer>> filters = new ArrayList<>();

        // Fuzzy search: case-insensitive contains across ID, name, type (columns 0, 1, 2)
        if (!text.isEmpty()) {
            String lower = text.toLowerCase();
            filters.add(new RowFilter<EquipmentTableModel, Integer>() {
                @Override
                public boolean include(Entry<? extends EquipmentTableModel, ? extends Integer> entry) {
                    for (int i = 0; i <= 2; i++) {
                        Object val = entry.getValue(i);
                        if (val != null && val.toString().toLowerCase().contains(lower)) {
                            return true;
                        }
                    }
                    return false;
                }
            });
        }

        // Type filter on column 2
        if (selectedType != null && !"全部".equals(selectedType)) {
            String finalSelectedType = selectedType;
            filters.add(new RowFilter<EquipmentTableModel, Integer>() {
                @Override
                public boolean include(Entry<? extends EquipmentTableModel, ? extends Integer> entry) {
                    Object val = entry.getValue(2);
                    return val != null && finalSelectedType.equals(val.toString());
                }
            });
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else if (filters.size() == 1) {
            sorter.setRowFilter(filters.get(0));
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    public void refreshData() {
        tableModel.setEquipments(new ArrayList<>(mainFrame.getDataManager().getEquipments()));
    }

    static class EquipmentTableModel extends AbstractTableModel {
        private final String[] columns = {"编号", "名称", "类型", "价格(金币)"};
        private List<Equipment> equipments;

        EquipmentTableModel(List<Equipment> equipments) {
            this.equipments = equipments;
        }

        void setEquipments(List<Equipment> equipments) {
            this.equipments = equipments;
            fireTableDataChanged();
        }

        Equipment getEquipmentAt(int row) {
            return equipments.get(row);
        }

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
}
