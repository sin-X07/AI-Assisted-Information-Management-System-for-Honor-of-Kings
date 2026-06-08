package ui;

import model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.FileStorageService;
import service.GameDataManager;
import service.OperationLogService;
import service.RankingService;
import ui.panels.*;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private static final Logger log = LoggerFactory.getLogger(MainFrame.class);

    private final Person currentUser;
    private final GameDataManager dataManager;
    private final RankingService rankingService;
    private final FileStorageService fileStorageService;
    private final OperationLogService opLogService;

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
        this.opLogService = OperationLogService.getInstance();

        setTitle("\u738B\u8005\u8363\u8000\u4FE1\u606F\u7BA1\u7406\u7CFB\u7EDF");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1024, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 550));

        initComponents();

        log.info("MainFrame initialized for user [{}] role [{}].", user.getNickname(), user.getRole());
    }

    // ==== Getters for panels ====

    public Person getCurrentUser() { return currentUser; }
    public GameDataManager getDataManager() { return dataManager; }
    public RankingService getRankingService() { return rankingService; }
    public FileStorageService getFileStorageService() { return fileStorageService; }
    public OperationLogService getOperationLogService() { return opLogService; }

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

        JLabel navTitle = new JLabel("菜单导航");
        navTitle.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        navTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        navTitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));
        sidebarContent.add(navTitle);

        Dimension btnSize = new Dimension(160, 38);

        JButton homeBtn = createNavButton("首页", btnSize);
        homeBtn.addActionListener(e -> showPanel("welcome"));
        sidebarContent.add(homeBtn);
        sidebarContent.add(Box.createVerticalStrut(4));

        heroBtn = createNavButton("英雄列表", btnSize);
        heroBtn.addActionListener(e -> showPanel("hero"));
        sidebarContent.add(heroBtn);
        sidebarContent.add(Box.createVerticalStrut(4));

        equipBtn = createNavButton("装备列表", btnSize);
        equipBtn.addActionListener(e -> showPanel("equipment"));
        sidebarContent.add(equipBtn);
        sidebarContent.add(Box.createVerticalStrut(4));

        teamBtn = createNavButton("战队列表", btnSize);
        teamBtn.addActionListener(e -> showPanel("team"));
        sidebarContent.add(teamBtn);
        sidebarContent.add(Box.createVerticalStrut(4));

        playerBtn = createNavButton("玩家查询", btnSize);
        playerBtn.addActionListener(e -> showPanel("player"));
        sidebarContent.add(playerBtn);
        sidebarContent.add(Box.createVerticalStrut(4));

        rankingBtn = createNavButton("排行榜", btnSize);
        rankingBtn.addActionListener(e -> showPanel("ranking"));
        sidebarContent.add(rankingBtn);

        boolean isAdmin = "ADMIN".equals(currentUser.getRole());
        if (isAdmin) {
            sidebarContent.add(Box.createVerticalStrut(20));
            JSeparator sep = new JSeparator();
            sep.setMaximumSize(new Dimension(160, 2));
            sidebarContent.add(sep);
            sidebarContent.add(Box.createVerticalStrut(10));

            dataMgmtBtn = createNavButton("数据管理", btnSize);
            dataMgmtBtn.addActionListener(e -> showPanel("datamgmt"));
            sidebarContent.add(dataMgmtBtn);
            sidebarContent.add(Box.createVerticalStrut(4));

            exportBtn = createNavButton("导出排行榜", btnSize);
            exportBtn.addActionListener(e -> doExportRanking());
            sidebarContent.add(exportBtn);
        }

        sidebarContent.add(Box.createVerticalGlue());

        JButton logoutBtn = createNavButton("退出登录", btnSize);
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

        String roleText = "ADMIN".equals(currentUser.getRole()) ? "管理员" : "玩家";
        statusLabel = new JLabel("当前用户: " + currentUser.getNickname()
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

    private void doExportRanking() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("导出排行榜");
        fileChooser.setSelectedFile(new java.io.File("ranking.txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();
            fileStorageService.exportRankingToFile(dataManager, path);
            JOptionPane.showMessageDialog(this, "排行榜已成功导出到:\n" + path,
                    "导出成功", JOptionPane.INFORMATION_MESSAGE);
            opLogService.logSuccess(currentUser, "EXPORT", "RANKING", "", "导出排行榜到 " + path);
        }
    }
}
