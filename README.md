# AI-Assisted Honor of Kings Information Management System

> **语言**: Java 17+ | **构建**: 纯标准 JDK（零第三方依赖）
> **类型**: 控制台应用 | **编码**: UTF-8

---

## 1. Project Overview

一个基于控制台的《王者荣耀》信息管理系统，核心功能包括英雄/装备/战队数据的增删改查、玩家战绩的级联查询、荣誉排行榜的动态计算与展示，以及排行榜结果的 UTF-8 文件导出。系统实现了角色权限控制——管理员具备全部数据操作权限，普通玩家仅可浏览和查询。

### 项目结构

```structure
src/
├── Main.java                          # 入口类，角色感知菜单 + 13 个交互方法
├── model/
│   ├── Person.java                    # 人员抽象基类
│   ├── Admin.java                     # 管理员实体
│   ├── Player.java                    # 玩家实体（含对局记录列表）
│   ├── Team.java                      # 战队实体
│   ├── Hero.java                      # 英雄实体
│   ├── Equipment.java                 # 装备实体
│   ├── MatchRecord.java               # 对局记录
│   └── MatchParticipant.java          # 对局参与者
├── service/
│   ├── Searchable.java                # 核心数据操作接口（20 个方法）
│   ├── GameDataManager.java           # 内存数据仓库 + 级联查询实现
│   ├── AuthenticationService.java     # 用户认证服务
│   ├── RankingService.java            # 排行榜排序 + 输出
│   └── FileStorageService.java        # 文件导出（UTF-8 + try-with-resources）
└── util/
    ├── DataInitializer.java           # 初始化 15 英雄 / 22 装备 / 3 战队 / 15 玩家
    └── InputHelper.java               # Scanner 封装 + 输入防崩溃

docs/
├── plan.md                            # 开发计划文档
└── test-cases.md                      # 11 个自动化测试用例

ai/
├── agent-log.md                       # 三代理协作日志（Architect/Implementation/Testing）
├── promots.md                         # AI 提示词记录
└── refection.md                       # 项目反思
```

### 初始数据集

| 实体 | 数量 | 示例 |
|------|------|------|

| 英雄 | 15 | 李白(H001)、韩信、貂蝉、后羿、蔡文姬 |
| 装备 | 22 | 无尽战刃、暗影战斧、博学者之怒、红莲斗篷 |
| 战队 | 3 | 重庆狼队、成都AG超玩会、武汉eStarPro |
| 玩家 | 15 | 花海(胜率80%)、一诺(78.6%)、Fly(75%) |
| 对局记录 | ≥120 | 每玩家 8-15 场，动态生成胜负比例 |
| 默认账号 | 2 | admin (管理员) / player (普通玩家) |

---

## 2. How to Run

### 前置条件

- JDK 17 或更高版本
- 系统终端支持 UTF-8 编码

### 编译

```bash
javac -d out -encoding UTF-8 src/Main.java src/model/*.java src/util/*.java src/service/*.java
```

### 运行

```bash
java -cp out Main
```

### 一键编译 + 运行

```bash
javac -d out -encoding UTF-8 src/Main.java src/model/*.java src/util/*.java src/service/*.java && java -cp out Main
```

---

## 3. Default Login Accounts

| 角色 | 用户名 | 密码 | 权限 |
|------|--------|------|------|

| 管理员 | `admin` | `123456` | 全部 10 项功能（含增删改 + 导出） |
| 普通玩家 | `player` | `123456` | 8 项功能（浏览/查询/排行榜） |

---

## 4. Implemented Features

### 管理员菜单（功能 0-10）

| 编号 | 功能 | 说明 |
|------|------|------|

| 1 | 查看英雄列表 | 展示全部 15 位英雄的基本信息 |
| 2 | 查看装备列表 | 展示全部 22 件装备的名称/类型/价格 |
| 3 | 查看战队列表 | 展示 3 支战队的名称/简称/地区/胜率 |
| 4 | 查询英雄 | 支持按名称（中文）或编号（如 H001）检索 |
| 5 | 查询装备 | 支持按名称或编号（如 E001）检索 |
| 6 | 查询战队 | 支持按名称或编号（如 T001）检索 |
| 7 | 玩家详情查询 | **级联查询**: 玩家 → 所属战队 → 常用英雄 → 推荐装备 |
| 8 | 玩家荣誉排行榜 | 按胜率→总场次双重降序，中文对齐排版 |
| 9 | 数据管理（增删改） | 子菜单：英雄/装备/战队的添加、删除、更新 |
| 10 | 导出排行榜 | 将排序结果写入 UTF-8 文件（制表符分隔） |
| 0 | 退出登录 | 返回登录界面 |

### 玩家菜单（功能 0-8）

普通玩家仅可使用上述功能 1-8，无权访问数据管理和导出功能。`readIntInRange` 的 max 参数硬限制输入范围。

### 数据管理子菜单（仅管理员）

| 编号 | 操作 |
|------|------|

| 1 | 添加英雄 |
| 2 | 添加装备 |
| 3 | 添加战队 |
| 4 | 删除英雄 |
| 5 | 删除装备 |
| 6 | 删除战队 |
| 7 | 更新英雄（展示旧值，回车保留） |
| 8 | 更新装备（展示旧值，回车保留） |
| 9 | 更新战队（展示旧值，回车保留） |
| 0 | 返回主菜单 |

### 核心特性亮点

