package ui.web;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import model.Equipment;
import model.Hero;
import model.Team;
import model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.GameDataManager;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Embedded HTTP visualization server using JDK HttpServer + ECharts (served via CDN).
 * Serves the static dashboard.html for the full ECharts dashboard.
 * Provides JSON API endpoints for hero, equipment, and team statistics.
 * Fuzzy search uses Levenshtein distance for similarity scoring.
 */
public class VisualizationServer {
    private static final Logger log = LoggerFactory.getLogger(VisualizationServer.class);

    private final GameDataManager dataManager;
    private HttpServer server;
    private int port;
    private static final String STATIC_DIR = "src/ui/web/static";

    public VisualizationServer(GameDataManager dataManager) {
        this.dataManager = dataManager;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        port = server.getAddress().getPort();

        server.createContext("/", this::handleRoot);
        server.createContext("/api/heroes/position-stats", this::handleHeroPositionStats);
        server.createContext("/api/heroes/type-stats", this::handleHeroTypeStats);
        server.createContext("/api/heroes/ability-stats", this::handleHeroAbilityStats);
        server.createContext("/api/heroes/difficulty-stats", this::handleHeroDifficultyStats);
        server.createContext("/api/equipments/price-by-type", this::handleEquipmentPriceByType);
        server.createContext("/api/heroes/search", this::handleHeroSearch);
        server.createContext("/api/teams/win-rates", this::handleTeamWinRates);
        server.createContext("/api/data/summary", this::handleDataSummary);
        server.createContext("/static", this::handleStatic);

        server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(8));
        server.start();

