# AI-Assisted Honor of Kings Information Management System

> **语言**: Java 17+ | **构建**: Maven | **持久化**: SQLite
> **界面**: 控制台 + Swing GUI（FlatLaf）+ Web 可视化（ECharts）
> **编码**: UTF-8 | **日志**: SLF4J + Logback

---

## 1. Project Overview

一个基于 Java 的《王者荣耀》信息管理系统，支持双模式运行：传统控制台交互 + 现代化桌面 GUI 界面。核心功能涵盖英雄/装备/战队数据的增删改查、玩家战绩的级联查询、荣誉排行榜的动态计算、排行榜文件的 UTF-8 导出、SQLite 数据持久化、操作日志审计以及嵌入式 Web 可视化仪表盘。系统实现了角色权限控制（RBAC）——管理员具备全部操作权限，普通玩家仅可浏览和查询。

### 版本演进

| 版本 | 日期 | 关键特性 |
|------|------|----------|

| v1.0.0 | 2026-06-06 | 纯 JDK 控制台应用，15 英雄 / 22 装备 / 3 战队 / 15 玩家 |
| v1.1.0 | 2026-06-07 | Searchable 接口，CRUD 闭环，RBAC |
| v1.2.0 | 2026-06-07 | 排行榜修复、中文对齐、编码修复 |
| v1.3.0 | 2026-06-07 | 11 个自动化测试用例 + 完整文档 |
| **v2.0.0** | **2026-06-08** | **Maven + SQLite + Swing GUI + Web 可视化 + 操作日志 + 数据扩展** |

### 项目结构

```structure
src/
├── Main.java                              # 控制台入口（v1.0 保留）
├── model/                                 # 领域模型层（8 个实体类）
│   ├── Person.java (abstract base)
│   ├── Admin.java / Player.java
│   ├── Hero.java / Equipment.java
│   ├── Team.java / MatchRecord.java / MatchParticipant.java
├── service/                                # 业务逻辑层（5 个类 + 1 个接口）
│   ├── Searchable.java (interface)
│   ├── GameDataManager.java               # 数据仓库 + 级联查询
│   ├── AuthenticationService.java          # 用户认证
│   ├── RankingService.java                 # 排行榜排序
│   ├── FileStorageService.java             # 文件导出
│   └── OperationLogService.java            # 操作日志（SQLite + SLF4J 双写）
├── db/                                     # 数据访问层
│   ├── DatabaseManager.java                # SQLite 连接管理（单例）
│   ├── GameDataDao.java                    # 英雄/装备/战队 DAO
│   ├── AdminDao.java                       # 管理员 DAO
│   ├── OperationLog.java / OperationLogDao.java  # 操作日志 DAO
├── util/                                   # 工具层
│   ├── DataInitializer.java                # 初始化 90 英雄/88 装备/20 战队/70+ 玩家
│   ├── InputHelper.java                    # Scanner 封装 + 防崩溃
│   └── DbQuickCheck.java                   # 数据库快速检查
├── ui/                                     # GUI 层（Swing + FlatLaf）
│   ├── AppLauncher.java                    # GUI 入口
│   ├── LoginDialog.java                    # 登录对话框
│   ├── MainFrame.java                      # 主窗口（BorderLayout + CardLayout 侧边栏）
│   ├── panels/                             # 8 个面板
│   │   ├── WelcomePanel.java               # 欢迎 + 数据总览
│   │   ├── HeroPanel.java                  # 英雄列表（JTable + 搜索）
│   │   ├── EquipmentPanel.java             # 装备列表
│   │   ├── TeamPanel.java                  # 战队列表
│   │   ├── PlayerQueryPanel.java           # 玩家级联查询
│   │   ├── RankingPanel.java               # 排行榜
│   │   ├── MatchHistoryPanel.java          # 比赛历史查询
│   │   └── DataManagementPanel.java        # 数据管理（CRUD）
│   ├── dialogs/                            # 7 个编辑/详情对话框
│   │   ├── HeroDetailDialog / HeroEditDialog
│   │   ├── EquipmentDetailDialog / EquipmentEditDialog
│   │   ├── TeamDetailDialog / TeamEditDialog
│   │   └── MatchDetailDialog
│   └── web/                                # Web 可视化层
│       ├── VisualizationServer.java        # JDK HttpServer + ECharts API
│       └── static/dashboard.html           # ECharts 仪表盘页面

docs/
├── plan.md                                 # 开发计划（含追加实现记录）
└── test-cases.md                           # 19 个测试用例文档

ai/
├── agent-log.md                            # 三代理协作日志
├── promots.md                              # 13 轮 AI 提示词往返
└── reflection.md                           # 5 大架构反思

pom.xml                                     # Maven 构建文件
```

