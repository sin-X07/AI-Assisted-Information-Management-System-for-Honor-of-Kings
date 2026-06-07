# 教学反思与设计决策总结

> 本文档记录了在 AI 辅助构建《王者荣耀》信息管理系统过程中，人类工程师于关键架构节点做出的纠偏决策。
> 每个问题遵循"现象 → 原因分析 → 重构决策"的结构展开。

---

## 问题一：模型层数据强耦合与单一职责原则的冲突

### 现象

开发初期，AI 为了快速提供初始数据，尝试将假数据硬编码在 `model/Hero.java` 等实体类的静态方法中（如 `createDefaultHeroes()`）。

### 原因分析

- **违反单一职责原则 (SRP)**: `Hero` 类的职责应当极为纯粹——仅作为承载单个英雄属性的数据结构。一旦将全局假数据初始化的行为混入其中，会导致模型类臃肿且职责不清。
- **生命周期死锁**: 当项目要求 3 个战队、15 名玩家、15 个英雄、22 件装备、120+ 条对局记录层层包含且具备复杂的级联绑定时，在各自的 model 类内部初始化会导致对象之间无法交叉咬合，形成无法解开的强耦合。

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

---

## 问题二：直接返回集合引用带来的表示泄露

### 现象

在设计 `GameDataManager`（内存管理器）的集合读取方法（如 `getHeroes()`）时，最初的朴素写法是直接将类内部 `ArrayList` 的内存引用返回给外部调用者。

### 原因分析

- **封装性荡然无存**: 外部菜单层或未授权模块拿到引用后，可以直接执行 `.clear()` 或 `.remove()`，在绕过系统业务验证的情况下偷偷篡改或擦除内存中的核心数据——这就是经典的"表示泄露"。

### 重构决策

贯彻**数据只读安全性与封装性保护**的设计标准。所有对外公开的集合 Getter 方法统一使用不可变视图包装：

```java
// GameDataManager.java — 对外暴露的 getter
public List<Hero> getHeroes() {
    return Collections.unmodifiableList(new ArrayList<>(heroes));
}

public List<Player> getPlayers() {
    return Collections.unmodifiableList(new ArrayList<>(players));
}
```

**原理**: 先通过 `new ArrayList<>(...)` 做浅拷贝，再用 `Collections.unmodifiableList` 包裹。外部可以安全遍历和只读访问，但任何修改集合结构的操作都会被 JVM 抛出 `UnsupportedOperationException` 强行拦截。

---

## 问题三：战队数据代理个人战绩导致排行榜失真

### 现象

在编写 `RankingService`（全服胜率排行榜）前，系统面临一个设计分歧：由于玩家个人的胜率和总场次没有独立存储，系统曾尝试**用所属战队的统计数据进行间接代理**。

### 原因分析

- **领域建模失真**: 同一战队中的顶级核心与刚入队的替补新人，在系统中的战绩将完全并列——数据离散度为零。
- **排序算法失效**: 排行榜要求"优先按胜率降序；胜率相同时按总场次降序"的双重条件排序。若同队五人共享一套战队数据，则五人永远卡在完全相同的顺位，全服排行榜功能彻底失效。

### 重构决策

做出两项关键决策，绝不使用战队数据代理玩家个人战绩：

1. **字段独立化 + 动态计算**: `Player` 类保留 `winRate` / `totalMatches` 等字段（用于 `DataInitializer` 初始化时提供参考值），但 `RankingService` 在排序时**不读取这些静态字段**，而是动态遍历 `Player.matchOverviews` 中的 `MatchRecord` 列表，实时统计 `"胜利"` 的记录数来计算胜率和总场次。
2. **离散化假数据注入**: `DataInitializer.createPlayer()` 为 15 名玩家各生成 8-15 场对局记录，通过胜负比例控制不同胜率（如花海 12 胜 / 15 场 = 80%，轩染 4 胜 / 8 场 = 50%），为排行榜排序算法提供了真实有效的测试土壤。

### 关键代码路径

