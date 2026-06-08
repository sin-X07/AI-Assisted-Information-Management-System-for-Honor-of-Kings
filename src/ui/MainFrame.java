package ui;

import model.Person;
import service.FileStorageService;
import service.GameDataManager;
import service.RankingService;
import ui.panels.*;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final Person currentUser;
    private final GameDataManager dataManager;
    private final RankingService rankingService;
    private final FileStorageService fileStorageService;

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel sidebarPanel;
    private JLabel statusLabel;

    private WelcomePanel welcomePanel;
    private HeroPanel heroPanel;
    private EquipmentPanel equipmentPanel;
    private TeamPanel teamPanel;
    private PlayerQueryPanel playerQueryPanel;
    private RankingPanel rankingPanel;
    private DataManagementPanel dataManagementPanel;

    private JButton heroBtn;
    private JButton equipBtn;
    private JButton teamBtn;
    private JButton playerBtn;
    private JButton rankingBtn;
    private JButton dataMgmtBtn;
    private JButton exportBtn;

    public MainFrame(Person user, GameDataManager dataManager,
                     RankingService rankingService, FileStorageService fileStorageService) {
        this.currentUser = user;
        this.dataManager = dataManager;
        this.rankingService = rankingService;
        this.fileStorageService = fileStorageService;

        setTitle("\u738B\u8005\u8363\u8000\u4FE1\u606F\u7BA1\u7406\u7CFB\u7EDF");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1024, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 550));

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        initSidebar();
        initContentPanel();
        initStatusBar();
    }

    private void initSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(180, 0));
        sidebarPanel.setLayout(new BorderLayout());

        JPanel sidebarContent = new JPanel();
        sidebarContent.setLayout(new BoxLayout(sidebarContent, BoxLayout.Y_AXIS));
        sidebarContent.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));

        JLabel navTitle = new JLabel("\u83DC\u5355\u5BFC\u822A");
        navTitle.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        navTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        navTitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));
        sidebarContent.add(navTitle);

        Dimension btnSize = new Dimension(160, 38);

        JButton homeBtn = createNavButton("\u9996\u9875", btnSize);
        homeBtn.addActionListener(e -> showPanel("welcome"));
        sidebarContent.add(homeBtn);
        sidebarContent.add(Box.createVerticalStrut(4));

        heroBtn = createNavButton("\u82F1\u96C4\u5217\u8868", btnSize);
        heroBtn.addActionListener(e -> showPanel("hero"));
        sidebarContent.add(heroBtn);
        sidebarContent.add(Box.createVerticalStrut(4));

        equipBtn = createNavButton("\u88C5\u5907\u5217\u8868", btnSize);
        equipBtn.addActionListener(e -> showPanel("equipment"));
        sidebarContent.add(equipBtn);
        sidebarContent.add(Box.createVerticalStrut(4));

        teamBtn = createNavButton("\u6218\u961F\u5217\u8868", btnSize);
        teamBtn.addActionListener(e -> showPanel("team"));
        sidebarContent.add(teamBtn);
        sidebarContent.add(Box.createVerticalStrut(4));

        playerBtn = createNavButton("\u73A9\u5BB6\u67E5\u8BE2", btnSize);
        playerBtn.addActionListener(e -> showPanel("player"));
        sidebarContent.add(playerBtn);
        sidebarContent.add(Box.createVerticalStrut(4));

        rankingBtn = createNavButton("\u6392\u884C\u699C", btnSize);
        rankingBtn.addActionListener(e -> showPanel("ranking"));
        sidebarContent.add(rankingBtn);

        boolean isAdmin = "ADMIN".equals(currentUser.getRole());
        if (isAdmin) {
            sidebarContent.add(Box.createVerticalStrut(20));
            JSeparator sep = new JSeparator();
            sep.setMaximumSize(new Dimension(160, 2));
            sidebarContent.add(sep);
            sidebarContent.add(Box.createVerticalStrut(10));

            dataMgmtBtn = createNavButton("\u6570\u636E\u7BA1\u7406", btnSize);
            dataMgmtBtn.addActionListener(e -> showPanel("datamgmt"));
            sidebarContent.add(dataMgmtBtn);
            sidebarContent.add(Box.createVerticalStrut(4));

            exportBtn = createNavButton("\u5BFC\u51FA\u6392\u884C\u699C", btnSize);
            exportBtn.addActionListener(e -> doExportRanking());
            sidebarContent.add(exportBtn);
        }

        sidebarContent.add(Box.createVerticalGlue());

        JButton logoutBtn = createNavButton("\u9000\u51FA\u767B\u5F55", btnSize);
        logoutBtn.addActionListener(e -> {
            dispose();
            AppLauncher.main(new String[0]);
        });
        sidebarContent.add(logoutBtn);

        sidebarPanel.add(sidebarContent, BorderLayout.CENTER);
        add(sidebarPanel, BorderLayout.WEST);
    }

    private JButton createNavButton(String text, Dimension size) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(size);
        btn.setPreferredSize(size);
        btn.setMinimumSize(size);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        btn.setFocusPainted(false);
        return btn;
    }

    private void initContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        welcomePanel = new WelcomePanel(currentUser);
        heroPanel = new HeroPanel(this);
        equipmentPanel = new EquipmentPanel(this);
        teamPanel = new TeamPanel(this);
        playerQueryPanel = new PlayerQueryPanel(this);
        rankingPanel = new RankingPanel(this);
        dataManagementPanel = new DataManagementPanel(this);

        contentPanel.add(welcomePanel, "welcome");
        contentPanel.add(heroPanel, "hero");
        contentPanel.add(equipmentPanel, "equipment");
        contentPanel.add(teamPanel, "team");
        contentPanel.add(playerQueryPanel, "player");
        contentPanel.add(rankingPanel, "ranking");
        contentPanel.add(dataManagementPanel, "datamgmt");

        add(contentPanel, BorderLayout.CENTER);
        cardLayout.show(contentPanel, "welcome");
    }

    private void initStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        String roleText = "ADMIN".equals(currentUser.getRole()) ? "\u7BA1\u7406\u5458" : "\u73A9\u5BB6";
        statusLabel = new JLabel("\u5F53\u524D\u7528\u6237: " + currentUser.getNickname()
                + " (" + roleText + ")");
        statusLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        statusBar.add(statusLabel, BorderLayout.WEST);

        add(statusBar, BorderLayout.SOUTH);
    }

    public void showPanel(String name) {
        if ("ranking".equals(name)) {
            rankingPanel.refreshData();
        } else if ("datamgmt".equals(name)) {
            dataManagementPanel.refreshData();
        } else if ("hero".equals(name)) {
            heroPanel.refreshData();
        } else if ("equipment".equals(name)) {
            equipmentPanel.refreshData();
        } else if ("team".equals(name)) {
            teamPanel.refreshData();
        }
        cardLayout.show(contentPanel, name);
    }

    public GameDataManager getDataManager() {
        return dataManager;
    }

    public RankingService getRankingService() {
        return rankingService;
    }

    public FileStorageService getFileStorageService() {
        return fileStorageService;
    }

    private void doExportRanking() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("\u5BFC\u51FA\u6392\u884C\u699C");
        fileChooser.setSelectedFile(new java.io.File("ranking.txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            fileStorageService.exportRankingToFile(dataManager, path);
            JOptionPane.showMessageDialog(this, "\u6392\u884C\u699C\u5DF2\u6210\u529F\u5BFC\u51FA\u5230:\n" + path,
                    "\u5BFC\u51FA\u6210\u529F", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
