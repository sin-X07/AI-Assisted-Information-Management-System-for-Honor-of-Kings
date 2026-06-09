# 教学反思与设计决策总结

> 本文档记录了在 AI 辅助构建《王者荣耀》信息管理系统过程中，于关键架构节点做出的纠偏决策。
> 每个问题遵循"现象 → 原因分析 → 重构决策"的结构展开，覆盖项目从 v1.0 控制台到 v2.0 双模式的完整演进。
> 问题一至五对应 v1.0 控制台阶段，问题六至十对应 v2.0 扩展阶段。

---

## 问题一：模型层数据强耦合与单一职责原则的冲突

### 现象

开发初期，AI 为了快速提供初始数据，尝试将假数据硬编码在 `model/Hero.java` 等实体类的静态方法中（如 `createDefaultHeroes()`）。

### 原因分析

- **违反单一职责原则 (SRP)**: `Hero` 类的职责应当极为纯粹——仅作为承载单个英雄属性的数据结构。一旦将全局假数据初始化的行为混入其中，会导致模型类臃肿且职责不清。
- **生命周期死锁**: 当项目要求多个战队、玩家、英雄、装备层层包含且具备复杂的级联绑定时，在各自的 model 类内部初始化会导致对象之间无法交叉咬合，形成无法解开的强耦合。

### 重构决策

实施**业务逻辑与辅助工具彻底解耦**策略：

1. **剥离数据生产线**: 精简所有 model 实体类，移除所有硬编码的假数据初始化方法，使其回归纯净的字段封装（Getter/Setter 与构造函数）。
2. **建立独立的组装工厂**: 在 `src/util/` 包下创建 `DataInitializer.java`，充当总装配车间——在程序启动时严格按照依赖顺序进行双向绑定组装，并统一注入到 `GameDataManager`。

### 架构对比

| 维度 | 重构前（AI 方案） | 重构后（人工修正） |
|------|-------------------|--------------------|

| 初始化位置 | 散落在各 model 类静态方法中 | 集中在 `DataInitializer` |
| Hero 类的职责 | 数据结构 + 数据工厂（混淆） | 纯数据结构（单一） |
| 跨实体绑定 | 无法实现级联（各自独立初始化） | 统一编排，双向绑定 |
| 扩展性（v2.0） | 无法扩展到 90 英雄 | 简单追加数据行即可 |

---

## 问题二：直接返回集合引用带来的表示泄露

### 现象

在设计 `GameDataManager` 的集合读取方法（如 `getHeroes()`）时，最初的朴素写法是直接将类内部 `ArrayList` 的内存引用返回给外部调用者。

### 原因分析

- **封装性荡然无存**: 外部菜单层或未授权模块拿到引用后，可以直接执行 `.clear()` 或 `.remove()`，在绕过系统业务验证的情况下偷偷篡改或擦除内存中的核心数据——这就是经典的"表示泄露"（Representation Leak）。

### 重构决策

贯彻**数据只读安全性与封装性保护**的设计标准。所有对外公开的集合 Getter 方法统一使用不可变视图包装：

```java
public List<Hero> getHeroes() {
    return Collections.unmodifiableList(new ArrayList<>(heroes));
}
```

**原理**: 先通过 `new ArrayList<>(...)` 做浅拷贝，再用 `Collections.unmodifiableList` 包裹。外部可以安全遍历和只读访问，但任何修改集合结构的操作都会被 JVM 抛出 `UnsupportedOperationException` 强行拦截。v2.0 的 GUI 层多个面板共享同一数据源时，此保护机制防止了 UI 事件线程意外篡改数据。

---

## 问题三：战队数据代理个人战绩导致排行榜失真

### 现象

在编写 `RankingService` 前，系统面临一个设计分歧：由于玩家个人的胜率和总场次没有独立存储，系统曾尝试用所属战队的统计数据进行间接代理。

### 原因分析

- **领域建模失真**: 同一战队中的顶级核心与刚入队的替补新人，在系统中的战绩将完全并列——数据离散度为零。
- **排序算法失效**: 排行榜要求"优先按胜率降序；胜率相同时按总场次降序"的双重条件排序。若同队五人共享一套战队数据，则五人永远卡在完全相同的顺位，全服排行榜功能彻底失效。

