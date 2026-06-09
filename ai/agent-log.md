# Agent Log

> 本文档由三个子代理（Architect / Implementation / Testing）协作填充，记录项目从 v1.0 控制台版到 v2.0 双模式版本的架构演进、实现要点和测试覆盖情况。

---

## Architect Agent

### 1. 项目概述（v2.0.0 最终版本）

- **项目名称**: AI 辅助的《王者荣耀》信息管理系统
- **语言/平台**: Java 17+，Maven 构建，SQLite 持久化
- **界面模式**: 双模式——控制台（Main.java）+ 桌面 GUI（ui.AppLauncher，Swing + FlatLaf）
- **编码**: 全项目 UTF-8

### 2. 最终架构总览

```UML
┌──────────────────────────────────────────────────────────┐
│                   入口层 (Entry Points)                    │
│   Main.java (控制台)     ui/AppLauncher.java (GUI)        │
├──────────────────────────────────────────────────────────┤
│                     UI 层 (Swing)                         │
│  ui/LoginDialog.java    ui/MainFrame.java                 │
│  ui/panels/ (7 panels)  ui/dialogs/ (6 dialogs)          │
│  ui/web/VisualizationServer.java  (ECharts 仪表盘)       │
├──────────────────────────────────────────────────────────┤
│                 业务逻辑层 (service/)                      │
│  AuthenticationService  GameDataManager                  │
│  RankingService         FileStorageService                │
│  OperationLogService    Searchable (interface)            │
├──────────────────────────────────────────────────────────┤
│                 数据访问层 (db/)                           │
│  DatabaseManager  GameDataDao  AdminDao  OperationLogDao  │
├──────────────────────────────────────────────────────────┤
│         领域模型层 (model/)       工具层 (util/)           │
│  8 实体类                     DataInitializer             │
│  Person/Admin/Player/Hero     InputHelper                 │
│  Team/Equipment/MatchRecord   DbQuickCheck                │
│  MatchParticipant                                         │
└──────────────────────────────────────────────────────────┘
```

### 3. 最终包结构与文件清单

#### src/ — 完整文件清单（40+ 源文件）

| 包 | 文件 | 职责 |
|----|------|------|

