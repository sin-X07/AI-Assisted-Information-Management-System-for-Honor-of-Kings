# Agent Log

> 本文档由三个子代理（Architect / Implementation / Testing）协作填充，记录项目的架构决策、实现要点和测试覆盖情况。

---

## Architect Agent

### 1. 项目概述

- **项目名称**: AI 辅助的《王者荣耀》信息管理系统
- **语言/平台**: Java 17+，标准 JDK（无第三方框架依赖），控制台应用

### 2. 分层架构

```UML
┌──────────────────────────────────────────┐
│               Main.java                  │  ← 入口 + 菜单路由 + 角色权限控制
├──────────────────────────────────────────┤
│           service/                       │  ← 业务逻辑层（5 个类 + 1 个接口）
│  AuthenticationService  GameDataManager  │
│  RankingService         FileStorageService│
│  Searchable (interface)                  │
├──────────────────────────────────────────┤
│           util/                          │  ← 工具层（2 个类）
│  DataInitializer        InputHelper      │
├──────────────────────────────────────────┤
│           model/                         │  ← 领域模型层（8 个类）
│  Person  Admin  Player  Team  Hero       │
│  Equipment  MatchRecord  MatchParticipant│
└──────────────────────────────────────────┘
```

### 3. 包结构与文件清单

| 包 | 文件 | 职责 |
|----|------|------|

| `model/` | `Person.java` (2.4 KB) | 人员抽象基类，含 id / username / password / nickname / role / status |
| | `Admin.java` (3.3 KB) | 管理员实体，继承 Person，增加管理级别/部门/权限标志 |
| | `Player.java` (4.3 KB) | 玩家实体，继承 Person，含段位/位置/钟爱英雄/对局记录列表 |
| | `Team.java` (5.2 KB) | 战队实体，含成员列表/常用英雄/荣誉/胜率 |
| | `Hero.java` (5.8 KB) | 英雄实体，含定位/类型/难度/技能/推荐装备 ID 列表 |
| | `Equipment.java` (6.1 KB) | 装备实体，含类型/价格/攻击/法强/生命/物防/法防等属性 |
| | `MatchRecord.java` (2.2 KB) | 对局记录，含时间/模式/结果/时长/参与者列表 |
| | `MatchParticipant.java` (1.7 KB) | 对局参与者，含英雄/阵营/KDA |
| `service/` | `Searchable.java` (1.0 KB) | 核心接口，定义增删改查+玩家查询契约 |
| | `GameDataManager.java` (9.1 KB) | 内存数据仓库，实现 Searchable，含级联查询 |
| | `AuthenticationService.java` (1.8 KB) | 用户认证，含默认 admin/player 账号初始化 |
| | `RankingService.java` (3.6 KB) | 排行榜服务，动态计算胜率并排序输出 |
| | `FileStorageService.java` (2.5 KB) | 文件导出服务，UTF-8 编码 + try-with-resources |
| `util/` | `DataInitializer.java` (13.9 KB) | 初始化 15 英雄 / 22 装备 / 3 战队 / 15 玩家（含对局记录） |
| | `InputHelper.java` (1.5 KB) | Scanner 封装，含防崩溃兜底（NumberFormatException） |
| 根 | `Main.java` (18.3 KB) | 入口类，角色感知菜单、增删改交互、排行榜/导出路由 |

### 4. 核心架构决策

## **4.1 接口驱动设计**

- 所有数据操作通过 `Searchable` 接口定义契约
- `GameDataManager` 是唯一实现类，作为全系统唯一的数据仓库
- 好处：后续可替换为数据库实现而无需改动上层调用

## **4.2 动态胜率计算（非存储代理）**

- `Player` 类上的 `winRate` / `totalMatches` 字段保留但不作为排序依据
- `RankingService.calculateWinRate()` 通过遍历 `Player.matchOverviews` 中的 `MatchRecord.result` 动态统计 `\\"胜利\\"` 次数
- `RankingService.calculateTotalMatches()` 取 `matchOverviews.size()`
- **决策理由**: 严禁用战队统计数据代理个人战绩，确保数据一致性和实时性

## **4.3 不可变数据访问**

- 所有 `getXxx()` 返回 `Collections.unmodifiableList(new ArrayList<>(internalList))`
- 防止外部直接修改内部列表，强制通过 `addXxx()` / `removeXxx()` 方法操作

## **4.4 角色权限控制 (RBAC)**

- 两级角色：`ADMIN` / `PLAYER`
- 管理员（admin）：全部 10 项菜单功能，含增删改 + 导出
- 玩家（player）：仅 8 项浏览/查询功能
- 权限控制通过 `readIntInRange` 的 max 参数硬限制，不依赖 switch 分支

## **4.5 中文编码处理**