### 重构决策

做出两项关键决策，绝不使用战队数据代理玩家个人战绩：

1. **字段独立化 + 动态计算**: `Player` 类保留 `winRate` / `totalMatches` 等字段（用于 `DataInitializer` 初始化时提供参考值），但 `RankingService` 在排序时**不读取这些静态字段**，而是动态遍历 `Player.matchOverviews` 中的 `MatchRecord` 列表，实时统计胜利记录数来计算胜率和总场次。
2. **离散化假数据注入**: `DataInitializer.createPlayer()` 为各玩家生成不同数量的对局记录，通过胜负比例控制不同胜率（如花海 12 胜/15 场 = 80%，轩染 4 胜/8 场 = 50%），为排行榜排序算法提供了真实有效的测试土壤。

### 关键代码路径

```java
static double calculateWinRate(Player player) {
    List<MatchRecord> records = player.getMatchOverviews();
    long wins = records.stream()
        .filter(r -> "胜利".equals(r.getResult()))
        .count();
    return (double) wins / records.size();
}
```

---

## 问题四：控制台键盘脏输入崩溃与终端管道中文编码紊乱

### 现象

控制台系统面临两层边缘危机：

1. **非健壮输入崩溃**: 系统菜单要求输入数字时，用户误输入英文字母，`Integer.parseInt()` 直接抛出 `NumberFormatException` 导致控制台程序瞬间崩溃。
2. **自动化流测试编码偏离**: 采用 PowerShell 管道模拟流输入时，操作系统终端管道的中文编码干扰导致在传输中文名称进行检索时无法正常命中对象。

### 重构决策

采取高强度防御性编程 + 自适应检索方案：
**1. 边界异常全面拦截（`InputHelper.java`）**

```java
public int readInt(String prompt) {
    while (true) {
        String value = readRequiredString(prompt);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.out.println("请输入有效的整数。");
        }
    }
}
```

捕获到 `NumberFormatException` 时程序绝不崩溃，而是提示错误并循环兜底。`readIntInRange()` 进一步限制输入值必须在 `[min, max]` 区间。
**2. 输入流结束安全保护**

```java
public String readLine(String prompt) {
    if (!scanner.hasNextLine()) {
        System.out.println("输入流已结束，程序退出。");
        System.exit(0);
    }
    return scanner.nextLine().trim();
}
```

当检测到输入流耗尽时，程序优雅退出，消除了未捕获流异常的隐患。
**3. 多维兼容检索算法**

在 `Main.searchHero()` 中实现"名称或编号双兼容"策略：先按中文名查找，再按英文 ID 兜底，完美绕开了终端环境的编码干扰。

---

## 问题五：中英文混排名单排版错位

### 现象

排行榜输出中，英文 ID（如 `Cat`、`Fly`）所在行与中文昵称行错位——中文占 2 个字符宽度而 ASCII 占 1 个，导致 `printf` 的固定 `%s` 宽度失效。

### 重构决策

新增 `formatWithChinese()` 方法，动态检测每个字符的 Unicode 范围并计算实际显示宽度，再补齐空格：

```java
private String formatWithChinese(String str, int totalLen) {
    int currentDisplayWidth = 0;
    for (char c : str.toCharArray()) {
        if (Character.toString(c).matches("[\\u4e00-\\u9fa5]")) {
            currentDisplayWidth += 2;
        } else {
            currentDisplayWidth += 1;
        }
    }
    int paddingSpaces = totalLen - currentDisplayWidth;
    return paddingSpaces <= 0 ? str : str + " ".repeat(paddingSpaces);
}
```

此方法应用于表头和数据行的所有字符串列，确保包含中文字符的多行在任何终端宽度下都能对齐。v2.0 的 GUI 排行榜面板使用 JTable 自动处理列宽，不再依赖此方法，但控制台模式仍保留此精确的对齐能力。

---

## 问题六：纯控制台架构无法满足现代信息管理需求

### 现象

项目完成 v1.0 控制台版本后，暴露出以下局限：