| `根` | `Main.java` | 控制台入口，角色感知菜单 + 完整交互逻辑 |
| `model/` | `Person.java` | 人员抽象基类，含 id/username/password/nickname/role/status |
| | `Admin.java` | 管理员实体，继承 Person，增加管理级别/部门/权限标志 |
| | `Player.java` | 玩家实体，继承 Person，含段位/位置/对局记录列表 |
| | `Team.java` | 战队实体，含成员列表/常用英雄/荣誉/胜率 |
| | `Hero.java` | 英雄实体，含定位/类型/难度/4 技能/推荐装备列表 |
| | `Equipment.java` | 装备实体，含类型/价格/11 项数值属性/被动效果 |
| | `MatchRecord.java` | 对局记录，含时间/模式/结果/时长/参与者 |
| | `MatchParticipant.java` | 对局参与者，含英雄/阵营/KDA |
| `service/` | `Searchable.java` | 核心接口，定义 20 个方法的增删改查契约 |
| | `GameDataManager.java` | 数据仓库，实现 Searchable，含级联查询 + 数据库同步 |
| | `AuthenticationService.java` | 用户认证，支持 SQLite 持久化管理员账号 |
| | `RankingService.java` | 排行榜服务，动态胜率计算 + Comparator 排序 + 中文对齐 |
| | `FileStorageService.java` | 文件导出，UTF-8 + try-with-resources 安全关闭 |
| | `OperationLogService.java` | 操作日志服务，SQLite 持久化 + SLF4J 滚动文件日志 |
| `db/` | `DatabaseManager.java` | SQLite 连接管理 + 5 张表自动建表（单例模式） |
| | `GameDataDao.java` | 英雄/装备/战队 DAO，全字段 CRUD |
| | `AdminDao.java` | 管理员 DAO，认证+密码管理 |
| | `OperationLog.java` | 操作日志实体，含时间/操作人/操作类型/结果 |
| | `OperationLogDao.java` | 操作日志 DAO，含多维查询和统计 |
| `util/` | `DataInitializer.java` | 初始化 90 英雄/88 装备/20 战队/70+ 玩家数据 |
| | `InputHelper.java` | Scanner 封装，防 NumberFormatException 崩溃兜底 |
| | `DbQuickCheck.java` | 数据库快速检查工具 |
| `ui/` | `AppLauncher.java` | GUI 入口：初始化 FlatLaf + SQLite + 启动登录对话框 |
| | `LoginDialog.java` | Swing 登录对话框，GridBagLayout 布局 |
| | `MainFrame.java` | 主窗口，BorderLayout + 左侧 CardLayout 侧边栏导航 |
| `ui/panels/` | `WelcomePanel.java` | 欢迎面板，显示系统概览和用户信息 |
| | `HeroPanel.java` | 英雄列表面板，JTable + 搜索过滤 + 双击详情 |
| | `EquipmentPanel.java` | 装备列表面板，JTable + 搜索过滤 |
| | `TeamPanel.java` | 战队列表面板，JTable + 胜率显示 |
| | `PlayerQueryPanel.java` | 玩家查询面板，级联显示战队/英雄/装备 |
| | `RankingPanel.java` | 排行榜面板，JTable 显示双重降序排名 |
| | `DataManagementPanel.java` | 数据管理面板，CRUD 操作表单 |
| `ui/dialogs/` | `HeroDetailDialog.java` | 英雄详情对话框 |
| | `HeroEditDialog.java` | 英雄编辑对话框 |
| | `EquipmentDetailDialog.java` | 装备详情对话框 |
| | `EquipmentEditDialog.java` | 装备编辑对话框 |
| | `TeamDetailDialog.java` | 战队详情对话框 |
| | `TeamEditDialog.java` | 战队编辑对话框 |
| `ui/web/` | `VisualizationServer.java` | 嵌入式 HTTP 服务器，JDK HttpServer + ECharts |
| | `static/dashboard.html` | ECharts 仪表盘页面（5 个统计图表） |

#### 根目录辅助文件

| 文件 | 用途 |
|------|------|

| `pom.xml` | Maven 构建配置：Java 17 + shade 插件 + SQLite/SLF4J/FlatLaf |
| `CheckDB.java` | 数据库内容快速检查 |
| `GenFile.java` | 文件生成工具 |
| `TestViz.java` | 可视化服务器测试工具 |
| `WriteTestCases.java` | 测试用例生成 |
| `hok_data.db` | SQLite 数据库文件（运行生成） |

#### 文档目录

| 文件 | 用途 |
|------|------|

| `docs/plan.md` | 开发计划与追加实现记录 |
| `docs/test-cases.md` | 测试用例文档 |
| `ai/agent-log.md` | 三代理协作日志（本文档） |
| `ai/promots.md` | 13 轮 AI 提示词往返记录 |
| `ai/reflection.md` | 5 大架构反思与纠偏决策 |

### 4. 核心架构决策

#### **4.1 接口驱动设计**

- 所有数据操作通过 `Searchable` 接口定义契约
- `GameDataManager` 是唯一实现类，作为全系统唯一的数据仓库
- 好处：后续可替换为数据库实现而无需改动上层调用

#### **4.2 动态胜率计算（非存储代理）**

- `Player` 类上的 `winRate` / `totalMatches` 字段保留但不作为排序依据
- `RankingService.calculateWinRate()` 通过遍历 `Player.matchOverviews` 中的 `MatchRecord.result` 动态统计胜利次数
- `RankingService.calculateTotalMatches()` 取 `matchOverviews.size()`
- **决策理由**: 严禁用战队统计数据代理个人战绩，确保数据一致性和实时性

#### **4.3 不可变数据访问**

- 所有 `getXxx()` 返回 `Collections.unmodifiableList(new ArrayList<>(internalList))`
- 防止外部直接修改内部列表，强制通过 `addXxx()` / `removeXxx()` 方法操作

