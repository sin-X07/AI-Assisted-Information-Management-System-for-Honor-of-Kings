package ui.web;

import db.DatabaseManager;
import service.GameDataManager;
import ui.web.VisualizationServer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class TestViz {
    public static void main(String[] args) throws Exception {
        DatabaseManager.getInstance().initialize();
        GameDataManager dataManager = new GameDataManager();
        dataManager.persistAllToDatabase();

        VisualizationServer server = new VisualizationServer(dataManager);
        server.start();
        int port = server.getPort();
        System.out.println("PORT:" + port);

        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

        String[] urls = {
            "http://localhost:" + port + "/api/data/summary",
            "http://localhost:" + port + "/api/heroes/position-stats",
            "http://localhost:" + port + "/api/heroes/type-stats",
            "http://localhost:" + port + "/api/heroes/ability-stats",
            "http://localhost:" + port + "/api/heroes/difficulty-stats",
            "http://localhost:" + port + "/api/equipments/price-by-type",
            "http://localhost:" + port + "/api/teams/win-rates",
            "http://localhost:" + port + "/"
        };

        for (String url : urls) {
            try {
                HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .GET().build();
                HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
                String body = resp.body().length() > 120 ? resp.body().substring(0, 120) + "..." : resp.body();
                System.out.println("OK [" + resp.statusCode() + "] " + url + " -> " + body);
            } catch (Exception e) {
                System.out.println("FAIL " + url + " -> " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }

        server.stop();
        System.out.println("DONE");
    }
}