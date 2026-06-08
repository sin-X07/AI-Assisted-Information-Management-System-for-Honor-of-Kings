package ui;

import db.DatabaseManager;
import model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.AuthenticationService;
import service.FileStorageService;
import service.GameDataManager;
import service.RankingService;

import javax.swing.*;

public class AppLauncher {
    private static final Logger log = LoggerFactory.getLogger(AppLauncher.class);

    public static void main(String[] args) {
        // Initialize database (SQLite)
        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.initialize();
        log.info("Database initialized.");

        try {
            Class.forName("com.formdev.flatlaf.FlatLightLaf")
                    .getMethod("setup").invoke(null);
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                // fall through to default
            }
        }

        SwingUtilities.invokeLater(() -> {
            AuthenticationService authService = new AuthenticationService();
            GameDataManager dataManager = new GameDataManager();
            RankingService rankingService = new RankingService();
            FileStorageService fileStorageService = new FileStorageService();

            // Seed initial game data if database was empty
            dataManager.persistAllToDatabase();

            LoginDialog loginDialog = new LoginDialog(null, authService);
            loginDialog.setVisible(true);

            Person user = loginDialog.getLoggedInUser();
            if (user != null) {
                MainFrame mainFrame = new MainFrame(user, dataManager, rankingService, fileStorageService);
                mainFrame.setVisible(true);
            } else {
                System.exit(0);
            }
        });

        // Shutdown hook: close database connection
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseManager.getInstance().close();
            log.info("Application shutting down.");
        }));
    }
}
