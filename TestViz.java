import db.DatabaseManager;
import service.GameDataManager;
import ui.web.VisualizationServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestViz {
    public static void main(String[] args) throws Exception {
        DatabaseManager.getInstance().initialize();
        GameDataManager gm = new GameDataManager();
        gm.persistAllToDatabase();

        System.out.println("Heroes loaded: " + gm.getHeroes().size());
        System.out.println("Equipments loaded: " + gm.getEquipments().size());
        System.out.println("Teams loaded: " + gm.getTeams().size());

        VisualizationServer vs = new VisualizationServer(gm);
        vs.start();
        int port = vs.getPort();

        String[] endpoints = new String[]{
            "/api/heroes/position-stats",
            "/api/heroes/type-stats",
            "/api/heroes/ability-stats",
            "/api/heroes/difficulty-stats",
            "/api/equipments/price-by-type",
            "/api/heroes/search?q=刺",
            "/api/teams/win-rates"
        };

        for (String ep : endpoints) {
            URL url = new URL("http://localhost:" + port + ep);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            int code = conn.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) sb.append(line);
            String body = sb.toString().substring(0, Math.min(200, sb.toString().length()));
            System.out.println("\n=== " + ep + " [" + code + "] ===");
            System.out.println(body);
            conn.disconnect();
        }

        vs.stop();
        DatabaseManager.getInstance().close();
    }
}