- **动态胜率计算**: 不存储死数据，通过遍历 `MatchRecord` 列表实时统计，确保数据一致性
- **双重降序排序**: `Comparator.comparingDouble(winRate).thenComparingInt(totalMatches).reversed()`
- **中英文对齐**: `formatWithChinese()` 检测 Unicode 中文字符并按双字节宽度补齐空格
- **级联查询**: 玩家 → 战队 → 英雄 → 装备，一次查询贯穿四级实体
- **输入防崩溃**: `InputHelper` 对 `NumberFormatException` 进行全面兜底
- **UTF-8 导出**: `StandardCharsets.UTF_8` + `try-with-resources` 确保文件流安全

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
| **文件 I/O** | `FileStorageService.java` | `BufferedWriter` + `OutputStreamWriter` + `FileOutputStream` |
| **字符编码** | `StandardCharsets.UTF_8` | 显式指定 UTF-8 编码读写 |
| **日期时间 API** | `LocalDateTime` | 管理员最后登录时间、对局时间 |
| **字符串处理** | `formatWithChinese()`, `String.repeat()` | Unicode 宽度计算 + 空格补齐 |
| **控制流** | `Main.mainMenuLoop()` | 双层 while 循环 + switch 路由 |

---

## 6. AI Usage Summary

本项目由 Codex (GPT-5) 辅助开发，AI 参与了以下阶段：

| 阶段 | AI 贡献 |
|------|---------|

| **架构设计** | 确定分层结构（model/service/util）、接口驱动设计、RBAC 权限模型、动态胜率计算方案 |
| **数据初始化** | 创建 15 英雄 / 22 装备 / 3 战队 / 15 玩家（含 120+ 对局记录），通过胜负比例控制不同胜率 |
| **核心实现** | `GameDataManager` 的级联查询、`RankingService` 的动态排序、`FileStorageService` 的 try-with-resources 导出 |
| **菜单补全** | 从 6 项扩展到 10 项（管理员）+ 8 项（玩家），含 CRUD 子菜单和全部交互逻辑 |
| **Bug 修复** | Comparator 双重降序错误、中英文排版不对齐（formatWithChinese）、文件编码损坏恢复 |
| **测试编写** | `docs/test-cases.md` 中 11 个自动化测试用例 + 一键 CI/CD 脚本 |
| **文档生成** | `ai/agent-log.md`（三代理日志）、`README.md`（本文档）、多轮 review 修正 |

### 开发流程

```map
需求分析 → 架构设计 → 分层实现（model → service → Main）→ 功能补全
                                              ↓
                          test-cases.md ← 审查修正 ← 全项目扫描 ← 编码/文件修复
                               ↓
                        agent-log.md + README.md
```

---

## 7. Testing Summary

### 测试方法

PowerShell Pipeline 黑盒自动化注入——将所有输入预先编排后通过标准输入流一次性注入 Java 进程。

### 11 个测试用例

| 编号 | 测试项 | 验证目标 |
|------|--------|----------|

| TC-01 | 错误凭证拦截 | 拒绝错误用户名/密码，返回登录界面 |
| TC-02 | 非法数字兜底 | 输入 `abc` 不崩溃，打印 `"请输入有效的整数。"` |
| TC-03 | 编号查英雄 | `H001` → 李白（英文 ID 绕过中文编码问题） |
| TC-04 | 玩家级联查询 | `花海` → eStarPro → 5 位英雄 → 关联装备 |
| TC-05 | 排行榜降序 | TOP 5 按胜率→场次双重降序 |
| TC-06 | 排行榜边界 | `topN=0` 安全跳过 / `topN=100` 仅显示 15 条 |
| TC-07 | 导出排行榜 | UTF-8 编码文件 + try-with-resources 安全关闭 |
| TC-08 | admin 全权限 | 菜单 0-10，含增删改和导出 |
| TC-09 | player 受限 | 菜单 0-8，输入 9 被 `readIntInRange` 拦截 |
| TC-10 | CRUD 闭环 | 添加 → 更新 → 删除英雄，全程反馈 |
| TC-11 | 退出登录 | 退出后回到登录界面，可重新登录 |

### 一键测试

```bash
javac -d out -encoding UTF-8 src/Main.java src/model/*.java src/util/*.java src/service/*.java
```

完整测试脚本见 `docs/test-cases.md` 底部。

---

## 8. Known Limitations

| 局限 | 说明 |
|------|------|

| 无持久化 | 所有数据存储在内存 `ArrayList` 中，程序退出后修改丢失 |
| 无单元测试 | 仅黑盒集成测试，未使用 JUnit 等框架 |
| KDA 未展示 | `MatchParticipant` 的击杀/死亡/助攻数据未在任何查询路径中呈现 |
| 技能未展示 | 英雄的 4 个技能名称字段（如"将进酒""神来之笔"）未在级联查询中展示 |
| 装备属性未全展示 | 装备的 11 项数值属性（atk/magicAtk/hp/物防/法防等）在控制台仅展示名称/类型/价格 |
| 玩家 player 账号 | 预设的普通玩家账号不在任何战队列表中，级联查询时会显示"无战队" |
| 单线程 | 纯控制台交互，无并发支持 |
| 中文依赖 | 部分查询（`findHeroByName`）依赖精确中文匹配，简繁体/别名不支持 |

---

*项目由 Codex (GPT-5) 辅助开发，遵循标准 JDK 实现，零第三方依赖。*