#### **4.4 角色权限控制 (RBAC)**

- 两级角色：`ADMIN` / `PLAYER`
- 管理员（admin）：全部功能，含增删改 + 导出 + 可视化
- 玩家（player）：仅浏览/查询功能
- 权限控制通过 `readIntInRange` 的 max 参数硬限制（控制台），或 GUI 侧边栏按钮动态启用/禁用

#### **4.5 中文编码处理**

- 全部源文件使用 UTF-8 编码（无 BOM）
- `FileStorageService` 明确使用 `StandardCharsets.UTF_8` 写入
- `RankingService.formatWithChinese()` 通过 Unicode 范围检测中文字符并按双字节宽度补齐
- Maven `pom.xml` 显式指定 `<encoding>UTF-8</encoding>`

#### **4.6 资源管理**

- `InputHelper` 持有单个 `Scanner(System.in)` 实例，全局复用（控制台模式）
- `FileStorageService.exportRankingToFile()` 使用 try-with-resources 包裹 `BufferedWriter`
- `GameDataManager` 在构造函数中一次性加载全部初始数据
- `DatabaseManager` 单例管理 SQLite 连接，`Runtime.getRuntime().addShutdownHook` 安全关闭

#### **4.7 持久化策略（v2.0 新增）**

- **数据层**: SQLite 数据库，5 张表（admins / heroes / equipments / teams / operation_logs）
- **DAO 模式**: `GameDataDao` / `AdminDao` / `OperationLogDao` 各自封装 SQL
- **初始化流程**: `GameDataManager.persistAllToDatabase()` 在首次运行时将内存数据写入数据库
- **日志体系**: `OperationLogService` 同时写入 SQLite + SLF4J/Logback 滚动文件

#### **4.8 双模式架构（v2.0 新增）**

- **控制台模式**: `Main.java` 作为独立入口，纯控制台交互，`javac` 编译即可运行
- **GUI 模式**: `ui.AppLauncher.java` 作为主入口，使用 FlatLaf 主题的 Swing 桌面应用
- **构建工具**: Maven 统一管理依赖和构建，`maven-shade-plugin` 打包为 fat JAR
- **两套界面共享同一业务逻辑层**（service 包）、同一数据访问层（db 包）、同一领域模型层（model 包）

#### **4.9 嵌入式 Web 可视化（v2.0 新增）**

- **服务器**: 基于 JDK 内置 `com.sun.net.httpserver.HttpServer`，零外部依赖
- **前端**: ECharts CDN 加载的静态 HTML 仪表盘
- **API 端点**: 8 个 JSON 接口覆盖英雄/装备/战队统计
- **模糊搜索**: Levenshtein 距离 + 加权评分算法
- **启动方式**: GUI 主框架侧边栏"数据可视化"按钮触发

#### **4.10 操作日志体系（v2.0 新增）**

- **双写入**: 每次操作同时写入 SQLite (`operation_logs` 表) 和 SLF4J 日志文件
- **日志分类**: 按操作类型（ADD/UPDATE/DELETE/QUERY）和目标类型（HERO/EQUIPMENT/TEAM）分类
- **查询能力**: 支持按管理员、按操作类型检索
- **便捷方法**: `logSuccess()` / `logFailure()` 封装常用日志模式

### 5. 数据流设计

#### 控制台模式数据流

```process
登录认证                   数据管理                    排行榜/导出
========                   ========                    ==========
loginLoop()         addHero/removeHero/       displayTopPlayers()
  └→ AuthService      updateHero                 └→ RankingService
      └→ login()         └→ GameDataManager           ├→ getPlayers()
          └→ users            ├→ addHero()            ├→ sort (Comparator)
                               ├→ removeHeroById()    ├→ calculateWinRate()
                               └→ updateHero()        └→ formatWithChinese()

玩家级联查询:
displayPlayerDetails(id)
  ├→ findPlainPlayerById(id)      → Player
  ├→ 遍历 teams.memberNames       → Team
  ├→ 遍历 team.mainHeroes         → Hero (× N)
  └→ 遍历 hero.equipmentIds       → Equipment (× M)
```