        log.info("Visualization server started at http://localhost:{}", port);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            log.info("Visualization server stopped.");
        }
    }

    public int getPort() { return port; }

    private void handleStatic(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();
        String filePath = requestPath.substring("/static/".length());
        if (filePath.contains("..") || filePath.contains("/") || filePath.contains("\\")) {
            exchange.sendResponseHeaders(403, -1);
            return;
        }
        Path file = Paths.get(STATIC_DIR, filePath);
        if (Files.exists(file) && !Files.isDirectory(file)) {
            byte[] data = Files.readAllBytes(file);
            String mime = filePath.endsWith(".js") ? "application/javascript" : "application/octet-stream";
            exchange.getResponseHeaders().add("Content-Type", mime);
            exchange.getResponseHeaders().add("Cache-Control", "public, max-age=3600");
            exchange.sendResponseHeaders(200, data.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(data); }
        } else {
            exchange.sendResponseHeaders(404, -1);
        }
    }

    private void sendJson(HttpExchange exchange, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }

    private void handleRoot(HttpExchange exchange) throws IOException {
        // Serve the static dashboard.html for the ECharts dashboard
        Path htmlFile = Paths.get("src/ui/web/static/dashboard.html");
        if (Files.exists(htmlFile)) {
            byte[] data = Files.readAllBytes(htmlFile);
            exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, data.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(data); }
        } else {
            String fallback = "<html><body><h1>Dashboard not found</h1><p>Static file not found at src/ui/web/static/dashboard.html</p></body></html>";
            byte[] fb = fallback.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(404, fb.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(fb); }
        }
    }

    private static int levenshteinDistance(String a, String b) {
        if (a == null || b == null) return Integer.MAX_VALUE;
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i-1][j] + 1, dp[i][j-1] + 1), dp[i-1][j-1] + cost);
            }
        }
        return dp[a.length()][b.length()];
    }

    private static double fuzzyScore(String query, String target) {
        if (query == null || target == null) return 0.0;
        String q = query.toLowerCase().trim();
        String t = target.toLowerCase().trim();
        if (t.contains(q)) return 1.0;
        if (q.length() <= 1) return 0.0;
        int dist = levenshteinDistance(q, t.length() > 20 ? t.substring(0, 20) : t);
        double maxLen = Math.max(q.length(), Math.min(t.length(), 20));
        return Math.max(0, 1.0 - (double) dist / maxLen);
    }

    private void handleHeroPositionStats(HttpExchange exchange) throws IOException {
        List<Hero> heroes = dataManager.getHeroes();
        Map<String, Long> counts = heroes.stream()
                .filter(h -> h.getPosition() != null)
                .collect(Collectors.groupingBy(Hero::getPosition, Collectors.counting()));
        StringBuilder json = new StringBuilder("{\"categories\":[");
        json.append(counts.keySet().stream().map(k -> "\"" + escapeJson(k) + "\"").collect(Collectors.joining(",")));
        json.append("],\"values\":[");
        json.append(counts.values().stream().map(String::valueOf).collect(Collectors.joining(",")));
        json.append("]}");
        sendJson(exchange, json.toString());
    }

    private void handleHeroTypeStats(HttpExchange exchange) throws IOException {
        List<Hero> heroes = dataManager.getHeroes();
        Map<String, Long> counts = heroes.stream()
                .filter(h -> h.getHeroType() != null)
                .collect(Collectors.groupingBy(Hero::getHeroType, Collectors.counting()));
        StringBuilder json = new StringBuilder("{\"categories\":[");
        json.append(counts.keySet().stream().map(k -> "\"" + escapeJson(k) + "\"").collect(Collectors.joining(",")));
        json.append("],\"values\":[");
        json.append(counts.values().stream().map(String::valueOf).collect(Collectors.joining(",")));
        json.append("]}");
        sendJson(exchange, json.toString());
    }

    private void handleHeroAbilityStats(HttpExchange exchange) throws IOException {
        List<Hero> heroes = dataManager.getHeroes();
        Map<String, List<Hero>> byPosition = heroes.stream()
                .filter(h -> h.getPosition() != null && h.getSurvivalAbility() > 0)
                .collect(Collectors.groupingBy(Hero::getPosition));
        List<String> posKeys = new ArrayList<>(byPosition.keySet());
        StringBuilder json = new StringBuilder("{\"positions\":[");
        json.append(posKeys.stream().map(k -> "\"" + escapeJson(k) + "\"").collect(Collectors.joining(",")));
        json.append("],\"seriesData\":[");
        boolean first = true;
        for (String pos : posKeys) {
            if (!first) json.append(",");
            first = false;
            List<Hero> list = byPosition.get(pos);
            double surv = list.stream().mapToInt(Hero::getSurvivalAbility).average().orElse(0);
            double atk = list.stream().mapToInt(Hero::getAttackAbility).average().orElse(0);
            double skill = list.stream().mapToInt(Hero::getSkillAbility).average().orElse(0);
            double supp = list.stream().mapToInt(Hero::getSupportAbility).average().orElse(0);
            json.append("{\"name\":\"").append(escapeJson(pos)).append("\",\"value\":[");
            json.append(String.format("%.1f,%.1f,%.1f,%.1f", surv, atk, skill, supp));
            json.append("]}");
        }
        json.append("]}");
        sendJson(exchange, json.toString());
    }

    private void handleHeroDifficultyStats(HttpExchange exchange) throws IOException {
        List<Hero> heroes = dataManager.getHeroes();
        Map<String, Long> counts = heroes.stream()
                .filter(h -> h.getDifficulty() != null)
                .collect(Collectors.groupingBy(Hero::getDifficulty, Collectors.counting()));
        StringBuilder json = new StringBuilder("{\"categories\":[");
        json.append(counts.keySet().stream().map(k -> "\"" + escapeJson(k) + "\"").collect(Collectors.joining(",")));
        json.append("],\"values\":[");
        json.append(counts.values().stream().map(String::valueOf).collect(Collectors.joining(",")));
        json.append("]}");
        sendJson(exchange, json.toString());
    }

    private void handleEquipmentPriceByType(HttpExchange exchange) throws IOException {
        List<Equipment> equipments = dataManager.getEquipments();
        Map<String, List<Integer>> pricesByType = new LinkedHashMap<>();
        for (Equipment eq : equipments) {
            if (eq.getEquipmentType() == null) continue;
            pricesByType.computeIfAbsent(eq.getEquipmentType(), k -> new ArrayList<>()).add(eq.getPrice());
        }
        StringBuilder json = new StringBuilder("{\"categories\":[");
        json.append(pricesByType.keySet().stream().map(k -> "\"" + escapeJson(k) + "\"").collect(Collectors.joining(",")));
        json.append("],\"avgPrices\":[");
        json.append(pricesByType.values().stream().map(list -> String.format("%.0f", list.stream().mapToInt(Integer::intValue).average().orElse(0))).collect(Collectors.joining(",")));
        json.append("],\"maxPrices\":[");
        json.append(pricesByType.values().stream().map(list -> String.valueOf(list.stream().mapToInt(Integer::intValue).max().orElse(0))).collect(Collectors.joining(",")));
        json.append("],\"minPrices\":[");
        json.append(pricesByType.values().stream().map(list -> String.valueOf(list.stream().mapToInt(Integer::intValue).min().orElse(0))).collect(Collectors.joining(",")));
        json.append("],\"counts\":[");
        json.append(pricesByType.values().stream().map(list -> String.valueOf(list.size())).collect(Collectors.joining(",")));
        json.append("]}");
        sendJson(exchange, json.toString());
    }

    private void handleHeroSearch(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String q = "";
        if (query != null) {
            for (String param : query.split("&")) {
                String[] kv = param.split("=", 2);
                if (kv.length == 2 && "q".equals(kv[0])) {
                    q = java.net.URLDecoder.decode(kv[1], "UTF-8").toLowerCase();
                    break;
                }
            }
        }
        List<Hero> heroes = dataManager.getHeroes();
        String finalQ = q;

        List<Object[]> scored = new ArrayList<>();
        for (Hero h : heroes) {
            String searchStr = (
                (h.getHeroName() != null ? h.getHeroName() : "") + " " +
                (h.getTitle() != null ? h.getTitle() : "") + " " +
                (h.getPosition() != null ? h.getPosition() : "") + " " +
                (h.getHeroType() != null ? h.getHeroType() : "") + " " +
                (h.getDifficulty() != null ? h.getDifficulty() : "") + " " +
                (h.getPassiveSkill() != null ? h.getPassiveSkill() : "") + " " +
                (h.getSkillOne() != null ? h.getSkillOne() : "") + " " +
                (h.getSkillTwo() != null ? h.getSkillTwo() : "") + " " +
                (h.getSkillThree() != null ? h.getSkillThree() : "") + " " +
                (h.getDescription() != null ? h.getDescription() : "")
            ).toLowerCase();

            double score = fuzzyScore(finalQ, searchStr);
            if (h.getHeroName() != null) {
                double nameScore = fuzzyScore(finalQ, h.getHeroName());
                score = Math.max(score, nameScore * 1.2);
            }
            if (h.getTitle() != null) {
                double titleScore = fuzzyScore(finalQ, h.getTitle());
                score = Math.max(score, titleScore * 1.1);
            }
            if (score > 0.4) {
                scored.add(new Object[]{h, score});
            }
        }

        scored.sort((a, b) -> Double.compare((double) b[1], (double) a[1]));

        StringBuilder json = new StringBuilder("{\"heroes\":[");
        boolean firstHero = true;
        int limit = Math.min(scored.size(), 20);
        for (int i = 0; i < limit; i++) {
            if (!firstHero) json.append(",");
            firstHero = false;
            Hero h = (Hero) scored.get(i)[0];
            json.append("{");
            json.append("\"heroId\":\"").append(escapeJson(h.getHeroId())).append("\",");
            json.append("\"heroName\":\"").append(escapeJson(h.getHeroName())).append("\",");
            json.append("\"title\":\"").append(escapeJson(h.getTitle())).append("\",");
            json.append("\"position\":\"").append(escapeJson(h.getPosition())).append("\",");
            json.append("\"heroType\":\"").append(escapeJson(h.getHeroType())).append("\",");
            json.append("\"difficulty\":\"").append(escapeJson(h.getDifficulty())).append("\",");
            json.append("\"survivalAbility\":").append(h.getSurvivalAbility()).append(",");
            json.append("\"attackAbility\":").append(h.getAttackAbility()).append(",");
            json.append("\"skillAbility\":").append(h.getSkillAbility()).append(",");
            json.append("\"supportAbility\":").append(h.getSupportAbility()).append(",");
            json.append("\"score\":").append(String.format("%.2f", (double) scored.get(i)[1]));
            json.append("}");
        }
        json.append("]}");
        sendJson(exchange, json.toString());
    }

    private void handleTeamWinRates(HttpExchange exchange) throws IOException {
        List<Team> teams = dataManager.getTeams().stream()
                .sorted(Comparator.comparingDouble(Team::getWinRate).reversed())
                .collect(Collectors.toList());
        StringBuilder json = new StringBuilder("{\"names\":[");
        json.append(teams.stream().map(t -> "\"" + escapeJson(t.getTeamName()) + "\"").collect(Collectors.joining(",")));
        json.append("],\"winRates\":[");
        json.append(teams.stream().map(t -> String.format("%.1f", t.getWinRate() * 100)).collect(Collectors.joining(",")));
        json.append("],\"totalMatches\":[");
        json.append(teams.stream().map(t -> String.valueOf(t.getTotalMatches())).collect(Collectors.joining(",")));
        json.append("]}");
        sendJson(exchange, json.toString());
    }

    private void handleDataSummary(HttpExchange exchange) throws IOException {
        List<Hero> heroes = dataManager.getHeroes();
        List<Equipment> equipments = dataManager.getEquipments();
        List<Team> teams = dataManager.getTeams();

        long positionCount = heroes.stream()
            .filter(h -> h.getPosition() != null)
            .map(Hero::getPosition)
            .distinct()
            .count();

        StringBuilder json = new StringBuilder("{");
        json.append("\"heroCount\":").append(heroes.size()).append(",");
        json.append("\"positionCount\":").append(positionCount).append(",");
        json.append("\"equipCount\":").append(equipments.size()).append(",");
        json.append("\"teamCount\":").append(teams.size());
        json.append("}");
        sendJson(exchange, json.toString());
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}