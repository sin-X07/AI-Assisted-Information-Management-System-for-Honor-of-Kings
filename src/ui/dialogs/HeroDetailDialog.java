package ui.dialogs;

import model.Equipment;
import model.Hero;
import service.GameDataManager;

import javax.swing.*;
import java.awt.*;

public class HeroDetailDialog extends JDialog {
    public HeroDetailDialog(JFrame parent, Hero hero, GameDataManager dm) {
        super(parent, "\u82F1\u96C4\u8BE6\u60C5 - " + hero.getHeroName(), true);
        setSize(480, 550);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        addField(mainPanel, "\u7F16\u53F7", hero.getHeroId());
        addField(mainPanel, "\u540D\u79F0", hero.getHeroName());
        addField(mainPanel, "\u79F0\u53F7", hero.getTitle());
        addField(mainPanel, "\u5B9A\u4F4D", hero.getPosition());
        addField(mainPanel, "\u7C7B\u578B", hero.getHeroType());
        addField(mainPanel, "\u96BE\u5EA6", hero.getDifficulty());
        addField(mainPanel, "\u53EC\u5524\u5E08\u6280\u80FD", hero.getRecommendedSummonerSkill());

        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(sectionLabel("\u80FD\u529B\u6570\u503C"));
        addField(mainPanel, "\u751F\u5B58", String.valueOf(hero.getSurvivalAbility()));
        addField(mainPanel, "\u653B\u51FB", String.valueOf(hero.getAttackAbility()));
        addField(mainPanel, "\u6280\u80FD", String.valueOf(hero.getSkillAbility()));
        addField(mainPanel, "\u8F85\u52A9", String.valueOf(hero.getSupportAbility()));

        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(sectionLabel("\u6280\u80FD"));
        addField(mainPanel, "\u88AB\u52A8", orEmpty(hero.getPassiveSkill()));
        addField(mainPanel, "\u6280\u80FD1", orEmpty(hero.getSkillOne()));
        addField(mainPanel, "\u6280\u80FD2", orEmpty(hero.getSkillTwo()));
        addField(mainPanel, "\u6280\u80FD3", orEmpty(hero.getSkillThree()));

        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(sectionLabel("\u63A8\u8350\u88C5\u5907"));
        if (hero.getRecommendedEquipmentIds().isEmpty()) {
            addFieldValue(mainPanel, "  \u65E0");
        } else {
            for (String eqId : hero.getRecommendedEquipmentIds()) {
                Equipment eq = dm.findEquipmentById(eqId);
                String name = eq != null ? eq.getEquipmentName() + " (" + eq.getEquipmentType() + ", " + eq.getPrice() + "\u91D1\u5E01)" : eqId;
                addFieldValue(mainPanel, "  " + name);
            }
        }

        if (hero.getDescription() != null && !hero.getDescription().isEmpty()) {
            mainPanel.add(Box.createVerticalStrut(8));
            mainPanel.add(sectionLabel("\u63CF\u8FF0"));
            addFieldValue(mainPanel, "  " + hero.getDescription());
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        add(scrollPane);
    }

    private void addField(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        lbl.setPreferredSize(new Dimension(80, 25));
        row.add(lbl, BorderLayout.WEST);
        JLabel val = new JLabel(value != null ? value : "");
        val.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        row.add(val, BorderLayout.CENTER);
        panel.add(row);
    }

    private void addFieldValue(JPanel panel, String value) {
        JLabel val = new JLabel(value);
        val.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        val.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(val);
    }

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        return label;
    }

    private String orEmpty(String s) { return s != null ? s : ""; }
}
