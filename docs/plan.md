# 1. 项目目标

## 本系统是一个基于 Java 面向对象编程（OOP）技术开发的《王者荣耀》信息管理系统（IMS）。系统主要服务于两类用户

- 普通玩家 (Player)：用于查询个人数据、查看英雄与战队排行榜、浏览比赛历史等。  
- 系统管理员 (Admin)：拥有最高权限，负责对玩家、战队、英雄、装备及比赛记录进行维护（增删改查）。

## 2. 需求分析

### 系统将采用控制台菜单驱动界面（Console-driven Menu）进行交互，确保核心业务逻辑稳定后再扩展加分项。核心功能实现规划如下

- F-1 玩家查询 (Player Lookup)：输入玩家 ID 或名字，从数据中心检索并打印该玩家基础信息、所属战队、胜率、拥有的英雄列表以及各英雄当前装配的装备。  
- F-2 战队总览 (Team Overview)：输入战队 ID 或名，动态计算并展示战队成员、平均等级、总对局数、全队综合胜率以及战队内的最高胜率玩家（Top Player）。  
- F-3 英雄详情 (Hero Details)：按名称搜索英雄，展示其属性、兼容的装备池，以及当前系统里拥有该英雄的玩家列表。  
- F-4 装备统计 (Equipment Statistics)：设计排序逻辑，通过 Collections.sort() 或存储在 TreeMap 中，按“使用次数（Usage Count）”对 20 件基础装备进行全网降序排列。  
- F-5 比赛历史 (Match History)：支持获取指定玩家或战队最近的 $N$ 场比赛记录，输出胜负分布和英雄出场率（Pick Rate）。  
- F-6 排行榜 (Leaderboard)：展示系统前 X 名的高胜率玩家。若胜率相同（Ties），则依次对比“总对局数（降序）”和“玩家等级（降序）”来打破平局。  
- F-7 数据管理 (Data Management)：设计严格的权限分流：Admin 菜单展示完整的 CRUD 选项；Player 菜单仅展示只读和有限的自我资料修改功能。  
- F-8 权限认证 (Authentication)：系统启动时强制进入登录界面，输入用户名和密码，匹配 AuthenticationService 中的用户凭证并返回对应的角色实例。

## 3. Java核心概念应用

- Inheritance (继承)：Player 类和 Admin 类共同继承自抽象父类
- Person，复用 ID、用户名、密码等基础字段。  
- Association (关联)：Player 对象内部持有一个 `List<Hero>` 作为已拥有英雄；每个 Hero 对象持有一个 `List<Equipment>` 存储装配的武器。  
- Aggregation (聚合)：Team 对象包含一个 `List<Player>`。战队销毁时玩家依然可以独立存在。  Interface (接口)：定义 Searchable 接口（包含 - searchById(String id) 和 searchByName(String name) 方法），由数据查询服务类实现。此外定义 Persistable 接口用于声明数据导入导出规范。  
- Encapsulation (封装)：所有实体类属性声明为 private。例如 Player 的 winRate 字段只能通过 updateStats() 方法在比赛记录产生时内部更新，严禁外部非法直接篡改。  
- Polymorphism (多态)：在 AuthenticationService 中，使用 `Map<String, Person>` 统一存储所有用户。登录成功后，通过 Person user 引用调用多态方法，或者在菜单分流时通过 instanceof 判断具体角色。  
- Collections (集合)：`List<MatchRecord>`：按时间顺序追加比赛记录。  `Map<String, Hero>`：通过英雄名称实现 $O(1)$ 复杂度的快速检索。  
- Exception Handling (异常处理)：通过 try-catch 捕获用户在控制台输入的非法字符（InputMismatchException），捕获数据录入时的 - DuplicateIdException，以及加载文件时的 IOException。  
- File I/O (文件操作)：通过 FileStorageService 使用 Java 标准的文件流（如 BufferedReader/BufferedWriter），以 CSV 格式将内存中的战队、玩家、比赛等数据保存到本地文本中。  
- Enums (枚举)：定义 HeroType (TANK, MAGE, ASSASSIN等)、MatchResult (WIN, LOSS) 以及 UserRole (ADMIN, PLAYER)。

## 4. 类设计

### 系统划分为三个主包：model（数据实体）、service（业务逻辑）、util（工具）