#### GUI 模式数据流

```structure
AppLauncher.main()
  ├→ DatabaseManager.initialize()        // 建表
  ├→ FlatLaf.setup()                     // 设置主题
  ├→ LoginDialog (模态)                   // 登录
  │   └→ AuthenticationService.login()
  ├→ MainFrame (BorderLayout)
  │   ├→ 侧边栏 (JPanel, BoxLayout)
  │   │   ├→ 欢迎按钮 → WelcomePanel
  │   │   ├→ 英雄管理 → HeroPanel (JTable + 搜索)
  │   │   ├→ 装备管理 → EquipmentPanel
  │   │   ├→ 战队管理 → TeamPanel
  │   │   ├→ 玩家查询 → PlayerQueryPanel
  │   │   ├→ 排行榜 → RankingPanel
  │   │   ├→ 数据管理 → DataManagementPanel (仅管理员)
  │   │   ├→ 导出排行榜 → FileStorageService.exportRankingToFile()
  │   │   └→ 数据可视化 → VisualizationServer.start()
  │   └→ 内容区 (CardLayout)
  │       └→ 切换各 panel
  └→ ShutdownHook → DatabaseManager.close()
```

#### Web 数据流

```process
浏览器 → localhost:{port}
  ├→ /  → dashboard.html (ECharts 仪表盘)
  ├→ /api/heroes/position-stats      → JSON (各位置英雄数量)
  ├→ /api/heroes/type-stats          → JSON (英雄类型分布)
  ├→ /api/heroes/ability-stats       → JSON (各位置能力雷达)
  ├→ /api/heroes/difficulty-stats    → JSON (难度分布)
  ├→ /api/equipments/price-by-type   → JSON (装备价格统计)
  ├→ /api/teams/win-rates            → JSON (战队胜率排行)
  ├→ /api/heroes/search?q=李白       → JSON (模糊搜索结果)
  └→ /api/data/summary               → JSON (数据总量概览)
```

### 6. 初始数据集对比（v1.0 → v2.0 扩展）

| 实体 | v1.0 初始 | v2.0 最终 | 增长 |
|------|-----------|-----------|------|

| 英雄 | 15 | 90 | 6× |
| 装备 | 22 | 88 | 4× |
| 战队 | 3 | 20 | ~7× |
| 玩家 | 15 | 70+（含 1200+ 对局记录） | ~5× |
| 对局记录 | ≥120 | ≥1200 | 10× |
| 账号 | 2 | 2（admin/player）+ 数据库持久化 | — |
| 数据库表 | 无 | 5 张 | 新增 |

### 7. 演进时间线（v1.0 控制台 → v2.0 双模式）

| 阶段 | 版本 | 关键变更 | 日期 |
|------|------|----------|------|

| 初始架构 | v1.0.0 | 纯 JDK 控制台，8 model + 5 service + 2 util | 2026-06-06 |
| 功能补全 | v1.1.0 | Searchable 接口，CRUD 闭环，RBAC | 2026-06-07 |
| Bug 修复 | v1.2.0 | 排行榜降序修复、中文对齐、编码修复 | 2026-06-07 |
| 测试 & 文档 | v1.3.0 | 11 个自动化测试用例，agent-log/README 文档 | 2026-06-07 |
| **Maven + SQLite** | **v2.0.0** | **Maven 构建 + SQLite 持久化 + DAO 层** | **2026-06-08** |
| **Swing GUI** | **v2.0.0** | **FlatLaf + LoginDialog + MainFrame + 7 面板 + 6 对话框** | **2026-06-08** |
| **Web 可视化** | **v2.0.0** | **JDK HttpServer + ECharts 仪表盘 + 模糊搜索** | **2026-06-08** |
| **操作日志** | **v2.0.0** | **SQLite 日志表 + SLF4J/Logback 滚动文件** | **2026-06-08** |
| **数据扩展** | **v2.0.0** | **90 英雄 / 88 装备 / 20 战队 / 70+ 玩家** | **2026-06-08** |

---

## Implementation Agent