- **用户体验差**: 纯字符终端操作，无法展示图片、图表等视觉信息，检索结果靠浏览文字
- **数据不可持久**: 程序退出后所有修改丢失，每次启动恢复初始数据集
- **操作无留痕**: 管理员做了什么修改、何时修改、由谁修改，完全无法追溯
- **无数据可视分析**: 排行榜只有数字列表，无法直观呈现分布和趋势

### 原因分析

- 原设计仅作为课程作业，以"跑通功能"为目标，未考虑信息管理系统在实际使用中的持久化、审计和可视化需求
- 控制台交互在数据量小（15 英雄/3 战队）时可接受，当数据扩展到 90 英雄/20 战队时，纯文字浏览效率极低
- 无日志体系意味着无法满足"谁在什么时间修改了什么"的基本审计要求

### 重构决策

实施 v2.0 三线扩展策略：**持久化 + GUI + Web 可视化 + 日志审计**

**1. Maven 构建迁移**: 从纯 JDK 切换到 Maven 构建，引入依赖管理能力

```xml
<!-- pom.xml: 引入 SQLite / FlatLaf / SLF4J 依赖 -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.49.1.0</version>
</dependency>
```

**2. 双模式入口设计**: 控制台 `Main.java` 保留不动，新增 `ui/AppLauncher.java` 作为 GUI 入口，两套界面共享同一业务逻辑层。

**3. 分层扩展**: 在原三层（model/service/util）基础上增加 db/ 和 ui/ 两层，不破坏原有架构。

### 架构对比

| 维度 | v1.0 控制台 | v2.0 双模式 |
|------|-------------|-------------|

| 入口 | 仅 `Main.java` | `Main.java` + `AppLauncher.java` |
| 构建 | javac 手动编译 | Maven + shade 插件 |
| 数据 | 内存 ArrayList | SQLite + 内存双缓存 |
| 用户界面 | 控制台菜单 | 控制台 + Swing GUI + Web |
| 日志 | 无 | SQLite 日志表 + SLF4J 文件 |
| 依赖 | 零 | SQLite / FlatLaf / SLF4J |

---

## 问题七：内存数据模型无法满足持久化需求

### 现象

v1.0 的所有数据存放在 `GameDataManager` 的 `ArrayList` 中，程序退出后：

- 管理员辛苦添加的英雄数据全部丢失
- 每次启动重置为初始数据集
- 无法跨会话保留用户自定义数据

### 原因分析

- 课程作业要求中提到的"文件 I/O"方案（CSV 读写）存在先天不足：检索需要全表扫描，写入需全量覆盖，并发修改无法处理
- SQLite 嵌入式数据库在 Java 生态中是最轻量的持久化方案：零配置、单文件、标准 SQL、JDBC 驱动仅 1MB
- `Searchable` 接口的 20 个方法天然适配 DAO 模式——每个方法都可以映射为一条 SQL 语句

### 重构决策

实施 SQLite 持久化 + DAO 模式：

**1. DatabaseManager（单例）**: 管理 SQLite 连接的生命周期，`initialize()` 自动建 5 张表

```java
public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;

    private static final String HEROES_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS heroes (
            hero_id TEXT PRIMARY KEY, hero_name TEXT NOT NULL,
            title TEXT, position TEXT, hero_type TEXT, difficulty TEXT,
            survival_ability INT, attack_ability INT,
            skill_ability INT, support_ability INT,
            ...
        )
    """;
}
```

**2. GameDataDao**: 全字段 CRUD，ResultSet → 实体映射

- `mapHero(rs)` 将 ResultSet 的 17 个字段映射为 Hero 对象
- 推荐装备列表用逗号分隔存储在 `recommended_equipment_ids` 字段，Java 侧通过 `String.join()` / `Arrays.asList()` 转换

**3. 双缓存策略**: 程序启动时从 SQLite 加载数据到内存，GUI 操作同时更新内存和数据库

```java
// GameDataManager.java — 数据同步
public void persistAllToDatabase() {
    heroes.forEach(gameDataDao::insertHero);
    equipments.forEach(gameDataDao::insertEquipment);
    teams.forEach(gameDataDao::insertTeam);
}
```