- Person (Abstract)   抽象用户基类，封装通用账户信息。  
- Player   继承自 Person。维护个人段位、胜率、战队归属及拥有的英雄实例。  
- Admin   继承自 Person。无特殊实体属性，仅作为管理员菜单的身份令牌。  
- Hero   维护英雄核心属性、类型（Enum）及当前穿戴的装备。  
- Equipment   维护装备的基础数值（如攻击力、法强、售价）。  
- Team   维护战队基础数据，动态计算全队的平均数据。
- MatchRecord   存储单场对局的元数据：比赛 ID、日期、对阵双方、选手选用的英雄及胜负结果。  
- Searchable (Interface)   规范对象检索行为（按 ID 或名称）。  
- GameDataManager   核心数据中心，持有系统运行时的所有内存集合，负责调用 CRUD。  
- AuthenticationService   负责用户的登录校验、安全登出以及会话状态（Session）的维护。

## 5. UML 草图

```UML
+---------------------------------+
    |        Abstract Person          |
    +---------------------------------+
                    ^
                    | (Inheritance)
         +----------+----------+
         |                     |
  +---------------+     +---------------+
  |    Player     |     |    Admin      |
  +---------------+     +---------------+
         | 1
         | (Aggregation)
         | *
  +---------------+ * (Association) * +---------------+
  |     Team      |-------------------|     Hero      |
  +---------------+                   +---------------+
                                              | 1
                                              | (Association)
                                              | *
                                      +---------------+
                                      |   Equipment   |
                                      +---------------+
```

## 6. 数据设计-满足最低数据集硬性指标

### 为了确保程序启动即有丰富的数据支持，DataInitializer 将在内存中构建满足要求的初始数据集

- 战队 (Teams)：初始化至少 3 个战队（例如：AG超玩会、狼队、WB）。  
- 玩家 (Players)：创建至少 10 名不同的玩家，分别聚合到上述 3 个战队中，确保每队至少 5 人（部分玩家可作为自由人或替补测试边界值）。  
- 英雄 (Heroes)：创建至少 15 个经典英雄（如李白、武则天、廉颇等），确保每个玩家通过随机或指定算法分配到至少 3 个英雄。  
- 装备 (Equipment)：创建至少 20 件武器防御装（如无尽战刃、贤者之书等），每个英雄在其推荐或当前装配槽中至少绑定 2 件装备。  
- 比赛记录 (Match History)：预先生成至少 10 条模拟对局记录，用于统计初始的胜率和出场率。

## 7. AI使用计划

### 在开发过程中，我将严格区分并使用三种不同的 AI Agent 角色，绝不使用 AI 进行全项目一键生成

- Architect Agent (架构师)：负责在 Stage 2 阶段对我的 plan.md、类划分以及接口设计提供评审建议，纠正潜在的设计模式缺陷（如高耦合）。
- Implementation Agent (实现者)：仅在具体算法（如排行榜多条件打破平局的 Comparator 实现）或重复的 CSV 字符串解析时，委托其生成精简的方法级代码片段。  
- Testing/Reviewer Agent (审查者)：在 Stage 7 阶段，将写好的复杂逻辑类（如 GameDataManager 级联删除英雄时的内存一致性维护）发给 AI，让其专门寻找 NullPointerException 或内存泄露等漏洞。

## 8. 提示词策略

- 精确受限提问：严格避免使用 “Write my project” 这种无脑指令。坚持使用结构化、上下文受限的强提示词。
- 三步验证法：对于 AI 生成的所有代码，执行：“人工阅读理清每一行 -> 复制到本地编译检查 -> 编写手动测试用例跑通边界”。坚决不提交自己无法口头向讲师解释的代码。

## 9. 开发时间线

- Stage 1：精读作业要求，构建 Git 仓库，输出首版 plan.md（当前阶段）。  
- Stage 2：连线 AI 架构师，评审类图设计，手写核心实体类骨架并进行 [Human] 提交。  
- Stage 3：实现 DataInitializer 并硬编码 10玩家/15英雄/20装备 的数据集，跑通基本打印。  
- Stage 4：设计控制台主菜单循环，实现基本的玩家、战队、英雄检索分支。  
- Stage 5：加入 AuthenticationService 与用户权限划分（管理员增删改 vs 玩家只读）。  
- Stage 6：引入文件 I/O，支持系统退出时保存数据，启动时自动加载；实现排行榜平局处理机制。  
- Stage 7：连线 AI 测试评审专家，全面扫描逻辑漏洞，手工编写 10 个测试用例填补测试文档。  
- Stage 8：整理 prompts.md, agent-log.md, reflection.md，导出 Git 日志，封包提交。

