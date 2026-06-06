# 1. 项目目标
## 本系统是一个基于 Java 面向对象编程（OOP）技术开发的《王者荣耀》信息管理系统（IMS）。系统主要服务于两类用户：
- 普通玩家 (Player)：用于查询个人数据、查看英雄与战队排行榜、浏览比赛历史等。  
- 系统管理员 (Admin)：拥有最高权限，负责对玩家、战队、英雄、装备及比赛记录进行维护（增删改查）。

# 2. 需求分析
## 系统将采用控制台菜单驱动界面（Console-driven Menu）进行交互，确保核心业务逻辑稳定后再扩展加分项。核心功能实现规划如下：
- F-1 玩家查询 (Player Lookup)：输入玩家 ID 或名字，从数据中心检索并打印该玩家基础信息、所属战队、胜率、拥有的英雄列表以及各英雄当前装配的装备。  
- F-2 战队总览 (Team Overview)：输入战队 ID 或名，动态计算并展示战队成员、平均等级、总对局数、全队综合胜率以及战队内的最高胜率玩家（Top Player）。  
- F-3 英雄详情 (Hero Details)：按名称搜索英雄，展示其属性、兼容的装备池，以及当前系统里拥有该英雄的玩家列表。  
- F-4 装备统计 (Equipment Statistics)：设计排序逻辑，通过 Collections.sort() 或存储在 TreeMap 中，按“使用次数（Usage Count）”对 20 件基础装备进行全网降序排列。  
- F-5 比赛历史 (Match History)：支持获取指定玩家或战队最近的 $N$ 场比赛记录，输出胜负分布和英雄出场率（Pick Rate）。  
- F-6 排行榜 (Leaderboard)：展示系统前 X 名的高胜率玩家。若胜率相同（Ties），则依次对比“总对局数（降序）”和“玩家等级（降序）”来打破平局。  
- F-7 数据管理 (Data Management)：设计严格的权限分流：Admin 菜单展示完整的 CRUD 选项；Player 菜单仅展示只读和有限的自我资料修改功能。  
- F-8 权限认证 (Authentication)：系统启动时强制进入登录界面，输入用户名和密码，匹配 AuthenticationService 中的用户凭证并返回对应的角色实例。