### 1. 实现清单与完成状态

| 模块 | 文件 | 状态 | 说明 |
|------|------|------|------|

| 领域模型 | 8 个 model 类 | ✅ v1.0 ✅ v2.0 | 全字段 getter/setter + 全参构造器，v2.0 未修改 |
| 认证服务 | `AuthenticationService.java` | ✅ v1.0 ✅ v2.0 | v1.0 内存认证；v2.0 增加 SQLite 管理员持久化 |
| 数据仓库 | `GameDataManager.java` | ✅ v1.0 ✅ v2.0 | v1.0 纯内存；v2.0 增加 `persistAllToDatabase()` 同步 |
| 排行榜 | `RankingService.java` | ✅ v1.0 ✅ v2.0 | v2.0 未修改，控制台 + GUI 复用 |
| 文件导出 | `FileStorageService.java` | ✅ v1.0 ✅ v2.0 | v2.0 未修改 |
| 数据初始化 | `DataInitializer.java` | ✅ v1.0 ✅ v2.0 | v1.0 15 英雄/22 装备/3 战队/15 玩家；v2.0 扩展到 90/88/20/70+ |
| 输入工具 | `InputHelper.java` | ✅ v1.0 | 控制台专用 |
| 入口 | `Main.java` | ✅ v1.0 | 控制台入口，v2.0 仍可用 |
| 接口 | `Searchable.java` | ✅ v1.0 ✅ v2.0 | 20 个方法，v2.0 未修改 |
| **数据库管理** | **`DatabaseManager.java`** | **✅ v2.0 新增** | **SQLite 单例 + 5 表自动建表** |
| **英雄/装备/战队 DAO** | **`GameDataDao.java`** | **✅ v2.0 新增** | **全字段 CRUD + ResultSet 映射** |
| **管理员 DAO** | **`AdminDao.java`** | **✅ v2.0 新增** | **认证 + 密码管理** |
| **操作日志实体** | **`OperationLog.java`** | **✅ v2.0 新增** | **10 字段日志实体** |
| **操作日志 DAO** | **`OperationLogDao.java`** | **✅ v2.0 新增** | **多维查询 + 统计** |
| **操作日志服务** | **`OperationLogService.java`** | **✅ v2.0 新增** | **双写入（SQLite + SLF4J）** |
| **GUI 入口** | **`ui/AppLauncher.java`** | **✅ v2.0 新增** | **FlatLaf 初始化 + 启动流程编排** |
| **登录对话框** | **`ui/LoginDialog.java`** | **✅ v2.0 新增** | **GridBagLayout + 默认填充** |
| **主框架** | **`ui/MainFrame.java`** | **✅ v2.0 新增** | **BorderLayout + CardLayout 侧边栏** |
| **欢迎面板** | **`ui/panels/WelcomePanel.java`** | **✅ v2.0 新增** | **系统概览 + 统计数据** |
| **英雄面板** | **`ui/panels/HeroPanel.java`** | **✅ v2.0 新增** | **JTable + DocumentListener 搜索过滤** |
| **装备面板** | **`ui/panels/EquipmentPanel.java`** | **✅ v2.0 新增** | **JTable + 类型/价格显示** |
| **战队面板** | **`ui/panels/TeamPanel.java`** | **✅ v2.0 新增** | **JTable + 成员和胜率** |
| **玩家查询面板** | **`ui/panels/PlayerQueryPanel.java`** | **✅ v2.0 新增** | **四级级联查询（玩家→战队→英雄→装备）** |
| **排行榜面板** | **`ui/panels/RankingPanel.java`** | **✅ v2.0 新增** | **JTable 动态排序显示** |
| **数据管理面板** | **`ui/panels/DataManagementPanel.java`** | **✅ v2.0 新增** | **CRUD 表单 + 操作日志记录** |
| **英雄详情/编辑** | **`ui/dialogs/HeroDetailDialog.java`** | **✅ v2.0 新增** | **英雄全属性展示 + 编辑表单** |
| **装备详情/编辑** | **`ui/dialogs/EquipmentDetailDialog.java`** | **✅ v2.0 新增** | **装备全属性展示 + 编辑表单** |
| **战队详情/编辑** | **`ui/dialogs/TeamDetailDialog.java`** | **✅ v2.0 新增** | **战队全属性展示 + 编辑表单** |
| **Web 服务器** | **`VisualizationServer.java`** | **✅ v2.0 新增** | **JDK HttpServer + ECharts 仪表盘** |
| **ECharts 仪表盘** | **`static/dashboard.html`** | **✅ v2.0 新增** | **5 个统计图表 + 模糊搜索** |