## 10. 测试计划

### 我将测试并记录至少 10 个核心业务场景，主要测试用例规划如下

- TC-01 登录越权测试：使用 Player 账户登录，尝试通过硬编码指令触发 Admin 专属的“删除英雄”功能，预期系统给出拒绝访问警告。  
- TC-02 玩家精确查询：输入存在的玩家名（如 "Li Bai"），预期正确级联打印战队、英雄以及英雄脚下的装备。  
- TC-03 模糊/未知检索：查询一个系统中完全不存在的玩家 ID，预期系统捕获自定义异常并优雅提示“未找到相关记录”，而不是引发程序崩溃（Crash）。  
- TC-04 排行榜平局打破测试：构造两个胜率完全相同的玩家，验证系统是否能自动根据总场次和等级判定先后顺序。  
- TC-05 级联删除测试：管理员彻底删除某件装备，验证所有装备了该武器的英雄其武器槽内是否同步清空（防止残留脏数据）

## 11. 风险分析与应对

### 风险 1：AI 生成的代码在本地频繁报错或包含废弃的 API

### 应对方案：严格限定每次向 AI 提问的代码行数在 30 行以内。在提问中强制加上约束："Use standard Java 8+ features only"

### 风险 2：由于合并或者忘记提交，导致最后导出的 Git Commit 数量少于 12 个或缺乏 AI 标签

### 应对方案：养成良好的开发习惯，每写完/由 AI 辅助重构完一个独立的方法，通过控制台验证无误后，立刻执行 git commit

## 12. 最终反思

```reflection
在使用AI工具完成这个项目期间，并非有我预想中的那么顺利。在初期规划阶段，代码量还不多的时候，使用agent工具编码很方便，没有遇到明显的问题。
但当完成基础内容后，随着代码量的增加，由于agent机制，如果不给他明确的提示词，通常会扫描整个项目来阅读理解问题，极大的拉长了工作时间与不必要的token浪费。
与此同时，AI工具不会和领导者一样明白所有的规划路线与之前的错误纠正，在有些情况下可能会遗忘，误解意思，甚至加入重复的逻辑内容导致程序无法正常运行。
由于上下文过于冗长，完成效率会有明显下降，效果也不总是符合预期，通常需要人工介入修改一些格式与语法错误的代码，完全依赖agent开发项目，人工零干预目前还不切实际。
总而言之，这次大作业使我对agent工具有了更深的理解，从之前的观众转变为实际操作的工程师，在人机对话方面也有了很大的进步，进一步丰富了对prompt的理解与使用。
这个项目在假期有时间后还会继续更新进展，争取使之有与工业级项目比肩的能力。
还望提出宝贵建议，我将虚心接受学习！
```

## 13. 最终实现回顾（v2.0.0 追加）

### 13.1 项目演变：从控制台到双模式

原始 plan.md 规划了一个纯控制台应用，实际项目在 v1.0 控制台版本完成后经历了重大扩展：

| 规划项 | 原始计划 | 最终实现 | 差异 |
|--------|----------|----------|------|

| 构建方式 | 纯 JDK 编译 | Maven + shade 插件 | 增加依赖管理 |
| 界面模式 | 仅控制台 | 控制台 + Swing GUI + Web | 大幅扩展 |
| 持久化 | 文件 I/O (CSV) | SQLite 数据库 (5 张表) | 更可靠 |
| 依赖 | 零第三方依赖 | SQLite + SLF4J + FlatLaf | 增加外部依赖 |
| 数据量 | 10 玩家/15 英雄/20 装备/10 对局 | 70+ 玩家/90 英雄/88 装备/1200+ 对局 | 远超规划 |
| 认证 | Map<String, Person> 内存存储 | SQLite 持久化 + 内存缓存 | 更健壮 |
| 日志 | 未规划 | OperationLogService (SQLite+SLF4J 双写) | 新增 |
| 可视化 | 未规划 | ECharts Web 仪表盘 (8 API 端点) | 新增 |
| 构建配置 | 无 | pom.xml + logback.xml | 新增 |

