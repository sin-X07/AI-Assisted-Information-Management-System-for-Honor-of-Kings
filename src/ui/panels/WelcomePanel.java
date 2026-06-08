package ui.panels;

import model.Person;

import javax.swing.*;
import java.awt.*;

public class WelcomePanel extends JPanel {
    private final Person currentUser;

    public WelcomePanel(Person currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        JLabel welcomeLabel = new JLabel("\u6B22\u8FCE\u4F7F\u7528\u738B\u8005\u8363\u8000\u4FE1\u606F\u7BA1\u7406\u7CFB\u7EDF");
        welcomeLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 22));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(welcomeLabel, gbc);

        gbc.insets = new Insets(5, 10, 5, 10);

        JLabel userLabel = new JLabel("\u5F53\u524D\u7528\u6237: " + currentUser.getNickname());
        userLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        userLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(userLabel, gbc);

        String roleText = "ADMIN".equals(currentUser.getRole()) ? "\u7BA1\u7406\u5458" : "\u73A9\u5BB6";
        JLabel roleLabel = new JLabel("\u89D2\u8272: " + roleText);
        roleLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        roleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(roleLabel, gbc);

        gbc.insets = new Insets(20, 10, 10, 10);

        JLabel navHint = new JLabel("\u8BF7\u901A\u8FC7\u5DE6\u4FA7\u5BFC\u822A\u83DC\u5355\u9009\u62E9\u529F\u80FD");
        navHint.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        navHint.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(navHint, gbc);

        add(centerPanel, BorderLayout.CENTER);
    }
}