- 全部源文件使用 UTF-8 编码（无 BOM）
- `FileStorageService` 明确使用 `StandardCharsets.UTF_8` 写入
- `RankingService.formatWithChinese()` 通过 Unicode 范围 `[\\u4e00-\\u9fa5]` 检测中文字符并按双字节宽度补齐

## **4.6 资源管理**

- `InputHelper` 持有单个 `Scanner(System.in)` 实例，全局复用
- `FileStorageService.exportRankingToFile()` 使用 try-with-resources 包裹 `BufferedWriter`
- `GameDataManager` 在构造函数中一次性加载全部初始数据

### 5. 数据流设计

```Gragh
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

### 6. 初始数据集

| 实体 | 数量 | 示例 |
|------|------|------|

| 英雄 | 15 | H001 李白 / H002 韩信 / H005 貂蝉 / H009 后羿 / H015 蔡文姬 |
| 装备 | 22 | E001 无尽战刃 / E005 暗影战斧 / E009 博学者之怒 / E015 红莲斗篷 |
| 战队 | 3 | T001 重庆狼队 / T002 成都AG超玩会 / T003 武汉eStarPro |
| 玩家 | 15 | 花海(15场12胜) / 一诺(14场11胜) / Fly(12场9胜) / Cat(13场10胜) |
| 对局记录 | ≥120 | 每玩家 8-15 场，每场含 2 名 MatchParticipant |
| 账号 | 2 | admin(ADMIN) / player(PLAYER) |

---

## Implementation Agent

### 1. 实现清单与完成状态

| 模块 | 文件 | 状态 | 说明 |
|------|------|------|------|

| 领域模型 | 8 个 model 类 | ✅ | 全字段 getter/setter，含全参构造器 |
| 认证服务 | `AuthenticationService.java` | ✅ | 初始化 admin+player 账号，login() 返回 Person |
| 数据仓库 | `GameDataManager.java` | ✅ | 实现 Searchable，含级联 displayPlayerDetails() |
| 排行榜 | `RankingService.java` | ✅ | Comparator 双重降序 + formatWithChinese 对齐 |
| 文件导出 | `FileStorageService.java` | ✅ | try-with-resources + UTF-8 + tab 分隔 |
| 数据初始化 | `DataInitializer.java` | ✅ | createPlayer 动态生成 MatchRecord 列表 |
| 输入工具 | `InputHelper.java` | ✅ | readInt/readIntInRange/readRequiredString |
| 入口 | `Main.java` | ✅ | 角色感知菜单 + 13 个交互方法 + CRUD |
| 接口 | `Searchable.java` | ✅ | 20 个方法覆盖全数据操作面 |

### 2. 关键实现细节

## **2.1 排行榜排序（RankingService.java）**

```java
players.sort(Comparator
    .comparingDouble(RankingService::calculateWinRate)
    .thenComparingInt(RankingService::calculateTotalMatches)
    .reversed());  // 统一 reversal，双重降序