### 初始数据集

| 实体 | 数量 | 示例 |
|------|------|------|

| 英雄 | 90 | 李白(H001)、韩信(H002)、貂蝉(H005)、武则天(H031)、敖隐(H089) |
| 装备 | 88 | 无尽战刃(E001)、暗影战斧(E005)、博学者之怒(E031)、极影(E079) |
| 战队 | 20 | 重庆狼队(T001)、成都AG超玩会(T002)、武汉eStarPro(T003)、广州TTG(T015) |
| 玩家 | 70+ | 花海(胜率80%)、一诺(78.6%)、Fly(75%)、九尾(65%) 等 75 名 |
| 对局记录 | 1200+ | 每玩家 8-24 场，动态生成胜负比例 |
| 默认账号 | 2 | admin (管理员) / player (普通玩家) |
| SQLite 表 | 5 | admins / heroes / equipments / teams / operation_logs |

---

## 2. How to Run

### 前置条件

- JDK 17 或更高版本
- Maven 3.6+（GUI 模式需要）
- 系统终端支持 UTF-8 编码

### 方式一：GUI 模式（推荐）

```bash
# 1. Maven 编译并打包（含全部依赖）
mvn clean package -DskipTests

# 2. 运行（自动启动 Swing GUI + FlatLaf 主题）
java -jar target/hok-info-management-2.0.0.jar

# 登录后点击侧边栏"数据可视化"可启动 Web 仪表盘
```

### 方式二：控制台模式（经典）

```bash
# 1. 直接编译（无需 Maven）
javac -d out -encoding UTF-8 src/Main.java src/model/*.java src/util/*.java src/service/*.java

# 2. 运行
java -cp out Main
```

---

## 3. Default Login Accounts

| 角色 | 用户名 | 密码 | 权限 |
|------|--------|------|------|

| **管理员** | `admin` | `123456` | 全部功能（含数据管理/导出/可视化） |
| **普通玩家** | `player` | `123456` | 仅浏览/查询 |

---

## 4. Implemented Features

### 控制台模式功能（Main.java）

| 编号 | 功能 | 说明 |
|------|------|------|

| 1 | 查看英雄列表 | 展示全部 90 位英雄的基本信息 |
| 2 | 查看装备列表 | 展示全部 88 件装备的名称/类型/价格 |
| 3 | 查看战队列表 | 展示 20 支战队详细数据 |
| 4 | 查询英雄 | 支持按名称（中文）或编号（如 H001）检索 |
| 5 | 查询装备 | 支持按名称或编号（如 E001）检索 |
| 6 | 查询战队 | 支持按名称或编号（如 T001）检索 |
| 7 | **玩家详情查询** | **级联查询**: 玩家 → 所属战队 → 常用英雄 → 推荐装备 |
| 8 | **玩家荣誉排行榜** | 按胜率→总场次双重降序，中文对齐排版 |
| 9 | 数据管理（增删改） | 子菜单：英雄/装备/战队的添加、删除、更新 |
| 10 | 导出排行榜 | 将排序结果写入 UTF-8 文件（制表符分隔） |
| 0 | 退出登录 | 返回登录界面 |

### GUI 模式功能（AppLauncher）

| 侧边栏按钮 | 面板 | 功能描述 |
|------------|------|----------|

