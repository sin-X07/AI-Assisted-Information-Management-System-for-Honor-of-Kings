package ui;

import model.Person;
import service.AuthenticationService;
import service.FileStorageService;
import service.GameDataManager;
import service.RankingService;

import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {
        try {
            Class.forName("com.formdev.flatlaf.FlatLightLaf")
                    .getMethod("setup").invoke(null);
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
            }
        }

        SwingUtilities.invokeLater(() -> {
            AuthenticationService authService = new AuthenticationService();
            GameDataManager dataManager = new GameDataManager();
            RankingService rankingService = new RankingService();
            FileStorageService fileStorageService = new FileStorageService();

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
    }
}
