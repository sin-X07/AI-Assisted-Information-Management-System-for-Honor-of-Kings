package ui.dialogs;

import model.Hero;
import service.GameDataManager;

import javax.swing.*;
import java.awt.*;

public class HeroEditDialog extends JDialog {
    private final GameDataManager dm;
    private final Hero existingHero;
    private boolean confirmed = false;
    private Hero resultHero;

    private JTextField idField, nameField, titleField, positionField, typeField;
    private JTextField difficultyField, summonerField, descField;
    private JSpinner survivalSpinner, attackSpinner, skillSpinner, supportSpinner;
    private JTextField passiveField, skill1Field, skill2Field, skill3Field;

    public HeroEditDialog(JFrame parent, GameDataManager dm, Hero existing) {
        super(parent, existing == null ? "\u6DFB\u52A0\u82F1\u96C4" : "\u7F16\u8F91\u82F1\u96C4", true);
        this.dm = dm;
        this.existingHero = existing;
        setSize(450, 600);
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
        idField = addRow(mainPanel, gbc, row++, "\u7F16\u53F7:", val(existingHero != null ? existingHero.getHeroId() : ""));
        nameField = addRow(mainPanel, gbc, row++, "\u540D\u79F0:", val(existingHero != null ? existingHero.getHeroName() : ""));
        titleField = addRow(mainPanel, gbc, row++, "\u79F0\u53F7:", val(existingHero != null ? existingHero.getTitle() : ""));
        positionField = addRow(mainPanel, gbc, row++, "\u5B9A\u4F4D:", val(existingHero != null ? existingHero.getPosition() : ""));
        typeField = addRow(mainPanel, gbc, row++, "\u7C7B\u578B:", val(existingHero != null ? existingHero.getHeroType() : ""));
        difficultyField = addRow(mainPanel, gbc, row++, "\u96BE\u5EA6:", val(existingHero != null ? existingHero.getDifficulty() : ""));
        summonerField = addRow(mainPanel, gbc, row++, "\u53EC\u5524\u5E08\u6280\u80FD:", val(existingHero != null ? existingHero.getRecommendedSummonerSkill() : ""));
        descField = addRow(mainPanel, gbc, row++, "\u63CF\u8FF0:", val(existingHero != null ? existingHero.getDescription() : ""));

        // Ability spinners
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        mainPanel.add(new JLabel("\u80FD\u529B:"), gbc);
        JPanel abilityPanel = new JPanel(new GridLayout(1, 8, 3, 0));
        abilityPanel.add(new JLabel("\u751F\u5B58"));
        survivalSpinner = new JSpinner(new SpinnerNumberModel(existingHero != null ? existingHero.getSurvivalAbility() : 0, 0, 10, 1));
        abilityPanel.add(survivalSpinner);
        abilityPanel.add(new JLabel("\u653B\u51FB"));
        attackSpinner = new JSpinner(new SpinnerNumberModel(existingHero != null ? existingHero.getAttackAbility() : 0, 0, 10, 1));
        abilityPanel.add(attackSpinner);
        abilityPanel.add(new JLabel("\u6280\u80FD"));
        skillSpinner = new JSpinner(new SpinnerNumberModel(existingHero != null ? existingHero.getSkillAbility() : 0, 0, 10, 1));
        abilityPanel.add(skillSpinner);
        abilityPanel.add(new JLabel("\u8F85\u52A9"));
        supportSpinner = new JSpinner(new SpinnerNumberModel(existingHero != null ? existingHero.getSupportAbility() : 0, 0, 10, 1));
        abilityPanel.add(supportSpinner);
        gbc.gridx = 1; gbc.gridwidth = 1;
        mainPanel.add(abilityPanel, gbc);
        row++;

        passiveField = addRow(mainPanel, gbc, row++, "\u88AB\u52A8:", val(existingHero != null ? existingHero.getPassiveSkill() : ""));
        skill1Field = addRow(mainPanel, gbc, row++, "\u6280\u80FD1:", val(existingHero != null ? existingHero.getSkillOne() : ""));
        skill2Field = addRow(mainPanel, gbc, row++, "\u6280\u80FD2:", val(existingHero != null ? existingHero.getSkillTwo() : ""));
        skill3Field = addRow(mainPanel, gbc, row++, "\u6280\u80FD3:", val(existingHero != null ? existingHero.getSkillThree() : ""));

        // Buttons
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

        gbc.gridx = 1; gbc.gridwidth = 1;
        JTextField field = new JTextField(value, 20);
        panel.add(field, gbc);
        return field;
    }

    private void doSave() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "\u7F16\u53F7\u4E0D\u80FD\u4E3A\u7A7A");
            return;
        }

        Hero existing = dm.findHeroById(id);
        if (existing != null && existingHero == null) {
            JOptionPane.showMessageDialog(this, "\u7F16\u53F7\u5DF2\u5B58\u5728");
            return;
        }

        Hero hero = new Hero();
        hero.setHeroId(id);
        hero.setHeroName(nameField.getText().trim());
        hero.setTitle(titleField.getText().trim());
        hero.setPosition(positionField.getText().trim());
        hero.setHeroType(typeField.getText().trim());
        hero.setDifficulty(difficultyField.getText().trim());
        hero.setRecommendedSummonerSkill(summonerField.getText().trim());
        hero.setDescription(descField.getText().trim());
        hero.setSurvivalAbility((int) survivalSpinner.getValue());
        hero.setAttackAbility((int) attackSpinner.getValue());
        hero.setSkillAbility((int) skillSpinner.getValue());
        hero.setSupportAbility((int) supportSpinner.getValue());
        hero.setPassiveSkill(passiveField.getText().trim());
        hero.setSkillOne(skill1Field.getText().trim());
        hero.setSkillTwo(skill2Field.getText().trim());
        hero.setSkillThree(skill3Field.getText().trim());

        resultHero = hero;
        confirmed = true;
        dispose();
    }

    private String val(String s) { return s != null ? s : ""; }
    public boolean isConfirmed() { return confirmed; }
    public Hero getHero() { return resultHero; }
}