### 关键决策

允许内存和数据库共存（不是二选一），原因：

- 游戏数据操作频繁，每次 CRUD 都直写 SQLite 会增加延迟
- 内存操作 + 异步写入（`persistAllToDatabase()` 在启动时批量写入）兼顾了性能和持久化
- `Collections.unmodifiableList` 保护机制在数据库模式下仍然有效

---

## 问题八：Swing GUI 架构设计——从零构建桌面应用

### 现象

在决定从控制台迁移到 GUI 时，面临一系列架构问题：

- **布局选择**: 使用什么布局管理器？单窗口还是多窗口？
- **组件选型**: 列表用 JList 还是 JTable？导航用菜单栏还是侧边栏？
- **主题外观**: 默认的 Swing Metal 外观在 Windows 上视觉老旧
- **资源共享**: GUI 多个面板如何共享同一个 `GameDataManager` 实例？
- **权限控制**: 如何在 GUI 中实现管理员/玩家的角色差异？

### 原因分析

- JTable 比 JList 更适合表格数据展示，支持排序、列宽调整、行选择
- CardLayout 比多个 JFrame 更合理——所有面板共享同一个窗口，切换流畅
- Metal 主题在 2026 年的桌面环境下显得过时，FlatLaf 提供了现代扁平化外观
- 依赖注入（将 `GameDataManager` 等通过 MainFrame 构造函数传入）比单例模式更清晰

### 重构决策

**1. 布局架构**: BorderLayout 主窗口 + 左侧 180px 侧边栏（BoxLayout）+ 右侧 CardLayout 内容区

```structure
MainFrame (BorderLayout)
├── 侧边栏 (JPanel, 固定宽度 180px)
│   ├── 欢迎 / 英雄管理 / 装备管理 / 战队管理
│   ├── 玩家查询 / 排行榜
│   └── 数据管理 / 导出 / 数据可视化 (管理员专属)
└── 内容区 (CardLayout) → 切换各 Panel
```

**2. 英雄面板**: JTable + `TableRowSorter` + `DocumentListener` 实时搜索过滤

```java
searchField.getDocument().addDocumentListener(new DocumentListener() {
    public void insertUpdate(DocumentEvent e) { filterByKeyword(); }
    public void removeUpdate(DocumentEvent e) { filterByKeyword(); }
    public void changedUpdate(DocumentEvent e) { filterByKeyword(); }
});
```

**3. FlatLaf 主题**: 类型安全的反射调用，失败时回退到系统主题

```java
try {
    Class.forName("com.formdev.flatlaf.FlatLightLaf")
            .getMethod("setup").invoke(null);
} catch (Exception e) {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
}
```

**4. 侧边栏权限控制**: 管理员可见全部按钮，玩家隐藏管理类按钮

```java
dataMgmtBtn.setVisible(isAdmin);
exportBtn.setVisible(isAdmin);
vizBtn.setVisible(isAdmin);
```

**5. 对话框设计**: 每个实体（Hero/Equipment/Team）有 Detail（只读查看）和 Edit（表单编辑）两个对话框，分离查看和编辑职责避免界面混乱。

---

## 问题九：嵌入式 Web 可视化服务器的设计权衡

### 现象

在构思"数据可视化"功能时，面临三种技术路线的选择：

1. **Swing 原生图表**: 使用 JFreeChart 等 Java 图表库在 GUI 中绘制
2. **Python Flask + ECharts**: 启动 Python 后端提供可视化页面
3. **JDK 内置 HttpServer + ECharts**: 在 Java 进程中嵌入 HTTP 服务器

### 原因分析

- JFreeChart 库体积较大，图表交互性不如 ECharts（无鼠标悬停、缩放、动画）
- Python Flask 方案需要用户安装 Python 环境，破坏了 Java 项目的自包含性
- JDK 自 `JDK 6` 起内置了 `com.sun.net.httpserver.HttpServer`，零外部依赖即可启动 HTTP 服务
- ECharts 通过 CDN 加载，无需下载图表库到本地，浏览器渲染效果远超 Swing 绘制

