package ui;

import model.Person;
import service.AuthenticationService;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {
    private final AuthenticationService authService;
    private Person loggedInUser;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginDialog(JFrame parent, AuthenticationService authService) {
        super(parent, "\u738B\u8005\u8363\u8000\u4FE1\u606F\u7BA1\u7406\u7CFB\u7EDF - \u767B\u5F55", true);
        this.authService = authService;
        this.loggedInUser = null;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 280);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("\u738B\u8005\u8363\u8000\u4FE1\u606F\u7BA1\u7406\u7CFB\u7EDF");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 20, 5);
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("\u7528\u6237\u540D:"), gbc);

        usernameField = new JTextField(15);
        usernameField.setText("admin");
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("\u5BC6\u7801:"), gbc);

        passwordField = new JPasswordField(15);
        passwordField.setText("123456");
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        JButton loginButton = new JButton("\u767B\u5F55");
        loginButton.addActionListener(e -> doLogin());
        mainPanel.add(loginButton, gbc);

        JLabel hintLabel = new JLabel("\u9ED8\u8BA4\uFF1Aadmin/123456 \u6216 player/123456");
        hintLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        hintLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 5, 5, 5);
        mainPanel.add(hintLabel, gbc);

        add(mainPanel);

        getRootPane().setDefaultButton(loginButton);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "\u8BF7\u8F93\u5165\u7528\u6237\u540D\u548C\u5BC6\u7801",
                    "\u63D0\u793A", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Person user = authService.login(username, password);
        if (user != null) {
            loggedInUser = user;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "\u7528\u6237\u540D\u6216\u5BC6\u7801\u9519\u8BEF\uFF0C\u8BF7\u91CD\u8BD5",
                    "\u767B\u5F55\u5931\u8D25", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Person getLoggedInUser() {
        return loggedInUser;
    }
}