| 欢迎 | WelcomePanel | 系统概览：英雄数/装备数/战队数/玩家数 |
| 英雄管理 | HeroPanel | JTable 显示英雄列表，支持搜索过滤，双击查看详情 |
| 装备管理 | EquipmentPanel | JTable 显示装备列表，含类型/价格/属性 |
| 战队管理 | TeamPanel | JTable 显示战队列表，含胜率/队长/荣誉 |
| 玩家查询 | PlayerQueryPanel | 输入玩家名，四级级联展示：玩家→战队→英雄→装备 |
| 排行榜 | RankingPanel | JTable 显示动态胜率排序 |
| 比赛历史 | MatchHistoryPanel | 按玩家/战队查询比赛记录，展示胜负分布 |
| 数据管理 *(admin only)* | DataManagementPanel | 英雄/装备/战队的表单 CRUD 操作 |
| 导出排行榜 *(admin only)* | — | 将排行榜导出为 tab 分隔的 UTF-8 文件 |
| 数据可视化 *(admin only)* | VisualizationServer | 启动嵌入式 HttpServer + ECharts 仪表盘 |

### Web 可视化仪表盘

启动后浏览器打开 `http://localhost:{port}`，展示 6 个 ECharts 图表：

| 图表 | 数据类型 | API 端点 |
|------|----------|----------|

| 英雄位置分布 | 柱状图 - 各位置(打野/中路/发育路/对抗路/游走) | `/api/heroes/position-stats` |
| 英雄类型分布 | 饼图 - 各类型(刺客/法师/射手/战士/坦克/辅助) | `/api/heroes/type-stats` |
| 各位置能力雷达 | 雷达图 - 生存/攻击/技能/支援四维 | `/api/heroes/ability-stats` |
| 英雄难度分布 | 饼图 - 简单/中等/困难 | `/api/heroes/difficulty-stats` |
| 装备价格统计 | 箱线图 - 6 类装备的平均价/最高价/最低价 | `/api/equipments/price-by-type` |
| 战队胜率排行 | 柱状图 - 20 支战队胜率 | `/api/teams/win-rates` |
| 英雄模糊搜索 | 输入框搜索 + Levenshtein 模糊匹配 | `/api/heroes/search?q=` |
| 数据总览 | 摘要卡片 | `/api/data/summary` |

### 核心特性亮点

- **双模式架构**: 控制台 + Swing GUI + Web 可视化，共享同一业务逻辑层
- **动态胜率计算**: 不存储死数据，通过遍历 `MatchRecord` 列表实时统计
- **双重降序排序**: `Comparator.comparingDouble(winRate).thenComparingInt(totalMatches).reversed()`
- **中英文对齐**: `formatWithChinese()` 检测 Unicode 中文字符并按双字节宽度补齐空格
- **级联查询**: 玩家 → 战队 → 英雄 → 装备，一次查询贯穿四级实体
- **SQLite 持久化**: 5 张表，DAO 模式，`persistAllToDatabase()` 同步内存与数据库
- **操作日志审计**: 每次 CRUD 操作同时写入 SQLite 和 SLF4J 日志文件
- **输入防崩溃**: `InputHelper` 对 `NumberFormatException` 进行全面兜底
- **UTF-8 导出**: `StandardCharsets.UTF_8` + `try-with-resources` 确保文件流安全
- **嵌入式 Web 仪表盘**: JDK 内置 HttpServer + ECharts CDN，零外部依赖
- **模糊搜索**: Levenshtein 距离 + 加权评分（名称×1.2，称号×1.1）
- **FlatLaf 主题**: 现代化 Swing 外观，替代默认 Metal 主题

---

## 5. Java Concepts Used

| 概念 | 应用位置 | 说明 |
|------|----------|------|

