import ui.web.VisualizationServer;
import service.GameDataManager;
import db.DatabaseManager;

public class TestViz {
    public static void main(String[] args) throws Exception {
        DatabaseManager db = DatabaseManager.getInstance();
        db.initialize();
        
        GameDataManager dm = new GameDataManager();
        dm.persistAllToDatabase();
        
        System.out.println("Heroes: " + dm.getHeroes().size());
        System.out.println("Equips: " + dm.getEquipments().size());
        System.out.println("Teams: " + dm.getTeams().size());
        System.out.println("Players: " + dm.getPlayers().size());
        
        VisualizationServer server = new VisualizationServer(dm);
        server.start();
        System.out.println("Server running on port " + server.getPort());
        
        Thread.currentThread().join();
    }
}