### 13.2 最终实际采用的架构

```structure
src/
├── Main.java               # 控制台入口（原规划保留）
├── model/ 8 个实体类       # 符合原规划
├── service/ 6 个类         # 增加 OperationLogService
├── db/ 5 个类              # 新增 SQLite DAO 层
├── util/ 3 个类            # 增加 DbQuickCheck
└── ui/                     # 新增 GUI 层
    ├── AppLauncher.java    # GUI 入口
    ├── LoginDialog.java    # 登录对话框
    ├── MainFrame.java      # 主框架
    ├── panels/ 7 个面板     # Welcome/Hero/Equipment/Team/PlayerQuery/Ranking/DataManagement
    ├── dialogs/ 6 个对话框  # Hero/Equipment/Team 的 Detail + Edit
    └── web/                # VisualizationServer + dashboard.html
```

### 13.3 与原计划的主要差异

**接口设计**: 原计划 `Searchable` 接口包含 `searchById` / `searchByName` 两个方法；实际实现扩展为 20 个方法，覆盖全部 CRUD 操作。

**枚举 (Enum) 使用**: 原计划定义 `HeroType` / `MatchResult` / `UserRole` 枚举；实际实现中角色使用 `String` 类型（"ADMIN"/"PLAYER"），英雄类型和定位使用 `String` 字段。

**文件 I/O**: 原计划 CSV 格式导入导出；实际实现为 `\t` 分隔的 UTF-8 文件导出，未实现 CSV 导入。

**数据持久化**: 原计划程序退出时保存、启动时加载文件；实际实现为 SQLite 数据库实时持久化，`persistAllToDatabase()` 在程序启动时同步。

**UI 层**: 原计划仅控制台菜单；实际增加 Swing GUI 和 Web 可视化两套界面。

### 13.4 AI 使用实际记录

项目共使用 Codex (GPT-5) 进行了 13+ 轮提示词往返（记录在 `ai/promots.md`）：

- **Architect Agent (架构师)**: 评审类图设计、接口划分、数据初始化方案
- **Implementation Agent (实现者)**: 生成方法级代码、Comparator 实现、DAO 层代码、GUI 面板框架
- **Testing/Reviewer Agent (审查者)**: 扫描逻辑漏洞、编写测试用例、验证边界条件

### 13.5 Web 仪表盘 HTML 修复（2026-06-11）

v2.0.0 初始版本中 dashboard.html 文件存在两个严重 bug：

1. **HTML 文档重复拼接**：文件包含两份完整的 HTML 文档（第 1-518 行与第 519-1061 行），所有 JavaScript 函数被重复定义。
2. **initDashboard 函数嵌套**：第一份文档中的 initDashboard 出现空函数体 + 嵌套声明错误，外层函数体为空，实际加载逻辑被封装在不可达的嵌套函数中。浏览器调用时什么都不执行，数据永远停留在"加载中"状态。

**修复**：删除第一份重复 HTML 文档，移除重复的 fetchJSON 调用，修复破损 </p> 标签，补全缺失的闭合标签。修复后文件从 1061 行精简至 531 行。

### 13.6 五个关键架构反思（详见 ai/reflection.md）

1. **模型层解耦**: AI 初始方案将初始化逻辑混入 model → 人工修正为 DataInitializer 独立工厂
2. **封装保护**: AI 直接暴露内部集合引用 → 人工修正为 `Collections.unmodifiableList` + 浅拷贝
3. **战绩独立计算**: AI 用战队数据代理个人战绩 → 人工修正为动态遍历 MatchRecord
4. **防御性编程**: AI 无异常处理 → 人工修正为 InputHelper 全面兜底
5. **中文排版**: AI 固定 printf 宽度 → 人工修正为 `formatWithChinese()` 动态宽度计算

### 13.7 最终统计数据

| 指标 | 数值 |
|------|------|

| Java 源文件 | 40+ |
| 总代码行（约） | 7,200+ |
| 数据库表 | 5 (admins/heroes/equipments/teams/operation_logs) |
| 英雄数量 | 90 |
| 装备数量 | 88 |
| 战队数量 | 20 |
| 玩家数量 | 70+ |
| 对局记录 | 1200+ |
| 测试用例 | 19 |
| 提示词往返 | 15+ |
| 架构反思 | 5+ |
| GitHub 提交 | 25+ |

---