| **继承 (Inheritance)** | `Person` → `Admin`, `Player` | 人员抽象基类与两个子类 |
| **封装 (Encapsulation)** | 所有 model 类 | 私有字段 + 公共 getter/setter |
| **接口 (Interface)** | `Searchable.java` | 20 个方法定义全部数据操作契约 |
| **多态 (Polymorphism)** | `AuthenticationService.login()` | 返回 `Person` 类型，实际为 `Admin` 或 `Player` |
| **泛型 (Generics)** | `List<Hero>`, `List<Player>` | 类型安全的集合操作 |
| **集合框架** | `ArrayList`, `Collections.unmodifiableList` | 数据存储 + 不可变视图保护 |
| **Comparator** | `RankingService.java` | 链式比较器实现双重排序 |
| **方法引用** | `RankingService::calculateWinRate` | Lambda 的简化写法 |
| **异常处理** | `InputHelper.readInt()`, `FileStorageService` | `NumberFormatException` 兜底 + `try-with-resources` |
| **文件 I/O** | `FileStorageService.java` | `BufferedWriter` + `OutputStreamWriter` |
| **字符编码** | `StandardCharsets.UTF_8` | 显式指定 UTF-8 编码读写 |
| **日期时间 API** | `LocalDateTime` | 管理员最后登录时间、对局时间 |
| **字符串处理** | `formatWithChinese()`, `String.repeat()` | Unicode 宽度计算 + 空格补齐 |
| **控制流** | `Main.mainMenuLoop()` | 双层 while 循环 + switch 路由 |
| **单例模式 (Singleton)** | `DatabaseManager`, `OperationLogService` | 数据库连接和日志服务的唯一实例 |
| **DAO 模式** | `GameDataDao`, `AdminDao`, `OperationLogDao` | 数据访问对象封装 SQL 操作 |
| **JDBC** | `DatabaseManager` + DAO 类 | SQLite 数据库连接和 CRUD |
| **Swing** | `ui/` 包 | `JFrame`, `JDialog`, `JTable`, `CardLayout`, `JPanel` |
| **FlatLaf** | `AppLauncher` | 跨平台扁平化 Look and Feel |
| **HTTP 服务器** | `VisualizationServer` | `com.sun.net.httpserver.HttpServer` 嵌入式 Web 服务 |
| **JSON 生成** | `VisualizationServer` | 手动字符串拼接构建 JSON 响应 |
| **SLF4J + Logback** | `OperationLogService` | 结构化日志 + 滚动文件输出 |
| **Maven** | `pom.xml` | 依赖管理 + shade 插件打包 |

---

## 6. AI Usage Summary

本项目由 Codex (GPT-5) 辅助开发，经历了从 v1.0 控制台到 v2.0 双模式的完整演进。AI 参与以下阶段：

| 阶段 | AI 贡献 |
|------|---------|

| **架构设计** | 确定分层结构、接口驱动设计、RBAC 权限模型、动态胜率计算方案 |
| **数据初始化 (v1.0)** | 创建 15 英雄 / 22 装备 / 3 战队 / 15 玩家 |
| **数据扩展 (v2.0)** | 扩展到 90 英雄 / 88 装备 / 20 战队 / 70+ 玩家 |
| **核心实现** | 级联查询、动态排序、try-with-resources 导出 |
| **SQLite 持久化 (v2.0)** | DatabaseManager + GameDataDao + AdminDao + OperationLogDao |
| **Swing GUI (v2.0)** | FlatLaf 主题 + LoginDialog + MainFrame + 7 面板 + 6 对话框 |
| **Web 可视化 (v2.0)** | JDK HttpServer + 8 个 JSON API + ECharts 仪表盘 + 模糊搜索 |
| **操作日志 (v2.0)** | OperationLogService 双写入（SQLite + SLF4J） |
| **Bug 修复** | Comparator 降序修复、中文对齐、编码损坏恢复 |
| **测试编写** | 19 个测试用例 + 一键 CI/CD 脚本 |
| **文档生成** | agent-log.md / README.md / test-cases.md 多轮修正 |

### 开发流程

```process
需求分析 → 架构设计 → model → service → util → 控制台 Main
                                                       ↓
          v2.0 GUI + Web 可视化 + SQLite ← 审查修正 ← 扩展与优化
                                                       ↓
                      test-cases.md ← 功能验证 ← agent-log.md + README.md
```

---

## 7. Testing Summary

### 测试方法

- **控制台模式**: PowerShell Pipeline 黑盒自动化注入
- **GUI 模式**: 手动验证（FlatLaf 渲染、面板切换、CRUD 操作）
- **Web 模式**: 浏览器访问 + JSON API 验证

### 19 个测试用例

| 编号 | 测试项 | 验证目标 | 模式 |
|:----:|--------|----------|:----:|