### 2. 关键实现细节

#### **2.1 排行榜排序（RankingService.java）**

```java
players.sort(Comparator
    .comparingDouble(RankingService::calculateWinRate)
    .thenComparingInt(RankingService::calculateTotalMatches)
    .reversed());
```

- 优先按胜率降序；胜率相同按总场次降序
- 此前 Bug：分别 `.reversed()` 导致第二条规则变为升序（已修复）

#### **2.2 中文字符对齐（RankingService.formatWithChinese）**

循环遍历字符，检测 Unicode 范围 `\u4e00-\u9fa5`，中文字符按双宽度计算后补齐空格。
应用于表头和数据行的所有字符串列，数值列仍使用 `%7.1f%%` / `%6d` 格式右对齐。

#### **2.3 文件导出（FileStorageService.java）**

```java
try (BufferedWriter writer = new BufferedWriter(
    new OutputStreamWriter(
        new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
    // 写入数据...
} catch (IOException e) {
    System.out.println("文件写入失败: " + e.getMessage());
}
```

- 严格使用 try-with-resources，确保文件流在任何路径下安全关闭
- 表头和数据用制表符 `\t` 分隔

#### **2.4 玩家级联查询（GameDataManager.displayPlayerDetails）**

输入玩家 ID 或昵称，从 teams 的 memberNames 反查战队，遍历 mainHeroes 查 Hero，再遍历 equipmentIds 查 Equipment，输出四级级联信息。

#### **2.5 角色感知菜单（Main.java）**

```java
boolean isAdmin = "ADMIN".equals(currentUser.getRole());
int maxChoice = isAdmin ? 10 : 8;
int choice = inputHelper.readIntInRange("请选择功能：", 0, maxChoice);
```

- 管理员菜单：1-8（公共）+ 9（数据管理）+ 10（导出）
- 玩家菜单：仅 1-8

#### **2.6 SQLite 数据库层（v2.0 新增）**

- **DatabaseManager**: 单例模式，`initialize()` 自动建 5 张表
- **GameDataDao**: 全字段 CRUD，ResultSet → 实体映射
- **推荐装备存储**: recommended_equipment_ids 字段用逗号分隔存储

#### **2.7 FlatLaf 主题与 Swing UI（v2.0 新增）**

```java
try {
    Class.forName("com.formdev.flatlaf.FlatLightLaf")
            .getMethod("setup").invoke(null);
} catch (Exception e) {
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
}
```

- **布局**: MainFrame 使用 BorderLayout，左侧 180px 侧边栏 + 中间 CardLayout 内容区
- **侧边栏导航**: 9 个按钮（含管理员专有），`switchPanel()` 切换各面板
- **英雄面板**: JTable + `TableRowSorter` + `DocumentListener` 实时搜索过滤
- **权限控制**: `dataMgmtBtn.setVisible(isAdmin)` 动态隐藏管理按钮

#### **2.8 嵌入式 Web 可视化（v2.0 新增）**

- **HttpServer**: JDK 内置 `com.sun.net.httpserver.HttpServer`，端口自动分配
- **ECharts 仪表盘**: CDN 加载 ECharts 5.x，5 个图表 + 模糊搜索输入框
- **API 并发**: `server.setExecutor(Executors.newFixedThreadPool(2))`
- **模糊搜索**: Levenshtein 距离 + 加权评分（名称权重 ×1.2、称号权重 ×1.1），阈值 0.4
- **API 端点**: 8 个，覆盖英雄位置/类型/难度/能力/装备价格/战队胜率/模糊搜索/数据摘要

