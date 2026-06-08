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
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 嵌入式 HTTP 服务器，提供基于 ECharts 的数据可视化仪表盘。
 * 启动后会在默认浏览器中自动打开。
 */
public class VisualizationServer {
    private static final Logger log = LoggerFactory.getLogger(VisualizationServer.class);

    private final GameDataManager dataManager;
    private HttpServer server;
    private int port;

    public VisualizationServer(GameDataManager dataManager) {
        this.dataManager = dataManager;
    }

    /** Start the server on a free port and open browser. */
    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0); // port 0 = auto-assign
        port = server.getAddress().getPort();

        // Serve the dashboard HTML
        server.createContext("/", this::handleRoot);

        // REST API endpoints
        server.createContext("/api/heroes/position-stats", this::handleHeroPositionStats);
        server.createContext("/api/heroes/type-stats", this::handleHeroTypeStats);
        server.createContext("/api/heroes/ability-stats", this::handleHeroAbilityStats);
        server.createContext("/api/heroes/difficulty-stats", this::handleHeroDifficultyStats);
        server.createContext("/api/equipments/price-by-type", this::handleEquipmentPriceByType);
        server.createContext("/api/teams/win-rates", this::handleTeamWinRates);

        server.setExecutor(java.util.concurrent.Executors.newFixedThreadPool(2));
        server.start();

        log.info("Visualization server started on port {}", port);

        // Open browser
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI("http://localhost:" + port));
            }
        } catch (Exception e) {
            log.warn("Could not open browser automatically: {}", e.getMessage());
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            log.info("Visualization server stopped.");
        }
    }

    public int getPort() {
        return port;
    }

    // ===== HTTP helpers =====

    private void sendJson(HttpExchange exchange, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendHtml(HttpExchange exchange, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    // ===== Handlers =====

    private void handleRoot(HttpExchange exchange) throws IOException {
        sendHtml(exchange, DASHBOARD_HTML);
    }

    private void handleHeroPositionStats(HttpExchange exchange) throws IOException {
        List<Hero> heroes = dataManager.getHeroes();
        Map<String, Long> counts = heroes.stream()
                .filter(h -> h.getPosition() != null)
                .collect(Collectors.groupingBy(Hero::getPosition, Collectors.counting()));

        StringBuilder json = new StringBuilder("{\"categories\":[");
        json.append(counts.keySet().stream()
                .map(k -> "\"" + escapeJson(k) + "\"")
                .collect(Collectors.joining(",")));
        json.append("],\"values\":[");
        json.append(counts.values().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")));
        json.append("]}");
        sendJson(exchange, json.toString());
    }

    private void handleHeroTypeStats(HttpExchange exchange) throws IOException {
        List<Hero> heroes = dataManager.getHeroes();
        Map<String, Long> counts = heroes.stream()
                .filter(h -> h.getHeroType() != null)
                .collect(Collectors.groupingBy(Hero::getHeroType, Collectors.counting()));

        StringBuilder json = new StringBuilder("{\"categories\":[");
        json.append(counts.keySet().stream()
                .map(k -> "\"" + escapeJson(k) + "\"")
                .collect(Collectors.joining(",")));
        json.append("],\"values\":[");
        json.append(counts.values().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")));
        json.append("]}");
        sendJson(exchange, json.toString());
    }

    private void handleHeroAbilityStats(HttpExchange exchange) throws IOException {
        List<Hero> heroes = dataManager.getHeroes();
        // Aggregate by position
        Map<String, List<Hero>> byPosition = heroes.stream()
                .filter(h -> h.getPosition() != null && h.getSurvivalAbility() > 0)
                .collect(Collectors.groupingBy(Hero::getPosition));

        StringBuilder json = new StringBuilder("{\"positions\":[");
        json.append(byPosition.keySet().stream()
                .map(k -> "\"" + escapeJson(k) + "\"")
                .collect(Collectors.joining(",")));
        json.append("],\"survival\":[");
        json.append(byPosition.values().stream()
                .map(list -> String.format("%.1f",
                        list.stream().mapToInt(Hero::getSurvivalAbility).average().orElse(0)))
                .collect(Collectors.joining(",")));
        json.append("],\"attack\":[");
        json.append(byPosition.values().stream()
                .map(list -> String.format("%.1f",
                        list.stream().mapToInt(Hero::getAttackAbility).average().orElse(0)))
                .collect(Collectors.joining(",")));
        json.append("],\"skill\":[");
        json.append(byPosition.values().stream()
                .map(list -> String.format("%.1f",
                        list.stream().mapToInt(Hero::getSkillAbility).average().orElse(0)))
                .collect(Collectors.joining(",")));
        json.append("],\"support\":[");
        json.append(byPosition.values().stream()
                .map(list -> String.format("%.1f",
                        list.stream().mapToInt(Hero::getSupportAbility).average().orElse(0)))
                .collect(Collectors.joining(",")));
        json.append("]}");
        sendJson(exchange, json.toString());
    }

    private void handleHeroDifficultyStats(HttpExchange exchange) throws IOException {
        List<Hero> heroes = dataManager.getHeroes();
        Map<String, Long> counts = heroes.stream()
                .filter(h -> h.getDifficulty() != null)
                .collect(Collectors.groupingBy(Hero::getDifficulty, Collectors.counting()));

        StringBuilder json = new StringBuilder("{\"categories\":[");
        json.append(counts.keySet().stream()
                .map(k -> "\"" + escapeJson(k) + "\"")
                .collect(Collectors.joining(",")));
        json.append("],\"values\":[");
        json.append(counts.values().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")));
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
        json.append(pricesByType.keySet().stream()
                .map(k -> "\"" + escapeJson(k) + "\"")
                .collect(Collectors.joining(",")));
        json.append("],\"avgPrices\":[");
        json.append(pricesByType.values().stream()
                .map(list -> String.format("%.0f",
                        list.stream().mapToInt(Integer::intValue).average().orElse(0)))
                .collect(Collectors.joining(",")));
        json.append("],\"maxPrices\":[");
        json.append(pricesByType.values().stream()
                .map(list -> String.valueOf(list.stream().mapToInt(Integer::intValue).max().orElse(0)))
                .collect(Collectors.joining(",")));
        json.append("],\"minPrices\":[");
        json.append(pricesByType.values().stream()
                .map(list -> String.valueOf(list.stream().mapToInt(Integer::intValue).min().orElse(0)))
                .collect(Collectors.joining(",")));
        json.append("],\"counts\":[");
        json.append(pricesByType.values().stream()
                .map(list -> String.valueOf(list.size()))
                .collect(Collectors.joining(",")));
        json.append("]}");
        sendJson(exchange, json.toString());
    }

    private void handleTeamWinRates(HttpExchange exchange) throws IOException {
        List<Team> teams = dataManager.getTeams().stream()
                .sorted(Comparator.comparingDouble(Team::getWinRate).reversed())
                .collect(Collectors.toList());

        StringBuilder json = new StringBuilder("{\"names\":[");
        json.append(teams.stream()
                .map(t -> "\"" + escapeJson(t.getTeamName()) + "\"")
                .collect(Collectors.joining(",")));
        json.append("],\"winRates\":[");
        json.append(teams.stream()
                .map(t -> String.format("%.1f", t.getWinRate() * 100))
                .collect(Collectors.joining(",")));
        json.append("],\"totalMatches\":[");
        json.append(teams.stream()
                .map(t -> String.valueOf(t.getTotalMatches()))
                .collect(Collectors.joining(",")));
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

    // ===== Embedded ECharts dashboard HTML =====

    private static final String DASHBOARD_HTML = ""
            + ko("<!DOCTYPE html>")
            + ko("<html lang=\"zh-CN\">")
            + ko("<head>")
            + ko("<meta charset=\"UTF-8\">")
            + ko("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
            + ko("<title>王者荣耀数据可视化仪表盘</title>")
            + ko("<script src=\"https://cdn.jsdelivr.net/npm/echarts@5/dist/echarts.min.js\"></script>")
            + ko("<style>")
            + ko("  * { margin: 0; padding: 0; box-sizing: border-box; }")
            + ko("  body { font-family: 'Microsoft YaHei', -apple-system, sans-serif; background: #0f1923; color: #e0e6ed; }")
            + ko("  .header { background: linear-gradient(135deg, #1a2a3a, #0d1b2a); padding: 24px 32px; border-bottom: 2px solid #c8a84e; }")
            + ko("  .header h1 { font-size: 24px; color: #c8a84e; letter-spacing: 2px; }")
            + ko("  .header p { font-size: 13px; color: #8899aa; margin-top: 6px; }")
            + ko("  .container { max-width: 1400px; margin: 0 auto; padding: 20px; }")
            + ko("  .grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }")
            + ko("  .card { background: #1a2a3a; border-radius: 8px; padding: 16px; border: 1px solid #2a3a4a; }")
            + ko("  .card h2 { font-size: 15px; color: #c8a84e; margin-bottom: 12px; padding-bottom: 8px; border-bottom: 1px solid #2a3a4a; }")
            + ko("  .card-full { grid-column: 1 / -1; }")
            + ko("  .chart { width: 100%; height: 340px; }")
            + ko("  @media (max-width: 900px) { .grid { grid-template-columns: 1fr; } }")
            + ko("</style>")
            + ko("</head>")
            + ko("<body>")
            + ko("<div class=\"header\">")
            + ko("  <h1>王者荣耀 — 数据可视化仪表盘</h1>")
            + ko("  <p>英雄分布 · 装备价格 · 战队胜率 · 能力雷达</p>")
            + ko("</div>")
            + ko("<div class=\"container\">")
            + ko("  <div class=\"grid\">")
            + ko("    <div class=\"card\"><h2>英雄定位分布</h2><div id=\"chart-position\" class=\"chart\"></div></div>")
            + ko("    <div class=\"card\"><h2>英雄类型分布</h2><div id=\"chart-type\" class=\"chart\"></div></div>")
            + ko("    <div class=\"card card-full\"><h2>各位置能力均值</h2><div id=\"chart-ability\" class=\"chart\" style=\"height:400px;\"></div></div>")
            + ko("    <div class=\"card\"><h2>装备价格统计（按类型）</h2><div id=\"chart-equip-price\" class=\"chart\"></div></div>")
            + ko("    <div class=\"card\"><h2>英雄难度分布</h2><div id=\"chart-difficulty\" class=\"chart\"></div></div>")
            + ko("    <div class=\"card card-full\"><h2>战队胜率排行</h2><div id=\"chart-team-winrate\" class=\"chart\" style=\"height:400px;\"></div></div>")
            + ko("  </div>")
            + ko("</div>")
            + ko("<script>")
            + ko("const BASE = '';")
            + ko("const darkTheme = { backgroundColor: 'transparent', textStyle: { color: '#e0e6ed' } };")
            + ko("")
            + ko("function fetchJSON(url) { return fetch(url).then(r => r.json()); }")
            + ko("")
            + ko("function initChart(id) {")
            + ko("  const el = document.getElementById(id);")
            + ko("  if (!el) return null;")
            + ko("  const chart = echarts.init(el, 'dark');")
            + ko("  window.addEventListener('resize', () => chart.resize());")
            + ko("  return chart;")
            + ko("}")
            + ko("")
            + ko("// 1. Hero Position Pie")
            + ko("fetchJSON('/api/heroes/position-stats').then(data => {")
            + ko("  const chart = initChart('chart-position');")
            + ko("  if (!chart) return;")
            + ko("  chart.setOption({")
            + ko("    tooltip: { trigger: 'item', formatter: '{b}: {c} 个 ({d}%)' },")
            + ko("    legend: { orient: 'vertical', left: 'left', textStyle: { color: '#e0e6ed' } },")
            + ko("    series: [{ type: 'pie', radius: ['35%', '65%'], center: ['60%', '50%'],")
            + ko("      label: { show: false },")
            + ko("      data: data.categories.map((c, i) => ({ name: c, value: data.values[i] })),")
            + ko("      itemStyle: { borderColor: '#1a2a3a', borderWidth: 2 }")
            + ko("    }]")
            + ko("  });")
            + ko("});")
            + ko("")
            + ko("// 2. Hero Type Pie")
            + ko("fetchJSON('/api/heroes/type-stats').then(data => {")
            + ko("  const chart = initChart('chart-type');")
            + ko("  if (!chart) return;")
            + ko("  chart.setOption({")
            + ko("    tooltip: { trigger: 'item', formatter: '{b}: {c} 个 ({d}%)' },")
            + ko("    legend: { orient: 'vertical', left: 'left', textStyle: { color: '#e0e6ed' } },")
            + ko("    series: [{ type: 'pie', radius: ['35%', '65%'], center: ['60%', '50%'],")
            + ko("      label: { show: false },")
            + ko("      data: data.categories.map((c, i) => ({ name: c, value: data.values[i] })),")
            + ko("      itemStyle: { borderColor: '#1a2a3a', borderWidth: 2 }")
            + ko("    }]")
            + ko("  });")
            + ko("});")
            + ko("")
            + ko("// 3. Ability Radar by Position")
            + ko("fetchJSON('/api/heroes/ability-stats').then(data => {")
            + ko("  const chart = initChart('chart-ability');")
            + ko("  if (!chart) return;")
            + ko("  const series = data.positions.map((p, i) => ({")
            + ko("    name: p, type: 'radar',")
            + ko("    data: [{ value: [data.survival[i], data.attack[i], data.skill[i], data.support[i]] }],")
            + ko("    lineStyle: { width: 2 },")
            + ko("    areaStyle: { opacity: 0.1 }")
            + ko("  }));")
            + ko("  chart.setOption({")
            + ko("    tooltip: { trigger: 'item' },")
            + ko("    legend: { data: data.positions, textStyle: { color: '#e0e6ed' } },")
            + ko("    radar: {")
            + ko("      indicator: [")
            + ko("        { name: '生存', max: 10 },")
            + ko("        { name: '攻击', max: 10 },")
            + ko("        { name: '技能', max: 10 },")
            + ko("        { name: '辅助', max: 10 }")
            + ko("      ],")
            + ko("      axisName: { color: '#e0e6ed' },")
            + ko("      splitArea: { areaStyle: { color: ['rgba(200,168,78,0.03)', 'rgba(200,168,78,0.06)'] } }")
            + ko("    },")
            + ko("    series: series")
            + ko("  });")
            + ko("});")
            + ko("")
            + ko("// 4. Equipment Price by Type")
            + ko("fetchJSON('/api/equipments/price-by-type').then(data => {")
            + ko("  const chart = initChart('chart-equip-price');")
            + ko("  if (!chart) return;")
            + ko("  chart.setOption({")
            + ko("    tooltip: { trigger: 'axis' },")
            + ko("    legend: { textStyle: { color: '#e0e6ed' } },")
            + ko("    xAxis: { type: 'category', data: data.categories, axisLabel: { color: '#8899aa' } },")
            + ko("    yAxis: { type: 'value', name: '金币', axisLabel: { color: '#8899aa' } },")
            + ko("    series: [")
            + ko("      { name: '平均价格', type: 'bar', barWidth: '30%', data: data.avgPrices.map(v => Math.round(Number(v))), itemStyle: { color: '#c8a84e' } },")
            + ko("      { name: '最高价格', type: 'bar', barWidth: '15%', data: data.maxPrices, itemStyle: { color: '#e8594e' } }")
            + ko("    ]")
            + ko("  });")
            + ko("});")
            + ko("")
            + ko("// 5. Difficulty Distribution")
            + ko("fetchJSON('/api/heroes/difficulty-stats').then(data => {")
            + ko("  const chart = initChart('chart-difficulty');")
            + ko("  if (!chart) return;")
            + ko("  chart.setOption({")
            + ko("    tooltip: { trigger: 'axis' },")
            + ko("    xAxis: { type: 'category', data: data.categories, axisLabel: { color: '#8899aa' } },")
            + ko("    yAxis: { type: 'value', name: '英雄数量', axisLabel: { color: '#8899aa' } },")
            + ko("    series: [{ type: 'bar', data: data.values.map((v,i) => ({")
            + ko("      value: v,")
            + ko("      itemStyle: { color: ['#c8a84e','#e8594e','#4e8be8','#5abf5a'][i % 4] }")
            + ko("    })), barWidth: '50%', label: { show: true, position: 'top', color: '#e0e6ed' } }]")
            + ko("  });")
            + ko("});")
            + ko("")
            + ko("// 6. Team Win Rate Ranking")
            + ko("fetchJSON('/api/teams/win-rates').then(data => {")
            + ko("  const chart = initChart('chart-team-winrate');")
            + ko("  if (!chart) return;")
            + ko("  // Reverse so highest is at top in horizontal bar")
            + ko("  const names = data.names.slice().reverse();")
            + ko("  const rates = data.winRates.slice().reverse();")
            + ko("  const matches = data.totalMatches.slice().reverse();")
            + ko("  chart.setOption({")
            + ko("    tooltip: { trigger: 'axis', formatter: function(params) {")
            + ko("      const i = params[0].dataIndex;")
            + ko("      return names[i] + '<br/>胜率: ' + rates[i] + '%<br/>总场次: ' + matches[i];")
            + ko("    } },")
            + ko("    xAxis: { type: 'value', name: '胜率(%)', axisLabel: { color: '#8899aa' }, max: 100 },")
            + ko("    yAxis: { type: 'category', data: names, axisLabel: { color: '#e0e6ed' } },")
            + ko("    series: [{")
            + ko("      type: 'bar', data: rates,")
            + ko("      barWidth: '60%',")
            + ko("      label: { show: true, position: 'right', formatter: function(p) { return p.value + '%'; }, color: '#e0e6ed' },")
            + ko("      itemStyle: { color: function(p) {")
            + ko("        const v = parseFloat(p.value);")
            + ko("        return v >= 60 ? '#5abf5a' : v >= 45 ? '#c8a84e' : '#e8594e';")
            + ko("      } }")
            + ko("    }]")
            + ko("  });")
            + ko("});")
            + ko("</script>")
            + ko("</body>")
            + ko("</html>");
    private static String ko(String s) { return s + "\n"; }
}
