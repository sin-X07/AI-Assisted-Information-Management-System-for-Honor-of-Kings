package ui.dialogs;

import model.Equipment;

import javax.swing.*;
import java.awt.*;

public class EquipmentDetailDialog extends JDialog {
    public EquipmentDetailDialog(JFrame parent, Equipment eq) {
        super(parent, "\u88C5\u5907\u8BE6\u60C5 - " + eq.getEquipmentName(), true);
        setSize(440, 480);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        addField(mainPanel, "\u7F16\u53F7", eq.getEquipmentId());
        addField(mainPanel, "\u540D\u79F0", eq.getEquipmentName());
        addField(mainPanel, "\u7C7B\u578B", eq.getEquipmentType());
        addField(mainPanel, "\u4EF7\u683C", eq.getPrice() + " \u91D1\u5E01");

        mainPanel.add(Box.createVerticalStrut(8));
        addSection(mainPanel, "\u5C5E\u6027\u52A0\u6210");
        addField(mainPanel, "\u653B\u51FB", val(eq.getAttackBonus()));
        addField(mainPanel, "\u6CD5\u672F\u653B\u51FB", val(eq.getMagicAttackBonus()));
        addField(mainPanel, "\u751F\u547D\u503C", val(eq.getHealthBonus()));
        addField(mainPanel, "\u6CD5\u529B\u503C", val(eq.getManaBonus()));
        addField(mainPanel, "\u7269\u7406\u9632\u5FA1", val(eq.getArmorBonus()));
        addField(mainPanel, "\u6CD5\u672F\u9632\u5FA1", val(eq.getMagicResistanceBonus()));
        addField(mainPanel, "\u51B7\u5374\u7F29\u51CF", pct(eq.getCooldownReduction()));
        addField(mainPanel, "\u66B4\u51FB\u7387", pct(eq.getCriticalRate()));
        addField(mainPanel, "\u79FB\u52A8\u901F\u5EA6", pct(eq.getMovementSpeed()));

        mainPanel.add(Box.createVerticalStrut(8));
        addSection(mainPanel, "\u88AB\u52A8\u6548\u679C");
        JLabel passive = new JLabel("  " + (eq.getPassiveEffect() != null ? eq.getPassiveEffect() : "\u65E0"));
        passive.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        passive.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(passive);

        if (eq.getSuitableHeroTypes() != null && !eq.getSuitableHeroTypes().isEmpty()) {
            mainPanel.add(Box.createVerticalStrut(8));
            addSection(mainPanel, "\u9002\u5408\u82F1\u96C4\u7C7B\u578B");
            JLabel types = new JLabel("  " + String.join(", ", eq.getSuitableHeroTypes()));
            types.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
            types.setAlignmentX(Component.LEFT_ALIGNMENT);
            mainPanel.add(types);
        }

        if (eq.getDescription() != null && !eq.getDescription().isEmpty()) {
            mainPanel.add(Box.createVerticalStrut(8));
            addSection(mainPanel, "\u63CF\u8FF0");
            JLabel desc = new JLabel("  " + eq.getDescription());
            desc.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
            desc.setAlignmentX(Component.LEFT_ALIGNMENT);
            mainPanel.add(desc);
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(scrollPane);
    }

    private void addField(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        lbl.setPreferredSize(new Dimension(90, 25));
        row.add(lbl, BorderLayout.WEST);
        JLabel val = new JLabel(value);
        val.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        row.add(val, BorderLayout.CENTER);
        panel.add(row);
    }

    private void addSection(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
    }

    private String val(int v) { return v > 0 ? "+" + v : String.valueOf(v); }
    private String pct(double v) { return v > 0 ? String.format("+%.0f%%", v * 100) : "0%"; }
}