```java
// RankingService.java — 动态计算，不依赖 Player 上的静态字段
static double calculateWinRate(Player player) {
    List<MatchRecord> records = player.getMatchOverviews();
    long wins = 0;
    for (MatchRecord r : records) {
        if (\"胜利\".equals(r.getResult())) wins++;
    }
    return (double) wins / records.size();
}
```

---

## 问题四：控制台键盘脏输入崩溃与终端管道中文编码紊乱

### 现象

控制台系统面临两层边缘危机：

1. **非健壮输入崩溃**: 系统菜单要求输入数字时，用户误输入英文字母，`Integer.parseInt()` 直接抛出 `NumberFormatException` 导致控制台程序瞬间崩溃。
2. **自动化流测试编码偏离**: 采用 PowerShell 管道模拟流输入（`$inputData | java ...`）一键模拟登录和菜单全路径时，操作系统终端管道的中文编码干扰导致在传输中文名称进行检索时无法正常命中对象。

### 重构决策

采取高强度防御性编程 + 自适应检索方案：

**1. 边界异常全面拦截 (`InputHelper.java`)**

```java
public int readInt(String prompt) {
    while (true) {
        String value = readRequiredString(prompt);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.out.println(\"请输入有效的整数。\");
        }
    }
}
```

当捕获到 `NumberFormatException` 时程序绝不崩溃，而是提示错误并循环兜底，直到输入合法的菜单数字。`readIntInRange()` 进一步限制输入值必须在 `[min, max]` 区间。
**2. 输入流结束安全保护**

```java
public String readLine(String prompt) {
    if (!scanner.hasNextLine()) {
        System.out.println(\"输入流已结束，程序退出。\");
        System.exit(0);
    }
    return scanner.nextLine().trim();
}
```

当检测到输入流耗尽（如自动化流测试结束）时，程序优雅退出，消除了未捕获流异常的隐患。
**3. 多维兼容检索算法**

在 `Main.searchHero()` 中实现"名称或编号双兼容"策略：

```java
private static void searchHero() {
    String keyword = inputHelper.readRequiredString(\"请输入英雄名称或编号：\");
    Hero hero = gameDataManager.findHeroByName(keyword);  // 先按中文名查找
    if (hero == null) {
        hero = gameDataManager.findHeroById(keyword);       // 再按英文ID兜底
    }
    // ...
}
```

系统同时支持通过中文名（如"李白"）和无编码歧义的英数字编号（如 `H001`）进行检索，完美绕开了终端环境的编码干扰。

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
        if (Character.toString(c).matches(\"[\\\\u4e00-\\\\u9fa5]\")) {
            currentDisplayWidth += 2;   // 中文字符按双宽度计算
        } else {
            currentDisplayWidth += 1;
        }
    }
    int paddingSpaces = totalLen - currentDisplayWidth;
    return paddingSpaces <= 0 ? str : str + \" \".repeat(paddingSpaces);
}
```

应用于表头和数据行的所有字符串列。

---

## 总结与收获

通过这次基于 AI 辅助的分层重构实践，我深刻体会到：

**AI 是一把高效的双刃剑。** 它能极快地生成基础代码架子，但它缺乏对项目工程指标的边界敏锐度，容易写出高耦合、低封装、无异常保护的脆弱代码。

整个项目的成功落地，关键在于人类工程师在以下维度做出的绝对主导和纠偏决策：

| 维度 | 人类决策 | AI 初始方案的缺陷 |
|------|----------|-------------------|

| 架构分层 | 严格的 model / service / util 三层分离 | 将初始化逻辑混入 model |
| 封装保护 | `Collections.unmodifiableList` + 浅拷贝 | 直接暴露内部集合引用 |
| 领域建模 | 通过 MatchRecord 动态计算个人战绩 | 用战队数据代理（排行榜失真） |
| 防御性编程 | `InputHelper` 全面异常兜底 | 无异常处理（直接崩溃） |
| 编码鲁棒性 | 名称+编号双兼容 + UTF-8 一致 | 仅单一中文匹配 |
| 排版精度 | `formatWithChinese` 动态宽度计算 | 固定 `printf` 宽度（中英文错位） |

这不仅让系统顺利通过了 11 个一键自动化测试用例，更让代码质量达到了结构清晰、职责分离、高鲁棒性的工业级标准。

---