# 3. Java核心概念应用
- Inheritance (继承)：Player 类和 Admin 类共同继承自抽象父类 
- Person，复用 ID、用户名、密码等基础字段。  
- Association (关联)：Player 对象内部持有一个 List<Hero> 作为已拥有英雄；每个 Hero 对象持有一个 List<Equipment> 存储装配的武器。  
- Aggregation (聚合)：Team 对象包含一个 List<Player>。战队销毁时玩家依然可以独立存在。  Interface (接口)：定义 Searchable 接口（包含 - searchById(String id) 和 searchByName(String name) 方法），由数据查询服务类实现。此外定义 Persistable 接口用于声明数据导入导出规范。  
- Encapsulation (封装)：所有实体类属性声明为 private。例如 Player 的 winRate 字段只能通过 updateStats() 方法在比赛记录产生时内部更新，严禁外部非法直接篡改。  
- Polymorphism (多态)：在 AuthenticationService 中，使用 Map<String, Person> 统一存储所有用户。登录成功后，通过 Person user 引用调用多态方法，或者在菜单分流时通过 instanceof 判断具体角色。  
- Collections (集合)：List<MatchRecord>：按时间顺序追加比赛记录。  Map<String, Hero>：通过英雄名称实现 $O(1)$ 复杂度的快速检索。  
- Exception Handling (异常处理)：通过 try-catch 捕获用户在控制台输入的非法字符（InputMismatchException），捕获数据录入时的 - DuplicateIdException，以及加载文件时的 IOException。  
- File I/O (文件操作)：通过 FileStorageService 使用 Java 标准的文件流（如 BufferedReader/BufferedWriter），以 CSV 格式将内存中的战队、玩家、比赛等数据保存到本地文本中。  
- Enums (枚举)：定义 HeroType (TANK, MAGE, ASSASSIN等)、MatchResult (WIN, LOSS) 以及 UserRole (ADMIN, PLAYER)。 
# 4. 类设计
## 系统划分为三个主包：model（数据实体）、service（业务逻辑）、util（工具）
- Person (Abstract)   抽象用户基类，封装通用账户信息。  
- Player  继承自 Person。维护个人段位、胜率、战队归属及拥有的英雄实例。  
- Admin   继承自 Person。无特殊实体属性，仅作为管理员菜单的身份令牌。  
- Hero    维护英雄核心属性、类型（Enum）及当前穿戴的装备。  
- Equipment   维护装备的基础数值（如攻击力、法强、售价）。  
- Team    维护战队基础数据，动态计算全队的平均数据。      
- MatchRecord    存储单场对局的元数据：比赛 ID、日期、对阵双方、选手选用的英雄及胜负结果。  
- Searchable (Interface)		规范对象检索行为（按 ID 或名称）。  
- GameDataManager		核心数据中心，持有系统运行时的所有内存集合，负责调用 CRUD。  
- AuthenticationService	负责用户的登录校验、安全登出以及会话状态（Session）的维护。
# 5. UML 草图
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
# 6. 数据设计-满足最低数据集硬性指标
## 为了确保程序启动即有丰富的数据支持，DataInitializer 将在内存中构建满足要求的初始数据集：
- 战队 (Teams)：初始化至少 3 个战队（例如：AG超玩会、狼队、WB）。  
- 玩家 (Players)：创建至少 10 名不同的玩家，分别聚合到上述 3 个战队中，确保每队至少 5 人（部分玩家可作为自由人或替补测试边界值）。  
- 英雄 (Heroes)：创建至少 15 个经典英雄（如李白、武则天、廉颇等），确保每个玩家通过随机或指定算法分配到至少 3 个英雄。  
- 装备 (Equipment)：创建至少 20 件武器防御装（如无尽战刃、贤者之书等），每个英雄在其推荐或当前装配槽中至少绑定 2 件装备。  
- 比赛记录 (Match History)：预先生成至少 10 条模拟对局记录，用于统计初始的胜率和出场率。 
# 7. AI使用计划
## 在开发过程中，我将严格区分并使用三种不同的 AI Agent 角色，绝不使用 AI 进行全项目一键生成：
- Architect Agent (架构师)：负责在 Stage 2 阶段对我的 plan.md、类划分以及接口设计提供评审建议，纠正潜在的设计模式缺陷（如高耦合）。 
- Implementation Agent (实现者)：仅在具体算法（如排行榜多条件打破平局的 Comparator 实现）或重复的 CSV 字符串解析时，委托其生成精简的方法级代码片段。  
- Testing/Reviewer Agent (审查者)：在 Stage 7 阶段，将写好的复杂逻辑类（如 GameDataManager 级联删除英雄时的内存一致性维护）发给 AI，让其专门寻找 NullPointerException 或内存泄露等漏洞。
# 8. 提示词策略
- 精确受限提问：严格避免使用 “Write my project” 这种无脑指令。坚持使用结构化、上下文受限的强提示词。
- 三步验证法：对于 AI 生成的所有代码，执行：“人工阅读理清每一行 -> 复制到本地编译检查 -> 编写手动测试用例跑通边界”。坚决不提交自己无法口头向讲师解释的代码。
# 9. 开发时间线
- Stage 1：精读作业要求，构建 Git 仓库，输出首版 plan.md（当前阶段）。  
- Stage 2：连线 AI 架构师，评审类图设计，手写核心实体类骨架并进行 [Human] 提交。  
- Stage 3：实现 DataInitializer 并硬编码 10玩家/15英雄/20装备 的数据集，跑通基本打印。  
- Stage 4：设计控制台主菜单循环，实现基本的玩家、战队、英雄检索分支。  
- Stage 5：加入 AuthenticationService 与用户权限划分（管理员增删改 vs 玩家只读）。  
- Stage 6：引入文件 I/O，支持系统退出时保存数据，启动时自动加载；实现排行榜平局处理机制。  
- Stage 7：连线 AI 测试评审专家，全面扫描逻辑漏洞，手工编写 10 个测试用例填补测试文档。  
- Stage 8：整理 prompts.md, agent-log.md, reflection.md，导出 Git 日志，封包提交。
# 10. 测试计划
## 我将手动测试并记录至少 10 个核心业务场景，主要测试用例规划如下：
- TC-01 登录越权测试：使用 Player 账户登录，尝试通过硬编码指令触发 Admin 专属的“删除英雄”功能，预期系统给出拒绝访问警告。  
- TC-02 玩家精确查询：输入存在的玩家名（如 "Li Bai"），预期正确级联打印战队、英雄以及英雄脚下的装备。  
- TC-03 模糊/未知检索：查询一个系统中完全不存在的玩家 ID，预期系统捕获自定义异常并优雅提示“未找到相关记录”，而不是引发程序崩溃（Crash）。  
- TC-04 排行榜平局打破测试：构造两个胜率完全相同的玩家，验证系统是否能自动根据总场次和等级判定先后顺序。  
- TC-05 级联删除测试：管理员彻底删除某件装备，验证所有装备了该武器的英雄其武器槽内是否同步清空（防止残留脏数据）
# 11. 风险分析与应对
## 风险 1：AI 生成的代码在本地频繁报错或包含废弃的 API。
### 应对方案：严格限定每次向 AI 提问的代码行数在 30 行以内。在提问中强制加上约束："Use standard Java 8+ features only"。
## 风险 2：由于合并或者忘记提交，导致最后导出的 Git Commit 数量少于 12 个或缺乏 AI 标签。
### 应对方案：养成良好的开发习惯，每写完/由 AI 辅助重构完一个独立的方法，通过控制台验证无误后，立刻执行 git commit。并在电脑旁张贴前缀便利贴提醒自己（如 [AI-Implementation]）。
# 12. 最终反思占位符
(此区域留空。我将在整个项目生命周期完结、代码完全跑通后，根据实际的开发心路历程，在 Stage 8 阶段配合 ai/reflection.md 独立填写此处的总结陈词。)