```

- 优先按胜率降序；胜率相同按总场次降序
- 此前 Bug：分别 `.reversed()` 导致第二条规则变为升序（已修复）

## **2.2 中文字符对齐（RankingService.formatWithChinese）**

```java
private String formatWithChinese(String str, int totalLen) {
    int currentDisplayWidth = 0;
    for (char c : str.toCharArray()) {
        if (Character.toString(c).matches(\\"[\\\\u4e00-\\\\u9fa5]\\")) {
            currentDisplayWidth += 2;  // 中文字符算双宽度
        } else {
            currentDisplayWidth += 1;
        }
    }
    int paddingSpaces = totalLen - currentDisplayWidth;
    return paddingSpaces <= 0 ? str : str + \\" \\".repeat(paddingSpaces);
}
```

- 应用于表头和数据行的所有字符串列（排名、ID、昵称、战队、胜率标签、总场次标签）
- 数值列（胜率百分比、对局数）仍使用标准 `%7.1f%%` / `%6d` 格式右对齐

## **2.3 文件导出（FileStorageService.java）**

```java
try (BufferedWriter writer = new BufferedWriter(
    new OutputStreamWriter(
        new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
    // 写入数据...
} catch (IOException e) {
    System.out.println(\\"文件写入失败: \\" + e.getMessage());
}
```

- 严格使用 try-with-resources，确保文件流在任何路径下安全关闭
- 表头和数据用制表符 `\\t` 分隔

## **2.4 玩家级联查询（GameDataManager.displayPlayerDetails）**

- 输入：玩家 ID（或昵称）
- 流程：① 从 teams 的 memberNames 反查战队 → ② 遍历 mainHeroes 查 Hero → ③ 遍历 equipmentIds 查 Equipment
- 输出：玩家信息 → 战队信息 → 英雄详情（定位/类型/难度）→ 推荐装备（名称/类型/价格）→ 战队荣誉

## **2.5 角色感知菜单（Main.java）**

```java
boolean isAdmin = \\"ADMIN\\".equals(currentUser.getRole());
int maxChoice = isAdmin ? 10 : 8;
int choice = inputHelper.readIntInRange(\\"请选择功能：\\", 0, maxChoice);
```

- 菜单范围由 `readIntInRange` 硬限制，超出自动拦截
- 管理员菜单：1-8（公共）+ 9（数据管理）+ 10（导出）
- 玩家菜单：仅 1-8

### 3. 修复历史

| 日期 | 问题 | 修复 |
|------|------|------|

| 2026-06-07 | 排行榜排序方向错误（双重 .reversed() 导致第二条升序） | 改为统一 `.reversed()` |
| 2026-06-07 | 中英文混排不对齐（4.cat / 5.fly 偏移） | 新增 `formatWithChinese()` 按中文字符双宽度补齐 |
| 2026-06-07 | RankingService.java 文件编码损坏（中文乱码） | git 恢复 + `UTF8Encoding($false)` 重写（无 BOM） |
| 2026-06-07 | 编译命令缺少 `-encoding UTF-8` | CI/CD 脚本补充编码参数 |

---

## Testing Agent

### 1. 测试覆盖总览

| 条目 | 数值 |
|------|------|

| 测试用例总数 | 11 |
| 覆盖的功能模块 | 认证、菜单健壮性、英雄查询、玩家级联查询、排行榜、导出、权限控制、CRUD、退出循环 |
| 测试方式 | PowerShell Pipeline 黑盒自动化注入 |
| 测试文件 | `docs/test-cases.md` |

### 2. 测试用例矩阵

| 编号 | 测试项 | 验证目标 |
|------|--------|----------|

| TC-01 | 错误凭证拦截 | 拒绝错误用户名/密码，打印准确消息后回到登录 |
| TC-02 | 非法数字兜底 | 输入 `abc` 时 `InputHelper` 打印 `\\"请输入有效的整数。\\"` 不崩溃 |
| TC-03 | 编号查英雄 | `H001` → 李白，通过英文 ID 绕过中文编码问题 |
| TC-04 | 玩家级联查询 | `花海` → eStarPro → 5 位英雄 → 关联装备属性 |
| TC-05 | 排行榜降序 | TOP 5 按胜率→场次双重降序，花海居首 |
| TC-06 | 排行榜边界 | `topN=0` 安全跳过 / `topN=100` 只显示 15 条 |
| TC-07 | 导出排行榜 | 生成 UTF-8 的 `ranking.txt`，`try-with-resources` 安全关闭 |
| TC-08 | admin 全权限 | 菜单 0-10，可见增删改和导出入口 |
| TC-09 | player 受限 | 菜单 0-8，输入 9 被 `readIntInRange` 拦截 |
| TC-10 | CRUD 闭环 | 添加英雄 → 更新英雄 → 删除英雄，全程反馈 |
| TC-11 | 退出登录循环 | `0` 退出后回到登录界面，可重新登录 |

### 3. 一键测试脚本

```powershell
javac -d out -encoding UTF-8 src/Main.java src/model/*.java src/util/*.java src/service/*.java

$inputData = @\\"
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
\\"@

$inputData | java -cp out Main
```

### 4. 对实际代码的消息验证结果

| 场景 | 文档消息（旧） | 实际代码消息（已验证） | 状态 |
|------|----------------|------------------------|------|

| 登录失败 | `\\"登录失败：用户名或密码错误！\\"` | `\\"用户名或密码错误，或账号状态不可用，请重试。\\"` | 🔧 已修正 |
| 非法输入 | `\\"输入非法，请输入合法的菜单数字！\\"` | `\\"请输入有效的整数。\\"` | 🔧 已修正 |
| H001 归属 | 描述为"玩家" | H001 是英雄编号，非玩家 | 🔧 已修正 |
| 玩家数量 | 10 名 | 15 名（3 战队 × 5 成员） | 🔧 已修正 |
| 胜率数据 | 65.5%/120 场（不存在） | 花海 80%/15 场（实际） | 🔧 已修正 |

### 5. 未覆盖项（已知局限）

- 玩家 `player` 账号在 `AuthenticationService` 中预设了静态字段值，但实际项目运行时这些字段不会出现在级联查询中（player 不在任何战队的 memberNames 里）
- 装备的 11 项数值属性（atk/magicAtk/hp/...）在控制台输出中未展示，仅在 model 中有 getter
- `MatchParticipant` 的 KDA 数据未在任何查询路径中被使用
- 英雄技能名称（4 个技能字段）在级联查询中未展示
- 无单元测试，仅黑盒集成测试

---