#### **2.9 操作日志系统（v2.0 新增）**

- 每次操作同时写入 SQLite (`operation_logs` 表) 和 SLF4J 日志文件
- 按操作类型（ADD/UPDATE/DELETE/QUERY）和目标类型（HERO/EQUIPMENT/TEAM）分类
- `logSuccess()` / `logFailure()` 封装常用日志模式

#### **2.10 数据初始化扩展（v2.0 新增）**

- 英雄扩展到 90 名（H001-H090），覆盖全定位/全类型
- 装备扩展到 88 件（含攻击/法术/防御/移动/打野/辅助 6 大类）
- 战队扩展到 20 支（T001-T020，涵盖全部 KPL 现役战队）
- 玩家扩展到 70+ 名（每战队 4-5 名成员），每人 8-24 场对局记录

### 3. 修复历史

| 日期 | 版本 | 问题 | 修复 |
|------|------|------|------|

| 2026-06-07 | v1.2.0 | 排行榜排序方向错误（双重 .reversed() 导致第二条升序） | 改为统一 `.reversed()` |
| 2026-06-07 | v1.2.0 | 中英文混排不对齐 | 新增 `formatWithChinese()` |
| 2026-06-07 | v1.2.0 | RankingService.java 文件编码损坏（中文乱码） | git 恢复 + 无 BOM 重写 |
| 2026-06-07 | v1.2.0 | 编译命令缺少 `-encoding UTF-8` | CI/CD 脚本补充编码参数 |
| 2026-06-08 | v2.0.0 | 模型层数据强耦合（model 含初始化逻辑） | 剥离到 DataInitializer |
| 2026-06-08 | v2.0.0 | 直接返回集合引用致表示泄露 | Collections.unmodifiableList + 浅拷贝 |
| 2026-06-08 | v2.0.0 | 战队数据代理个人战绩致排行榜失真 | 动态遍历 MatchRecord |
| 2026-06-08 | v2.0.0 | 控制台键盘脏输入崩溃 | InputHelper 全面异常兜底 |
| 2026-06-08 | v2.0.0 | 中英文混排错位（printf 固定宽度失效） | formatWithChinese() 动态宽度计算 |

---

## Testing Agent

### 1. 测试覆盖总览

| 条目 | 数值 |
|------|------|

| 测试用例总数 | 11（v1.0 黑盒）+ 新增 GUI/SQLite/Web 用例 |
| 覆盖的功能模块 | 认证、菜单健壮性、英雄查询、玩家级联查询、排行榜、文件导出、权限控制、CRUD、退出循环、SQLite 持久化、GUI 面板、Web 可视化 |
| 测试方式 | PowerShell Pipeline 黑盒自动化注入（v1.0 控制台）+ 手动 GUI 验证（v2.0） |
| 测试文件 | `docs/test-cases.md` |

### 2. 测试用例矩阵

| 编号 | 测试项 | 验证目标 |
|------|--------|----------|

| TC-01 | 错误凭证拦截 | 拒绝错误用户名/密码，打印准确消息后回到登录 |
| TC-02 | 非法数字兜底 | 输入 `abc` 时 `InputHelper` 不崩溃 |
| TC-03 | 编号查英雄 | `H001` → 李白，通过英文 ID 绕过中文编码问题 |
| TC-04 | 玩家级联查询 | `花海` → eStarPro → 英雄 → 装备 |
| TC-05 | 排行榜降序 | TOP 5 按胜率→场次双重降序，花海居首 |
| TC-06 | 排行榜边界 | `topN=0` 安全跳过 / `topN=100` 只显示 15 条 |
| TC-07 | 导出排行榜 | 生成 UTF-8 文件，try-with-resources 安全关闭 |
| TC-08 | admin 全权限 | 菜单 0-10，可见增删改和导出入口 |
| TC-09 | player 受限 | 菜单 0-8，输入 9 被 `readIntInRange` 拦截 |
| TC-10 | CRUD 闭环 | 添加→更新→删除英雄，全程反馈 |
| TC-11 | 退出登录循环 | 退出后回到登录界面，可重新登录 |
| TC-12 (GUI) | **SQLite 数据持久化** | 添加数据后重启系统，数据仍然存在 |
| TC-13 (GUI) | **GUI 登录对话框** | 正确/错误密码的响应，FlatLaf 正常加载 |
| TC-14 (GUI) | **GUI 面板切换** | 侧边栏各按钮正确切换对应 panel |
| TC-15 (GUI) | **GUI 权限控制** | 管理员可见数据管理按钮，玩家不可见 |
| TC-16 (GUI) | **GUI CRUD 操作** | 英雄/装备/战队的增删改操作在 GUI 中可用 |
| TC-17 (GUI) | **Web 可视化启动** | VisualizationServer 启动并返回正确 JSON |
| TC-18 (GUI) | **Web 模糊搜索** | `?q=李白` 返回包含李白的 JSON 结果 |
| TC-19 (GUI) | **操作日志记录** | CRUD 操作后 operation_logs 表有对应记录 |