| TC-01 | 错误凭证拦截 | 拒绝错误用户名/密码 | 控制台+GUI |
| TC-02 | 非法数字兜底 | 输入 `abc` 不崩溃 | 控制台 |
| TC-03 | 编号查英雄 | `H001` → 李白 | 控制台+GUI |
| TC-04 | 玩家级联查询 | `花海` → eStarPro → 英雄 → 装备 | 控制台+GUI |
| TC-05 | 排行榜降序 | 花海居首，中文对齐 | 控制台+GUI |
| TC-06 | 排行榜边界 | `topN=0` / `topN=100` | 控制台 |
| TC-07 | 导出排行榜 | UTF-8 文件 + try-with-resources | 控制台+GUI |
| TC-08 | admin 全权限 | 可见数据管理和导出 | 控制台+GUI |
| TC-09 | player 受限 | 输入 9 被拦截 | 控制台+GUI |
| TC-10 | CRUD 闭环 | 添加→删除英雄 | 控制台+GUI |
| TC-11 | 退出登录 | 回到登录界面 | 控制台+GUI |
| TC-12 | SQLite 持久化 | 重启后数据保留 | GUI |
| TC-13 | GUI 登录对话框 | 正确/错误密码响应 | GUI |
| TC-14 | GUI 面板切换 | 侧边栏切换 panel | GUI |
| TC-15 | GUI 权限控制 | admin/player 按钮差异 | GUI |
| TC-16 | GUI CRUD 操作 | 增删改后 JTable 刷新 | GUI |
| TC-17 | Web 可视化 | ECharts 仪表盘加载 | GUI |
| TC-18 | Web 模糊搜索 | Levenshtein 匹配 | GUI |
| TC-19 | 操作日志 | SQLite + SLF4J 双写 | GUI |

---

## 8. Known Limitations

| 局限 | 说明 |
|------|------|

| 无 JUnit 单元测试 | 仅黑盒集成测试 + 手动 GUI 验证 |
| GUI 无自动化测试 | Selenium/Playwright 未集成 |
| KDA 数据未展示 | `MatchParticipant` 的击杀/死亡/助攻未在查询路径中呈现 |
| 技能展示不完整 | 英雄技能在级联查询中仅展示部分 |
| player 预设账号 | 不在任何战队成员列表中 |
| SQLite 无显式事务 | 批量操作未使用事务控制 |
| Web 无安全认证 | 可视化服务器未做 HTTPS 或认证 |
| Dashboard HTML 修复 | v2.0.0 初始版本存在 HTML 文档重复拼接与 initDashboard 嵌套 bug（已修复于 2026-06-11） |
| 模糊搜索性能 | Levenshtein 距离 O(n²) 在 90 英雄场景可接受，更大数据集需优化 |
| GUI 多语言 | 仅中文界面，无国际化支持 |
| 单线程 | GUI 事件线程 + Web 服务器双线程池，无复杂并发 |

---

## 9. Tech Stack

| 技术 | 用途 | 版本 |
|------|------|------|

| Java | 编程语言 | 17+ |
| Maven | 构建工具 | 3.6+ |
| SQLite (xerial JDBC) | 嵌入式数据库 | 3.49.1.0 |
| FlatLaf | Swing 外观主题 | 3.5.4 |
| SLF4J | 日志门面 | 2.0.16 |
| Logback | 日志实现 | 1.5.16 |
| ECharts | Web 数据可视化 | 5.x (CDN) |
| JDK HttpServer | 嵌入式 HTTP 服务器 | JDK 内置 |

---

## 10. PS

- 该项目内容源自于学校Java课程期末大作业，在硬性要求的基础上有所拓展，但在有限时间内还未完善，当前只作为作业提交以供检查。
- 后续空闲时间会继续追加功能，优化项目内容，争取使之成为一个工业级项目软件。
- 欢迎提出指导意见，我将虚心学习。

*项目由 Codex (GPT-5) 辅助开发，从纯 JDK 控制台应用演进为 Maven + SQLite + Swing GUI + Web 可视化的完整信息管理系统。* **v2.0.0**