### 重构决策

选择**JDK HttpServer + ECharts CDN**方案：

**1. HTTP 服务器**: 端口自动分配（`new InetSocketAddress(0)`），避免端口冲突

```java
server = HttpServer.create(new InetSocketAddress(0), 0);
port = server.getAddress().getPort();  // 自动分配的端口
server.setExecutor(Executors.newFixedThreadPool(2));
server.start();
log.info("Visualization server started at http://localhost:{}", port);
```

**2. 8 个 JSON API 端点**: 覆盖所有数据维度

| API | 类型 | 用途 |
|-----|------|------|

| `/api/heroes/position-stats` | 柱状图 | 各位置(打野/中路/发育路/对抗路/游走)英雄数量 |
| `/api/heroes/type-stats` | 饼图 | 英雄类型(刺客/法师/射手/战士/坦克/辅助)分布 |
| `/api/heroes/ability-stats` | 雷达图 | 各位置的能力四维(生存/攻击/技能/支援) |
| `/api/heroes/difficulty-stats` | 饼图 | 难度(简单/中等/困难)分布 |
| `/api/equipments/price-by-type` | 箱线图 | 6 类装备的均价/最高/最低/数量 |
| `/api/teams/win-rates` | 柱状图 | 20 支战队胜率排行 |
| `/api/heroes/search?q=` | 搜索 | 模糊搜索（Levenshtein 距离） |
| `/api/data/summary` | 摘要 | 数据总量概览 |

**3. 模糊搜索算法**: Levenshtein 距离 + 加权评分

```java
// 模糊匹配评分：名称匹配×1.2，称号匹配×1.1，阈值 0.4
double nameScore = fuzzyScore(query, hero.getHeroName());
score = Math.max(score, nameScore * 1.2);
```

**4. 自动打开浏览器**: 利用 `java.awt.Desktop.browse()`

```java
if (Desktop.isDesktopSupported()
    && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
    Desktop.getDesktop().browse(new URI("http://localhost:" + port));
}
```

### 已知局限

- JSON 响应通过字符串拼接生成（未使用 Jackson/Gson），避免增加依赖
- ECharts 通过 CDN 加载，离线环境无法使用
- 无 HTTPS 和认证，仅供本地访问
- 静态文件路径依赖 `src/ui/web/static/` 目录结构

---

## 问题十：操作日志系统的双写入策略

### 现象

v1.0 系统没有日志功能，管理员执行了哪些操作完全不可追溯。v2.0 引入 CRUD 操作后，需要回答一个问题：**日志应该写到哪里？**

- 只写文件？文件日志适合排错（Exception 堆栈），但不方便查询（按管理员/操作类型过滤）
- 只写数据库？数据库日志适合查询和统计，但无法与应用的常规错误日志统一
- 日志的性能开销对主流程影响多大？

### 原因分析

- SLF4J + Logback 已经是 Maven 依赖列表中的标准日志方案，用于记录应用运行状态、SQL 异常、启动信息
- 操作日志具有明确的结构化字段（操作人、操作类型、目标类型、目标 ID、时间、结果），适合存储在数据库关系表中
- 两种日志的消费场景不同：文件日志给开发者排错用，数据库日志给审计用

### 重构决策

实施**双写入策略**——每次操作同时写入 SQLite 和 SLF4J 文件：
**1. 数据库日志表**