### 3. 一键测试脚本（控制台模式）

```powershell
javac -d out -encoding UTF-8 src/Main.java src/model/*.java src/util/*.java src/service/*.java

$inputData = @"
wrong        # TC-01: 错误凭证
bad
admin        # TC-01: 正确登录
123456
abc          # TC-02: 非法菜单输入
4            # TC-03: 查英雄
H001
7            # TC-04: 查玩家
花海
8            # TC-05: 排行榜 TOP 5
5
8            # TC-06: 排行榜边界
0
10           # TC-07: 导出
ranking.txt
9            # TC-10: CRUD 闭环
1
H999
测试英雄
测试称号
打野
刺客
0
9
4
H999
0            # TC-11: 退出
player       # TC-09: 玩家登录
123456
9            # 尝试越权
0
0
"@

$inputData | java -cp out Main
```

### 4. Maven 构建与 GUI 测试

```powershell
# Maven 编译 + 打包 fat JAR
mvn clean package -DskipTests

# 运行 GUI 模式（推荐）
java -jar target/hok-info-management-2.0.0.jar

# 运行控制台模式（可选）
# java -cp "target/classes;target/dependency/*" Main
```

### 5. 对实际代码的消息验证结果

| 场景 | 文档消息（旧） | 实际代码消息（已验证） | 状态 |
|------|----------------|------------------------|------|

| 登录失败 | "登录失败：用户名或密码错误！" | "用户名或密码错误，或账号状态不可用，请重试。" | ✅ |
| 非法输入 | "输入非法，请输入合法的菜单数字！" | "请输入有效的整数。" | ✅ |
| H001 归属 | 描述为"玩家" | H001 是英雄编号，非玩家 | ✅ |
| 玩家数量 | 10 名 | 15 名（v1.0）/ 70+ 名（v2.0） | ✅ |
| 胜率数据 | 65.5%/120 场（不存在） | 花海 80%/15 场（实际） | ✅ |

### 6. 未覆盖项（已知局限）

- player 预设账号不在任何战队列表中
- 装备 11 项数值属性在控制台输出中未全部展示
- `MatchParticipant` 的 KDA 数据未在任何查询路径中完整展示
- 英雄技能名称在级联查询中仅展示部分
- 无 JUnit 单元测试，仅黑盒集成测试和手动 GUI 验证
- GUI 自动化测试未覆盖（Selenium/Playwright 未集成）
- SQLite 事务未使用显式事务控制
- Web 可视化服务器未做 HTTPS 或认证

---

### 7. 项目文件体积统计

| 类别 | 文件数 | 总代码行（约） |
|------|--------|----------------|

| Java 源文件 | 40+ | 8,500+ 行 |
| 配置文件 | 2（pom.xml + logback.xml） | ~120 行 |
| HTML/JS | 1（dashboard.html） | ~600 行 |
| 文档 | 5（md 文件） | ~2,500 行 |
| **总计** | **48+** | **~11,700 行** |

---

*本文档最后更新于 2026-06-09，对应项目版本 v2.0.0。*
