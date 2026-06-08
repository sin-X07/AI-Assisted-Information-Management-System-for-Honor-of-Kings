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

import java.awt.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 嵌入式 HTTP 服务器，提供基于 ECharts 的数据可视化仪表盘。
 * 启动后自动打开浏览器，包含英雄分布、装备价格、战队胜率等图表。
 * 支持 Levenshtein 模糊搜索，ECharts 多 CDN 回退加载。
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
        server.createContext("/static", this::handleStatic);

        server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(2));
        server.start();

        log.info("可视化仪表盘已启动: http://localhost:{}", port);

        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI("http://localhost:" + port));
            }
        } catch (Exception e) {
            log.warn("无法自动打开浏览器: {}", e.getMessage());
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            log.info("可视化服务器已停止。");
        }
    }

    public int getPort() { return port; }

    // ===== 静态文件服务 =====

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

    // ===== HTTP 辅助方法 =====

    private void sendJson(HttpExchange exchange, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }

    private void sendHtml(HttpExchange exchange, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }

    private void handleRoot(HttpExchange exchange) throws IOException {
        sendHtml(exchange, DASHBOARD_HTML);
    }

    // ===== Levenshtein 模糊搜索 =====

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

    // ===== API 处理器 =====

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
        json.append("],\"indicators\":[\"生存\",\"攻击\",\"技能\",\"辅助\"]");
        json.append("],\"survival\":[");
        json.append(posKeys.stream().map(pos -> String.format("%.1f", byPosition.get(pos).stream().mapToInt(Hero::getSurvivalAbility).average().orElse(0))).collect(Collectors.joining(",")));
        json.append("],\"attack\":[");
        json.append(posKeys.stream().map(pos -> String.format("%.1f", byPosition.get(pos).stream().mapToInt(Hero::getAttackAbility).average().orElse(0))).collect(Collectors.joining(",")));
        json.append("],\"skill\":[");
        json.append(posKeys.stream().map(pos -> String.format("%.1f", byPosition.get(pos).stream().mapToInt(Hero::getSkillAbility).average().orElse(0))).collect(Collectors.joining(",")));
        json.append("],\"support\":[");
        json.append(posKeys.stream().map(pos -> String.format("%.1f", byPosition.get(pos).stream().mapToInt(Hero::getSupportAbility).average().orElse(0))).collect(Collectors.joining(",")));
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

        // 构建每个英雄的搜索文本
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
            // 对英雄名字赋予更高权重
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

        // 按分数降序排列
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

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // ===== 嵌入式 ECharts 仪表盘 HTML（含多 CDN 回退、雷达图、模糊搜索、错误处理） =====

    private static final String DASHBOARD_HTML = ""
            + ko("<!DOCTYPE html>")
            + ko("<html lang=\"zh-CN\">")
            + ko("<head>")
            + ko("<meta charset=\"UTF-8\">")
            + ko("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
            + ko("<title>王者荣耀 · 数据可视化仪表盘</title>")
            + ko("<style>")
            + ko("  * { margin: 0; padding: 0; box-sizing: border-box; }")
            + ko("  body { font-family: 'Microsoft YaHei', -apple-system, sans-serif; background: #0f1923; color: #e0e6ed; }")
            + ko("  .header { background: linear-gradient(135deg, #1a2a3a, #0d1b2a); padding: 20px 32px; border-bottom: 2px solid #c8a84e; display: flex; align-items: center; justify-content: space-between; flex-wrap: wrap; gap: 12px; }")
            + ko("  .header-left { display: flex; flex-direction: column; }")
            + ko("  .header h1 { font-size: 24px; color: #c8a84e; letter-spacing: 2px; }")
            + ko("  .header p { font-size: 13px; color: #8899aa; margin-top: 4px; }")
            + ko("  .search-box { display: flex; align-items: center; background: #0f1923; border: 1px solid #2a3a4a; border-radius: 6px; padding: 4px 12px; min-width: 240px; }")
            + ko("  .search-box:focus-within { border-color: #c8a84e; }")
            + ko("  .search-box input { background: none; border: none; color: #e0e6ed; font-size: 14px; padding: 8px 4px; outline: none; width: 100%; }")
            + ko("  .search-box input::placeholder { color: #556677; }")
            + ko("  .search-results { position: absolute; top: 100%; right: 0; margin-top: 4px; background: #1a2a3a; border: 1px solid #2a3a4a; border-radius: 8px; width: 380px; max-height: 420px; overflow-y: auto; display: none; box-shadow: 0 8px 32px rgba(0,0,0,0.5); z-index: 100; }")
            + ko("  .search-results.open { display: block; }")
            + ko("  .search-result-item { padding: 10px 14px; border-bottom: 1px solid #2a3a4a; cursor: pointer; transition: background 0.15s; }")
            + ko("  .search-result-item:last-child { border-bottom: none; }")
            + ko("  .search-result-item:hover { background: #2a3a4a; }")
            + ko("  .search-result-item .hero-name { color: #c8a84e; font-weight: 600; font-size: 14px; }")
            + ko("  .search-result-item .hero-title { color: #8899aa; font-size: 12px; margin-left: 6px; }")
            + ko("  .search-result-item .hero-meta { color: #6b7c8d; font-size: 12px; margin-top: 2px; }")
            + ko("  .search-result-item .hero-abilities { display: flex; gap: 8px; margin-top: 4px; }")
            + ko("  .search-result-item .hero-abilities span { font-size: 11px; padding: 1px 6px; border-radius: 3px; background: #0f1923; color: #8899aa; }")
            + ko("  .search-wrapper { position: relative; }")
            + ko("  .container { max-width: 1400px; margin: 0 auto; padding: 20px; }")
            + ko("  .grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }")
            + ko("  .card { background: #1a2a3a; border-radius: 8px; padding: 16px; border: 1px solid #2a3a4a; }")
            + ko("  .card h2 { font-size: 15px; color: #c8a84e; margin-bottom: 12px; padding-bottom: 8px; border-bottom: 1px solid #2a3a4a; }")
            + ko("  .card-full { grid-column: 1 / -1; }")
            + ko("  .chart { width: 100%; height: 340px; position: relative; }")
            + ko("  .chart-loading { display: flex; align-items: center; justify-content: center; height: 340px; color: #6b7c8d; font-size: 14px; background: #152435; border-radius: 4px; }")
            + ko("  .chart-error { display: flex; align-items: center; justify-content: center; height: 340px; color: #e8594e; font-size: 14px; text-align: center; padding: 20px; background: #2a1a1a; border-radius: 4px; }")
            + ko("  .stats-bar { display: flex; gap: 16px; flex-wrap: wrap; margin-bottom: 16px; }")
            + ko("  .stat-item { flex: 1; min-width: 120px; background: #0f1923; border-radius: 6px; padding: 12px 16px; text-align: center; }")
            + ko("  .stat-item .value { font-size: 28px; font-weight: 700; color: #c8a84e; }")
            + ko("  .stat-item .label { font-size: 12px; color: #6b7c8d; margin-top: 2px; }")
            + ko("  #echartsLoadError { display: none; background: #2a1a1a; border: 1px solid #e8594e; color: #e8594e; padding: 12px 20px; margin: 12px auto; max-width: 600px; border-radius: 6px; text-align: center; font-size: 14px; }")
            + ko("  @media (max-width: 900px) { .grid { grid-template-columns: 1fr; } .search-box { min-width: 100%; } .search-results { width: 100%; } }")
            + ko("</style>")
            + ko("</head>")
            + ko("<body>")
            + ko("<div id=\"echartsLoadError\">⚠ ECharts 加载失败，请检查网络连接或刷新页面重试</div>")
            + ko("<div class=\"header\">")
            + ko("  <div class=\"header-left\">")
            + ko("    <h1>王者荣耀 · 数据可视化仪表盘</h1>")
            + ko("    <p>英雄分布 · 装备价格 · 战队胜率 · 能力均衡 · 模糊搜索</p>")
            + ko("  </div>")
            + ko("  <div class=\"search-wrapper\">")
            + ko("    <div class=\"search-box\">")
            + ko("      <svg width=\"16\" height=\"16\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"#556677\" stroke-width=\"2\"><circle cx=\"11\" cy=\"11\" r=\"8\"/><path d=\"m21 21-4.35-4.35\"/></svg>")
            + ko("      <input type=\"text\" id=\"searchInput\" placeholder=\"搜索英雄名称、定位、技能…\" oninput=\"onSearch(this.value)\">")
            + ko("    </div>")
            + ko("    <div class=\"search-results\" id=\"searchResults\"></div>")
            + ko("  </div>")
            + ko("</div>")
            + ko("<div class=\"container\">")
            + ko("  <div class=\"stats-bar\" id=\"statsBar\">")
            + ko("    <div class=\"stat-item\"><div class=\"value\" id=\"statHeroes\">-</div><div class=\"label\">英雄总数</div></div>")
            + ko("    <div class=\"stat-item\"><div class=\"value\" id=\"statPositions\">-</div><div class=\"label\">定位种类</div></div>")
            + ko("    <div class=\"stat-item\"><div class=\"value\" id=\"statEquipments\">-</div><div class=\"label\">装备总数</div></div>")
            + ko("    <div class=\"stat-item\"><div class=\"value\" id=\"statTeams\">-</div><div class=\"label\">战队总数</div></div>")
            + ko("  </div>")
            + ko("  <div class=\"grid\">")
            + ko("    <div class=\"card\"><h2>英雄定位分布</h2><div id=\"chart-position\" class=\"chart\"><div class=\"chart-loading\">图表加载中…</div></div></div>")
            + ko("    <div class=\"card\"><h2>英雄类型分布</h2><div id=\"chart-type\" class=\"chart\"><div class=\"chart-loading\">图表加载中…</div></div></div>")
            + ko("    <div class=\"card card-full\"><h2>各位置能力均衡图（雷达）</h2><div id=\"chart-ability\" class=\"chart\" style=\"height:420px;\"><div class=\"chart-loading\" style=\"height:420px;\">雷达图加载中…</div></div></div>")
            + ko("    <div class=\"card\"><h2>装备价格统计（按类型）</h2><div id=\"chart-equip-price\" class=\"chart\"><div class=\"chart-loading\">图表加载中…</div></div></div>")
            + ko("    <div class=\"card\"><h2>英雄难度分布</h2><div id=\"chart-difficulty\" class=\"chart\"><div class=\"chart-loading\">图表加载中…</div></div></div>")
            + ko("    <div class=\"card card-full\"><h2>战队胜率排名</h2><div id=\"chart-team-winrate\" class=\"chart\" style=\"height:420px;\"><div class=\"chart-loading\" style=\"height:420px;\">图表加载中…</div></div></div>")
            + ko("  </div>")
            + ko("</div>")
            + ko("<script>")
            + ko("var echarts;")
            + ko("var COLORS = ['#c8a84e','#e8594e','#4e8be8','#5abf5a','#bf7adb','#e8844e','#4ec4c4','#d94e8b','#8b7355','#6b7c8d'];")
            + ko("var radarColors = ['#c8a84e','#e8594e','#4e8be8','#5abf5a','#bf7adb','#e8844e','#4ec4c4','#d94e8b','#8b7355'];")
            + ko("")
            + ko("function showError(id, msg) {")
            + ko("  var el = document.getElementById(id);")
            + ko("  if (!el) return;")
            + ko("  var loading = el.querySelector('.chart-loading');")
            + ko("  if (loading) loading.remove();")
            + ko("  var errDiv = el.querySelector('.chart-error');")
            + ko("  if (!errDiv) {")
            + ko("    errDiv = document.createElement('div');")
            + ko("    errDiv.className = 'chart-error';")
            + ko("    el.appendChild(errDiv);")
            + ko("  }")
            + ko("  errDiv.textContent = '⚠ ' + msg;")
            + ko("}")
            + ko("")
            + ko("function clearLoading(id) {")
            + ko("  var el = document.getElementById(id);")
            + ko("  if (!el) return;")
            + ko("  var loading = el.querySelector('.chart-loading');")
            + ko("  if (loading) loading.remove();")
            + ko("  var oldErr = el.querySelector('.chart-error');")
            + ko("  if (oldErr) oldErr.remove();")
            + ko("}")
            + ko("")
            + ko("function fetchJSON(url) { return fetch(url).then(function(r) { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); }); }")
            + ko("")
            + ko("function initChart(id) {")
            + ko("  var el = document.getElementById(id);")
            + ko("  if (!el) return null;")
            + ko("  clearLoading(id);")
            + ko("  try {")
            + ko("    var chart = echarts.init(el, 'dark');")
            + ko("    window.addEventListener('resize', function() { chart.resize(); });")
            + ko("    return chart;")
            + ko("  } catch(e) {")
            + ko("    showError(id, '图表初始化失败: ' + e.message);")
            + ko("    return null;")
            + ko("  }")
            + ko("}")
            + ko("")
            + ko("/* ===== 动态 ECharts 加载（多 CDN 回退链） ===== */")
            + ko("var echartsUrls = [")
            + ko("  '/static/echarts.min.js',")
            + ko("  'https://cdn.bootcdn.net/ajax/libs/echarts/5.5.0/echarts.min.js',")
            + ko("  'https://cdnjs.cloudflare.com/ajax/libs/echarts/5.5.0/echarts.min.js',")
            + ko("  'https://unpkg.com/echarts@5/dist/echarts.min.js',")
            + ko("  'https://cdn.jsdelivr.net/npm/echarts@5/dist/echarts.min.js'")
            + ko("];")
            + ko("var echartsLoaded = false;")
            + ko("var chartCallbacks = [];")
            + ko("")
            + ko("function tryLoadECharts(idx) {")
            + ko("  if (idx >= echartsUrls.length) {")
            + ko("    document.getElementById('echartsLoadError').style.display = 'block';")
            + ko("    return;")
            + ko("  }")
            + ko("  var s = document.createElement('script');")
            + ko("  s.src = echartsUrls[idx];")
            + ko("  s.onload = function() {")
            + ko("    echartsLoaded = true;")
            + ko("    initDashboard();")
            + ko("  };")
            + ko("  s.onerror = function() {")
            + ko("    console.warn('ECharts CDN ⨯ ' + echartsUrls[idx]);")
            + ko("    tryLoadECharts(idx + 1);")
            + ko("  };")
            + ko("  document.head.appendChild(s);")
            + ko("}")
            + ko("tryLoadECharts(0);")
            + ko("")
            + ko("/* ===== 工具函数 ===== */")
            + ko("function levenshtein(a, b) {")
            + ko("  var alen = a.length, blen = b.length;")
            + ko("  if (alen === 0) return blen;")
            + ko("  if (blen === 0) return alen;")
            + ko("  var dp = [];")
            + ko("  for (var i = 0; i <= alen; i++) { dp[i] = []; dp[i][0] = i; }")
            + ko("  for (var j = 0; j <= blen; j++) dp[0][j] = j;")
            + ko("  for (var i = 1; i <= alen; i++) {")
            + ko("    for (var j = 1; j <= blen; j++) {")
            + ko("      var cost = a[i-1] === b[j-1] ? 0 : 1;")
            + ko("      dp[i][j] = Math.min(dp[i-1][j]+1, dp[i][j-1]+1, dp[i-1][j-1]+cost);")
            + ko("    }")
            + ko("  }")
            + ko("  return dp[alen][blen];")
            + ko("}")
            + ko("")
            + ko("function fuzzyScore(query, target) {")
            + ko("  if (!query || !target) return 0;")
            + ko("  var q = query.toLowerCase().trim();")
            + ko("  var t = target.toLowerCase().trim();")
            + ko("  if (t.indexOf(q) !== -1) return 1;")
            + ko("  if (q.length <= 1) return 0;")
            + ko("  var tSub = t.length > 20 ? t.substring(0, 20) : t;")
            + ko("  var dist = levenshtein(q, tSub);")
            + ko("  var maxLen = Math.max(q.length, Math.min(t.length, 20));")
            + ko("  return Math.max(0, 1 - dist / maxLen);")
            + ko("}")
            + ko("")
            + ko("/* ===== 模糊搜索 ===== */")
            + ko("var searchTimer;")
            + ko("function onSearch(val) {")
            + ko("  clearTimeout(searchTimer);")
            + ko("  var el = document.getElementById('searchResults');")
            + ko("  if (!val.trim()) { el.classList.remove('open'); return; }")
            + ko("  searchTimer = setTimeout(function() {")
            + ko("    fetch('/api/heroes/search?q=' + encodeURIComponent(val.trim()))")
            + ko("      .then(function(r) { return r.json(); })")
            + ko("      .then(function(data) {")
            + ko("        if (data.heroes.length === 0) {")
            + ko("          el.innerHTML = '<div class=\"search-result-item\" style=\"color:#6b7c8d;\">未找到匹配的英雄</div>';")
            + ko("        } else {")
            + ko("          el.innerHTML = data.heroes.map(function(h) {")
            + ko("            var scoreStars = '';")
            + ko("            if (h.score > 0.8) scoreStars = ' ⭐⭐⭐';")
            + ko("            else if (h.score > 0.6) scoreStars = ' ⭐⭐';")
            + ko("            else if (h.score > 0.4) scoreStars = ' ⭐';")
            + ko("            return '<div class=\"search-result-item\" onclick=\"selectHero(\\'' + h.heroId + '\\')\">' +")
            + ko("              '<div><span class=\"hero-name\">' + h.heroName + '</span><span class=\"hero-title\">' + h.title + '</span>' + scoreStars + '</div>' +")
            + ko("              '<div class=\"hero-meta\">' + h.position + ' · ' + h.heroType + ' · ' + h.difficulty + '</div>' +")
            + ko("              '<div class=\"hero-abilities\">' +")
            + ko("                '<span>生存' + h.survivalAbility + '</span>' +")
            + ko("                '<span>攻击' + h.attackAbility + '</span>' +")
            + ko("                '<span>技能' + h.skillAbility + '</span>' +")
            + ko("                '<span>辅助' + h.supportAbility + '</span>' +")
            + ko("              '</div></div>';")
            + ko("          }).join('');")
            + ko("        }")
            + ko("        el.classList.add('open');")
            + ko("      })")
            + ko("      .catch(function(e) {")
            + ko("        el.innerHTML = '<div class=\"search-result-item\" style=\"color:#e8594e;\">搜索请求失败: ' + e.message + '</div>';")
            + ko("        el.classList.add('open');")
            + ko("      });")
            + ko("  }, 200);")
            + ko("}")
            + ko("")
            + ko("function selectHero(heroId) {")
            + ko("  document.getElementById('searchInput').value = '';")
            + ko("  document.getElementById('searchResults').classList.remove('open');")
            + ko("}")
            + ko("")
            + ko("document.addEventListener('click', function(e) {")
            + ko("  if (!e.target.closest('.search-wrapper')) {")
            + ko("    document.getElementById('searchResults').classList.remove('open');")
            + ko("  }")
            + ko("});")
            + ko("")
            + ko("/* ===== 仪表盘初始化 ===== */")
            + ko("function initDashboard() {")
            + ko("  // 统计栏")
            + ko("  Promise.all([")
            + ko("    fetchJSON('/api/heroes/position-stats'),")
            + ko("    fetchJSON('/api/equipments/price-by-type'),")
            + ko("    fetchJSON('/api/teams/win-rates')")
            + ko("  ]).then(function(results) {")
            + ko("    var pos = results[0], equip = results[1], team = results[2];")
            + ko("    document.getElementById('statHeroes').textContent = pos.values.reduce(function(a,b){return a+b},0);")
            + ko("    document.getElementById('statPositions').textContent = pos.categories.length;")
            + ko("    document.getElementById('statEquipments').textContent = equip.counts.reduce(function(a,b){return a+b},0);")
            + ko("    document.getElementById('statTeams').textContent = team.names.length;")
            + ko("  }).catch(function(e) {")
            + ko("    document.getElementById('statHeroes').textContent = '?';")
            + ko("    document.getElementById('statPositions').textContent = '?';")
            + ko("    document.getElementById('statEquipments').textContent = '?';")
            + ko("    document.getElementById('statTeams').textContent = '?';")
            + ko("  });")
            + ko("")
            + ko("  // 1. 英雄定位分布（饼图）")
            + ko("  fetchJSON('/api/heroes/position-stats').then(function(data) {")
            + ko("    var chart = initChart('chart-position');")
            + ko("    if (!chart) return;")
            + ko("    chart.setOption({")
            + ko("      tooltip: { trigger: 'item', formatter: '{b}: {c} 个 ({d}%)' },")
            + ko("      legend: { orient: 'vertical', left: 'left', textStyle: { color: '#e0e6ed' } },")
            + ko("      series: [{ type: 'pie', radius: ['35%', '65%'], center: ['60%', '50%'],")
            + ko("        label: { show: false },")
            + ko("        data: data.categories.map(function(c, i) { return { name: c, value: data.values[i], itemStyle: { color: COLORS[i % COLORS.length] } }; }),")
            + ko("        itemStyle: { borderColor: '#1a2a3a', borderWidth: 2 }")
            + ko("      }]")
            + ko("    });")
            + ko("    window.addEventListener('resize', function() { chart.resize(); });")
            + ko("  }).catch(function(e) { showError('chart-position', '定位分布加载失败: ' + e.message); });")
            + ko("")
            + ko("  // 2. 英雄类型分布（饼图）")
            + ko("  fetchJSON('/api/heroes/type-stats').then(function(data) {")
            + ko("    var chart = initChart('chart-type');")
            + ko("    if (!chart) return;")
            + ko("    chart.setOption({")
            + ko("      tooltip: { trigger: 'item', formatter: '{b}: {c} 个 ({d}%)' },")
            + ko("      legend: { orient: 'vertical', left: 'left', textStyle: { color: '#e0e6ed' } },")
            + ko("      series: [{ type: 'pie', radius: ['35%', '65%'], center: ['60%', '50%'],")
            + ko("        label: { show: false },")
            + ko("        data: data.categories.map(function(c, i) { return { name: c, value: data.values[i], itemStyle: { color: COLORS[i % COLORS.length] } }; }),")
            + ko("        itemStyle: { borderColor: '#1a2a3a', borderWidth: 2 }")
            + ko("      }]")
            + ko("    });")
            + ko("  }).catch(function(e) { showError('chart-type', '类型分布加载失败: ' + e.message); });")
            + ko("")
            + ko("  // 3. 各位置能力均衡图（雷达图）")
            + ko("  fetchJSON('/api/heroes/ability-stats').then(function(data) {")
            + ko("    var chart = initChart('chart-ability');")
            + ko("    if (!chart) return;")
            + ko("    var radarIndicators = [")
            + ko("      { name: '生存', max: 10 },")
            + ko("      { name: '攻击', max: 10 },")
            + ko("      { name: '技能', max: 10 },")
            + ko("      { name: '辅助', max: 10 }")
            + ko("    ];")
            + ko("    chart.setOption({")
            + ko("      tooltip: { trigger: 'item' },")
            + ko("      legend: { data: data.positions, textStyle: { color: '#e0e6ed' }, selectedMode: 'multiple', bottom: 0 },")
            + ko("      radar: { indicator: radarIndicators, center: ['50%', '45%'], radius: '65%',")
            + ko("        axisName: { color: '#e0e6ed' },")
            + ko("        splitArea: { areaStyle: { color: ['rgba(200,168,78,0.02)', 'rgba(200,168,78,0.04)'] } },")
            + ko("        splitLine: { lineStyle: { color: '#2a3a4a' } },")
            + ko("        axisLine: { lineStyle: { color: '#2a3a4a' } }")
            + ko("      },")
            + ko("      series: [{")
            + ko("        type: 'radar',")
            + ko("        data: data.seriesData.map(function(d, i) {")
            + ko("          return {")
            + ko("            value: d.value,")
            + ko("            name: d.name,")
            + ko("            areaStyle: { color: radarColors[i % radarColors.length], opacity: 0.15 },")
            + ko("            lineStyle: { color: radarColors[i % radarColors.length], width: 2 },")
            + ko("            itemStyle: { color: radarColors[i % radarColors.length] }")
            + ko("          };")
            + ko("        })")
            + ko("      }]")
            + ko("    });")
            + ko("  }).catch(function(e) { showError('chart-ability', '能力均衡图加载失败: ' + e.message); });")
            + ko("")
            + ko("  // 4. 装备价格统计")
            + ko("  fetchJSON('/api/equipments/price-by-type').then(function(data) {")
            + ko("    var chart = initChart('chart-equip-price');")
            + ko("    if (!chart) return;")
            + ko("    chart.setOption({")
            + ko("      tooltip: { trigger: 'axis' },")
            + ko("      legend: { textStyle: { color: '#e0e6ed' }, selectedMode: 'multiple' },")
            + ko("      xAxis: { type: 'category', data: data.categories, axisLabel: { color: '#8899aa', rotate: 15 } },")
            + ko("      yAxis: { type: 'value', name: '金币', axisLabel: { color: '#8899aa' }, splitLine: { lineStyle: { color: '#2a3a4a', type: 'dashed' } } },")
            + ko("      dataZoom: [{ type: 'slider', start: 0, end: 100, bottom: 0 }],")
            + ko("      series: [")
            + ko("        { name: '平均价格', type: 'bar', barWidth: '20%', data: data.avgPrices.map(function(v){return Math.round(Number(v))}), itemStyle: { color: '#c8a84e' } },")
            + ko("        { name: '最高价格', type: 'bar', barWidth: '16%', barGap: '10%', data: data.maxPrices, itemStyle: { color: '#e8594e' } },")
            + ko("        { name: '最低价格', type: 'bar', barWidth: '16%', data: data.minPrices, itemStyle: { color: '#5abf5a' } }")
            + ko("      ]")
            + ko("    });")
            + ko("  }).catch(function(e) { showError('chart-equip-price', '装备价格加载失败: ' + e.message); });")
            + ko("")
            + ko("  // 5. 英雄难度分布")
            + ko("  fetchJSON('/api/heroes/difficulty-stats').then(function(data) {")
            + ko("    var chart = initChart('chart-difficulty');")
            + ko("    if (!chart) return;")
            + ko("    var diffColors = ['#5abf5a','#c8a84e','#e8594e'];")
            + ko("    chart.setOption({")
            + ko("      tooltip: { trigger: 'axis' },")
            + ko("      xAxis: { type: 'category', data: data.categories, axisLabel: { color: '#8899aa' } },")
            + ko("      yAxis: { type: 'value', name: '英雄数量', axisLabel: { color: '#8899aa' }, splitLine: { lineStyle: { color: '#2a3a4a', type: 'dashed' } } },")
            + ko("      series: [{ type: 'bar', data: data.values.map(function(v,i) {")
            + ko("        return { value: v, itemStyle: { color: diffColors[i % diffColors.length] } };")
            + ko("      }), barWidth: '40%',")
            + ko("        label: { show: true, position: 'top', color: '#e0e6ed', fontSize: 13 }")
            + ko("      }]")
            + ko("    });")
            + ko("  }).catch(function(e) { showError('chart-difficulty', '难度分布加载失败: ' + e.message); });")
            + ko("")
            + ko("  // 6. 战队胜率排名（水平柱状图）")
            + ko("  fetchJSON('/api/teams/win-rates').then(function(data) {")
            + ko("    var chart = initChart('chart-team-winrate');")
            + ko("    if (!chart) return;")
            + ko("    var names = data.names.slice().reverse();")
            + ko("    var rates = data.winRates.slice().reverse();")
            + ko("    var matches = data.totalMatches.slice().reverse();")
            + ko("    chart.setOption({")
            + ko("      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' }, formatter: function(params) {")
            + ko("        var i = params[0].dataIndex;")
            + ko("        return names[i] + '<br/>胜率: ' + rates[i] + '%<br/>总场次: ' + matches[i];")
            + ko("      } },")
            + ko("      grid: { left: '3%', right: '6%', bottom: '3%', containLabel: true },")
            + ko("      xAxis: { type: 'value', name: '胜率(%)', axisLabel: { color: '#8899aa' }, max: 100, splitLine: { lineStyle: { color: '#2a3a4a', type: 'dashed' } } },")
            + ko("      yAxis: { type: 'category', data: names, axisLabel: { color: '#e0e6ed', fontSize: 12 }, axisLine: { lineStyle: { color: '#2a3a4a' } } },")
            + ko("      dataZoom: [{ type: 'slider', orient: 'vertical', start: 0, end: 100, right: 0 }],")
            + ko("      series: [{")
            + ko("        type: 'bar', data: rates,")
            + ko("        barWidth: '55%',")
            + ko("        label: { show: true, position: 'right', formatter: function(p) { return p.value + '%'; }, color: '#e0e6ed', fontSize: 11 },")
            + ko("        itemStyle: { color: function(p) {")
            + ko("          var v = parseFloat(p.value);")
            + ko("          return v >= 65 ? '#5abf5a' : v >= 50 ? '#c8a84e' : '#e8594e';")
            + ko("        } }")
            + ko("      }]")
            + ko("    });")
            + ko("  }).catch(function(e) { showError('chart-team-winrate', '战队胜率加载失败: ' + e.message); });")
            + ko("}")
            + ko("</script>")
            + ko("</body>")
            + ko("</html>");
    private static String ko(String s) { return s + "\n"; }
}
