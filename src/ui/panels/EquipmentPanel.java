package ui.panels;

import model.Equipment;
import service.GameDataManager;
import ui.MainFrame;
import ui.dialogs.EquipmentDetailDialog;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class EquipmentPanel extends JPanel {
    private final MainFrame mainFrame;
    private JTable equipTable;
    private EquipmentTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> typeFilter;
    private TableRowSorter<EquipmentTableModel> sorter;

    private static final String[] TYPES = {"\u5168\u90E8", "\u653B\u51FB", "\u6CD5\u672F", "\u9632\u5FA1", "\u79FB\u52A8", "\u6253\u91CE", "\u8F85\u52A9"};

    public EquipmentPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(0, 10));
        initSearchBar();
        initTable();
    }

    private void initSearchBar() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("\u641C\u7D22\u88C5\u5907:"));

        searchField = new JTextField(15);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
        });
        topPanel.add(searchField);

        topPanel.add(new JLabel("\u7C7B\u578B:"));
        typeFilter = new JComboBox<>(TYPES);
        typeFilter.addActionListener(e -> applyFilter());
        topPanel.add(typeFilter);

        add(topPanel, BorderLayout.NORTH);
    }

    private void initTable() {
        GameDataManager dm = mainFrame.getDataManager();
        tableModel = new EquipmentTableModel(dm.getEquipments());
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

        RowFilter<EquipmentTableModel, Integer> textFilter = null;
        RowFilter<EquipmentTableModel, Integer> typeFilterObj = null;

        if (!text.isEmpty()) {
            textFilter = RowFilter.regexFilter("(?i)" + text, 0, 1, 2);
        }

        if (selectedType != null && !"\u5168\u90E8".equals(selectedType)) {
            typeFilterObj = RowFilter.regexFilter(selectedType, 2);
        }

        if (textFilter != null && typeFilterObj != null) {
            sorter.setRowFilter(RowFilter.andFilter(List.of(textFilter, typeFilterObj)));
        } else if (textFilter != null) {
            sorter.setRowFilter(textFilter);
        } else if (typeFilterObj != null) {
            sorter.setRowFilter(typeFilterObj);
        } else {
            sorter.setRowFilter(null);
        }
    }

    public void refreshData() {
        tableModel.setEquipments(mainFrame.getDataManager().getEquipments());
    }

    static class EquipmentTableModel extends AbstractTableModel {
        private final String[] columns = {"\u7F16\u53F7", "\u540D\u79F0", "\u7C7B\u578B", "\u4EF7\u683C(\u91D1\u5E01)"};
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
