package ui.dialogs;

import model.Equipment;
import service.GameDataManager;

import javax.swing.*;
import java.awt.*;

public class EquipmentEditDialog extends JDialog {
    private final GameDataManager dm;
    private final Equipment existingEquip;
    private boolean confirmed = false;
    private Equipment resultEquip;

    private JTextField idField, nameField, typeField, priceField;
    private JSpinner atkSpinner, matkSpinner, hpSpinner, mpSpinner;
    private JSpinner armorSpinner, mresSpinner;
    private JTextField cdrField, critField, speedField;
    private JTextField passiveField, descField;

    public EquipmentEditDialog(JFrame parent, GameDataManager dm, Equipment existing) {
        super(parent, existing == null ? "\u6DFB\u52A0\u88C5\u5907" : "\u7F16\u8F91\u88C5\u5907", true);
        this.dm = dm;
        this.existingEquip = existing;
        setSize(450, 550);
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
        idField = addRow(mainPanel, gbc, row++, "\u7F16\u53F7:", sval(existingEquip != null ? existingEquip.getEquipmentId() : null));
        nameField = addRow(mainPanel, gbc, row++, "\u540D\u79F0:", sval(existingEquip != null ? existingEquip.getEquipmentName() : null));
        typeField = addRow(mainPanel, gbc, row++, "\u7C7B\u578B:", sval(existingEquip != null ? existingEquip.getEquipmentType() : null));
        priceField = addRow(mainPanel, gbc, row++, "\u4EF7\u683C:", existingEquip != null ? String.valueOf(existingEquip.getPrice()) : "0");

        atkSpinner = addSpinnerRow(mainPanel, gbc, row++, "\u653B\u51FB:", existingEquip != null ? existingEquip.getAttackBonus() : 0);
        matkSpinner = addSpinnerRow(mainPanel, gbc, row++, "\u6CD5\u672F\u653B\u51FB:", existingEquip != null ? existingEquip.getMagicAttackBonus() : 0);
        hpSpinner = addSpinnerRow(mainPanel, gbc, row++, "\u751F\u547D\u503C:", existingEquip != null ? existingEquip.getHealthBonus() : 0);
        mpSpinner = addSpinnerRow(mainPanel, gbc, row++, "\u6CD5\u529B\u503C:", existingEquip != null ? existingEquip.getManaBonus() : 0);
        armorSpinner = addSpinnerRow(mainPanel, gbc, row++, "\u7269\u7406\u9632\u5FA1:", existingEquip != null ? existingEquip.getArmorBonus() : 0);
        mresSpinner = addSpinnerRow(mainPanel, gbc, row++, "\u6CD5\u672F\u9632\u5FA1:", existingEquip != null ? existingEquip.getMagicResistanceBonus() : 0);

        cdrField = addRow(mainPanel, gbc, row++, "\u51B7\u5374\u7F29\u51CF:", existingEquip != null ? String.valueOf(existingEquip.getCooldownReduction()) : "0.0");
        critField = addRow(mainPanel, gbc, row++, "\u66B4\u51FB\u7387:", existingEquip != null ? String.valueOf(existingEquip.getCriticalRate()) : "0.0");
        speedField = addRow(mainPanel, gbc, row++, "\u79FB\u52A8\u901F\u5EA6:", existingEquip != null ? String.valueOf(existingEquip.getMovementSpeed()) : "0.0");

        passiveField = addRow(mainPanel, gbc, row++, "\u88AB\u52A8:", sval(existingEquip != null ? existingEquip.getPassiveEffect() : null));
        descField = addRow(mainPanel, gbc, row++, "\u63CF\u8FF0:", sval(existingEquip != null ? existingEquip.getDescription() : null));

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
        lbl.setPreferredSize(new Dimension(110, 25));
        panel.add(lbl, gbc);
        gbc.gridx = 1;
        JTextField field = new JTextField(value, 20);
        panel.add(field, gbc);
        return field;
    }

    private JSpinner addSpinnerRow(JPanel panel, GridBagConstraints gbc, int row, String label, int value) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        lbl.setPreferredSize(new Dimension(110, 25));
        panel.add(lbl, gbc);
        gbc.gridx = 1;
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, 0, 999, 5));
        panel.add(spinner, gbc);
        return spinner;
    }

    private void doSave() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "\u7F16\u53F7\u4E0D\u80FD\u4E3A\u7A7A");
            return;
        }

        Equipment existing = dm.findEquipmentById(id);
        if (existing != null && existingEquip == null) {
            JOptionPane.showMessageDialog(this, "\u7F16\u53F7\u5DF2\u5B58\u5728");
            return;
        }

        Equipment eq = new Equipment();
        eq.setEquipmentId(id);
        eq.setEquipmentName(nameField.getText().trim());
        eq.setEquipmentType(typeField.getText().trim());
        eq.setPrice(parseInt(priceField.getText(), 0));
        eq.setAttackBonus((int) atkSpinner.getValue());
        eq.setMagicAttackBonus((int) matkSpinner.getValue());
        eq.setHealthBonus((int) hpSpinner.getValue());
        eq.setManaBonus((int) mpSpinner.getValue());
        eq.setArmorBonus((int) armorSpinner.getValue());
        eq.setMagicResistanceBonus((int) mresSpinner.getValue());
        eq.setCooldownReduction(parseDouble(cdrField.getText(), 0.0));
        eq.setCriticalRate(parseDouble(critField.getText(), 0.0));
        eq.setMovementSpeed(parseDouble(speedField.getText(), 0.0));
        eq.setPassiveEffect(passiveField.getText().trim());
        eq.setDescription(descField.getText().trim());

        resultEquip = eq;
        confirmed = true;
        dispose();
    }

    private int parseInt(String s, int def) {
        try { return Integer.parseInt(s.trim()); } catch (NumberFormatException e) { return def; }
    }

    private double parseDouble(String s, double def) {
        try { return Double.parseDouble(s.trim()); } catch (NumberFormatException e) { return def; }
    }

    private String sval(String s) { return s != null ? s : ""; }
    public boolean isConfirmed() { return confirmed; }
    public Equipment getEquipment() { return resultEquip; }
}