```sql
CREATE TABLE IF NOT EXISTS operation_logs (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    person_id   TEXT NOT NULL,
    username    TEXT NOT NULL,
    nickname    TEXT,
    operation_type   TEXT NOT NULL, -- ADD / UPDATE / DELETE / QUERY
    target_type      TEXT,          -- HERO / EQUIPMENT / TEAM / ADMIN
    target_id        TEXT,
    detail      TEXT,
    result      TEXT NOT NULL,      -- 成功 / 失败
    operation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**2. OperationLogService**: 封装双写入逻辑

```java
public void log(Person admin, String optType, String targetType,
                String targetId, String detail, String result) {
    // 写 SQLite
    OperationLog record = new OperationLog(personId, username, nickname,
            optType, targetType, targetId, detail, result);
    logDao.insert(record);

    // 写 SLF4J 文件
    log.info("操作日志: [{}] {} - {} {} ({}): {} -> {}",
            record.getOperationTime(), nickname, optType,
            targetType, targetId, detail, result);
}
```

**3. 便捷方法封装**:

```java
public void logSuccess(Person admin, String optType, String targetType,
                       String targetId, String detail) {
    log(admin, optType, targetType, targetId, detail, "成功");
}
public void logFailure(Person admin, String optType, String targetType,
                       String targetId, String detail) {
    log(admin, optType, targetType, targetId, detail, "失败");
}
```

**4. 单例模式**: `OperationLogService.getInstance()` 全局共享一个实例，避免重复创建 DAO 和日志连接。

### 对比其他方案

| 方案 | 优点 | 缺点 |
|------|------|------|

| 仅 SLF4J 文件日志 | 实现简单，统一的日志格式 | 搜索过滤困难，无法按管理员/操作类型查询 |
| 仅 SQLite 表日志 | 结构化存储，可 SQL 查询 | 无法与应用日志统一查看 |
| **双写入（最终选择）** | 两者兼得 | 日志路径不统一，两处写入有短暂不一致窗口 |

---

## 总结与收获

通过这次基于 AI 辅助的分层重构实践，我深刻体会到：**AI 是一把高效的双刃剑。** 它能极快地生成基础代码架子，但它缺乏对项目工程指标的边界敏锐度，容易写出高耦合、低封装、无异常保护的脆弱代码。

整个项目的成功落地，关键在于人类工程师在以下维度做出的绝对主导和纠偏决策：

### v1.0 阶段纠偏（控制台）

| 维度 | 人类决策 | AI 初始方案的缺陷 |
|------|----------|-------------------|

| 架构分层 | 严格的 model/service/util 三层分离 | 将初始化逻辑混入 model |
| 封装保护 | `Collections.unmodifiableList` + 浅拷贝 | 直接暴露内部集合引用 |
| 领域建模 | 通过 MatchRecord 动态计算个人战绩 | 用战队数据代理（排行榜失真）|
| 防御性编程 | `InputHelper` 全面异常兜底 | 无异常处理（直接崩溃）|
| 编码鲁棒性 | 名称+编号双兼容 + UTF-8 一致 | 仅单一中文匹配 |
| 排版精度 | `formatWithChinese` 动态宽度计算 | 固定 printf 宽度（中英文错位）|

### v2.0 阶段纠偏（双模式扩展）

| 维度 | 人类决策 | 初始方案的缺陷 |
|------|----------|----------------|

| 扩展策略 | 三线并行：持久化 + GUI + Web + 日志 | 无扩展规划 |
| 构建工具 | Maven 统一管理依赖 | 纯 JDK 编译难以管理外部库 |
| 持久化 | SQLite + DAO 模式 | 文件 I/O（CSV）检索和并发差 |
| GUI 架构 | BorderLayout + CardLayout 侧边栏 | 单窗口多 Tab 或 多 JFrame 方案 |
| 可视化 | JDK HttpServer + ECharts CDN | JFreeChart（体积大）或 Python（破坏自包含）|
| 日志体系 | SQLite + SLF4J 双写入 | 仅文件日志或仅数据库日志 |

### 五个核心教训

1. **不要让框架替你思考架构。** AI 生成的代码架子容易高耦合，每个类只做一件事是最好的防御。

2. **数据封装是软件的第一道防线。** `Collections.unmodifiableList` 一行代码避免了无数潜在的并发和篡改 bug。

3. **领域建模的偏差会在排序算法中放大。** 用战队数据代理个人战绩不仅违反领域语义，还会使排行榜完全失效。

4. **异常处理不是可选项，是合同的组成部分。** `InputHelper` 的每一层 `catch` 都对应一个用户可能遭遇的真实崩溃场景。

5. **从控制台到 GUI 的迁移中，业务逻辑层保持不变是架构成功的标志。** service 包中的 6 个类在 v2.0 中没有一行修改，证明了分层设计的价值。

---
