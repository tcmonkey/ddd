# Java DDD 开发规范

版本：1.0。适用于使用 `ddd` 参考工程生成的 Java Maven 项目；这是已确认的项目约定，不宣称是所有 DDD 项目的通用标准。

## 1. 使用方式与规则优先级

- 本文件是当前生效规范。输入材料清单、决策记录、技术方案版本日志是历史证据，不应直接按历史条目生成代码。
- “必须/禁止”是本模板约束；“按需”由业务方案决定；“样例限定”不得机械复制到生产。
- 新项目使用本模板时默认采用这些规则。已有项目的明确规范或用户后续确认的变更优先；发生冲突须说明并记录，不能静默覆盖。
- 单条规则的例外必须注明规则 ID、原因、影响和验证方式。不得通过全局关闭 Checkstyle 代替解决违规。
- 检查标记：**C**＝Checkstyle 已实现；**P**＝Checkstyle 只检查部分语法特征，其余需要评审；**R**＝设计、AI/人工评审或专项测试，本轮未自动实现。

## 2. 模块与包结构

| ID | 生效规则 | 检查 |
|---|---|---|
| MOD-001 | 必须使用 Maven 聚合根和独立层级 module，目录/artifactId 为 `<项目名>-<层名>`；不得将层级全部折叠成单个 src 下的 package。 | R |
| MOD-002 | 标准层级为 common、client、model、domain、application、infrastructure、adaptor、start；根据实际需求裁剪，不创建无用途的空层。 | R |
| MOD-003 | common 放统一 Result、ErrorCode、BaseException，不依赖业务、Spring、HTTP、数据库或第三方协议。 | R |
| MOD-004 | client 只放外部 Request/Response 与按需 RPC 接口，可依赖 common，禁止依赖 model 与业务实现。 | P：禁止 model 导入，完整依赖图需评审 |
| MOD-005 | model 是内部稳定 DO 共享层，domain/application/adaptor/infrastructure 可依赖，client 禁止依赖；不放持久化映射、协议模型和领域行为。 | R |
| MOD-006 | domain 只依赖 common/model/JDK，禁止 Spring、Jakarta、MyBatis 等框架。 | P：禁止常见框架 import；POM、全限定类型/传递依赖需评审 |
| MOD-007 | application 依赖 domain/model/common；infrastructure 实现 domain 仓储端口；adaptor 依赖 application/client/model/common。start 只负责启动和装配，业务模块禁止反向依赖 start。 | R |
| MOD-008 | 同一 adaptor module 区分 input/output：input 放协议入口及 assembler，output 放外部能力实现、converter 和私有第三方模型。 | R |

按业务子域组织包，推荐骨架：

```text
common       error / result
client       <业务>.request / response / 按需 RPC 接口
model        <业务>.XXDO
domain       annotation / <业务>.exception
             <业务>.model.aggregate / entity / value / param
             <业务>.repository / service
application  exception / <业务>.command / result / assembler / service / adaptor
infrastructure exception / 基础仓储与 Mapper
             <业务>.mysql.mapper / pojo / repository
adaptor      exception / common
             <业务>.input.assembler / 按需 listener、scheduler
             <业务>.output.converter / model
start        Application / config / resources / 按需 aop
```

包按真实用途建立；application 不创建无用途的通用 param 包，真正独立的外部能力参数归其 adaptor 包。

## 3. 公开入口与转换责任

| ID / 边界 | 标准签名 | 检查 |
|---|---|---|
| SIG-001 Controller 请求体 | `public Result<XxResponse> xx(@Valid @RequestBody XxRequest xxRequest)` | P：注解、类型、命名形式自动；简单签名的类型/变量前缀由正则校验，复杂写法需评审 |
| SIG-002 Application | `public Result<XxResult> xx(XxCommand xxCommand)` | P：类型、命名形式自动；简单签名前缀自动，复杂写法需评审 |
| SIG-003 DomainService | `public Result<XxDO> xx(XxParam param)` | C |
| SIG-004 OutAdaptor | `public Result<XxDO> xx(XxCommand xxCommand)` | P：接口/入口类型与命名形式；简单签名前缀自动，复杂写法需评审 |
| SIG-005 | 服务入口即使只有一个基础字段，也必须封装为 Command/Param；仓储是例外。 | C：以上入口 |
| SIG-006 | Controller 的 GET、path/query、未来 RPC 按协议接收，进入 Application 前组装 Command；不强行变为 POST，请求注解不进入领域/应用层。 | P：请求体注解；协议选择需评审 |
| SIG-007 | DomainService 规则不套到 Entity、Aggregate、Repository、私有辅助方法或 HTTP 全局异常处理器。OutAdaptor converter 与第三方客户端辅助方法不套统一 Result。 | C：按包/注解区分，范围见第 10 节 |
| MAP-001 | Controller assembler：协议 Request/path/query → Command，Application Result → Response。Controller 不处理 DO/Aggregate/Entity/Value。 | R |
| MAP-002 | Application assembler：Command → DomainService Param，内部 DO/仓储 Aggregate → Application Result；Application Service 不手写字段映射。 | R |
| MAP-003 | OutAdaptor 接口放 Application，由 output 实现；直接接收调用所需的 Application Command，converter 转第三方请求，第三方响应转换为内部 DO。 | P：输入/输出签名；职责需评审 |
| MAP-004 | 无独立语义时禁止为了形式一致复制 Command 为额外 Param；有真正独立语义时类型归 Application adaptor，需记录对默认签名的例外。 | R |
| MAP-005 | 返回链为 Result<DO> → Result<Application 的 XxResult> → Result<Response>；结构相同也要转换，不能让 Controller 直接引用内部领域模型。 | P：输出后缀；真实类型依赖/转换需评审 |

## 4. 领域模型与业务模式

| ID | 生效规则 | 检查 |
|---|---|---|
| DDD-001 | Aggregate 是场景一致性容器，业务状态只由其中的 Entity 持有；Aggregate 不重复保存 id、状态、版本等标量状态，不注入 Spring/Repository。复杂场景可持有多个 Entity/实体集合。 | P：模型后缀；容器字段及注入依赖需评审 |
| DDD-002 | Entity 内聚自身状态、初始化、校验、计算和变更；复杂属性用 Value 表达不变量。Aggregate 用语义方法委托 Entity，不进行属性式状态修改。 | R |
| DDD-003 | DomainService 通过 Aggregate 语义方法协作，不用 aggregate.entity().xxx() 编排状态；仓储恢复/保存和只读 assembler 可访问 Entity 快照。 | R |
| DDD-004 | 写入 Param 只持有输入 Aggregate；Application assembler 将命令原始数据交给 Aggregate 工厂，由 Entity 构造 Value/子实体。Application 不创建或计算 Value。 | R |
| DDD-005 | DomainService 根据用例决定读取、补齐、确认、筛选、计算与保存；最后用 Repository 保存完整 Aggregate。纯新增可直接初始化后保存，不强制先查询；幂等更新按实际需求加载已有聚合。 | R |
| DDD-006 | Repository 依赖由 DomainService 的构造器传入，不注入 Aggregate。框架装配在 start，领域对象保持普通 Java 对象。 | P：禁止字段 Autowired/Resource；依赖设计需评审 |
| DDD-007 | 写模式：Application → DomainService → Aggregate/Entity → Repository，状态变化归 Entity。域内普通读：Application → Repository → Aggregate → assembler。 | R |
| DDD-008 | 外部普通读：Application → OutAdaptor；规则+计算：DomainService 加载规则 Aggregate，以模型行为完成计算；纯计算：DomainService，无虚构聚合/持久化依赖。 | R |
| DDD-009 | 真实需求只保留所需模式；本模板的五条链路用于覆盖四种模式及读模式的内/外分支，不要求真实项目全部复制。 | R |

## 5. 仓储与数据访问

| ID | 生效规则 | 检查 |
|---|---|---|
| REPO-001 | domain 声明仓储接口，infrastructure 实现；按 ID/IDs 查询、删除可接受基础标识或标识集合，不使用 DomainService Param。 | R |
| REPO-002 | 单个查询返回 Aggregate，批量查询返回 Aggregate 集合，不返回 Optional；新增/修改接收完整 Aggregate，保存返回 Boolean，修改按标识与版本定位。 | P：禁止 Optional；聚合类型/行为需评审 |
| REPO-003 | 查询缺失语义由业务明确：当前主聚合返回可首次写入的未持久化空聚合；规则缺失返回领域错误。真实项目不能无条件套用“查不到就创建”。 | R、样例限定 |
| REPO-004 | 复用 MyBatis-Plus CrudRepository，项目 BaseRepository 提供公共能力；Mapper 继承 BaseMapper 并由 start 扫描。框架基类已有 Mapper 注入时禁止重复声明注入或复杂继承。 | R |
| REPO-005 | 禁止自定义 SQL：不写 XML SQL、SQL 注解、Provider、拼接原生 SQL；使用 MyBatis-Plus CRUD/安全条件构造器，多表关联按业务编排。不得使用 last/apply 等方法绕过此约束。 | P：禁止 SQL/Provider 注解，其余需评审 |
| REPO-006 | XXPO 仅为 infrastructure 数据库表映射，Repository 负责 PO ↔ Aggregate，PO 不进入 domain/application/adaptor/client。 | P：类型命名；完整跨层引用需评审 |
| REPO-007 | 分页接收 domain 自有页码/页大小/筛选/排序对象，返回域内总数/页码/页大小/Aggregate 列表；infrastructure 双向转换 MyBatis-Plus Page/IPage，框架模型不能越界。 | R，当前无分页实现 |
| REPO-008 | 普通分页由 Application 调 Repository 并转换应用结果；业务过滤由 DomainService 编排，仍用域内分页契约。影响总数的过滤必须先于分页，排序白名单与页大小必须校验。 | R、专项测试 |
| REPO-009 | schema、索引、唯一键、事务、幂等和乐观锁按真实业务设计；当前单表 JSON 快照不是生产数据库范式要求，不复制无用第二张表或内存仓储。 | R、样例限定 |

## 6. 注入、结果、异常与日志

| ID | 生效规则 | 检查 |
|---|---|---|
| DI-001 | 自有 Spring 组件统一单一构造器注入，依赖字段 final；禁止字段 Autowired/Resource。domain 使用自定义 DomainService 标记，start 定向扫描并构造装配。 | P：字段注解禁止；构造器数量/扫描需评审 |
| DI-002 | 无状态 assembler/converter 直接用 Component 扫描，不为已有扫描能力手动写重复 Bean；不创建无真实职责的 Configuration。 | R |
| DI-003 | 当前取时间直接 Instant.now，不引入仅转发当前时间的 Clock Bean；需固定时钟测试、多时区策略时经方案确认后引入。 | R、按需 |
| ERR-001 | common 统一 Result<T>；success/code/message/data 语义一致，不在 client/domain 各写一套 Result。XxResult 是应用业务 DTO，不是公共包装器。 | P：入口类型；统一实现与语义需评审 |
| ERR-002 | DomainService/OutAdaptor 公共入口返回 Result<XXDO>；错误码用 Domain/Application/Infrastructure/AdaptorErrorCode 枚举，实现 common ErrorCode。禁止 ExternalErrorCode 等歧义模块名。 | P：返回签名；枚举与错误映射需评审 |
| ERR-003 | common BaseException 统一携带内部 ErrorCode；DomainException、InfrastructureException、AdaptorException 按实际私有抛出需求定义，Application 不为凑层级创建无用途异常。 | R |
| ERR-004 | DomainService/OutAdaptor 主调用入口捕获预期及未预期异常，内部私有方法/Entity/Aggregate 不重复 catch/log；Application 判断 Result，并按方案保留或转换错误码。不得 catch 后返回成功、吞异常或暴露原始技术堆栈。 | R |
| ERR-005 | 第三方原始响应/错误/堆栈留在可追溯日志，向上返回友好内部错误；不得在多层重复打印同一异常。HTTP 全局异常处理器仅兜底协议校验与未处理异常。 | R |
| LOG-001 | start 提供 logback-spring.xml：UTF-8、时间、线程、级别、类、traceId 字段；按部署需要启用滚动文件。敏感信息不得原样记录。 | R |
| LOG-002 | 模板仅提供 traceId 输出占位，尚无 MDC 填充/监控指标；真实项目必须按方案补齐链路、关键业务日志和观测指标，不能把配置文件等同完整可观测能力。 | R、生产适配 |

## 7. 命名与注释

| ID | 生效规则 | 检查 |
|---|---|---|
| NAM-001 | 类型 UpperCamelCase；方法/普通字段/参数/局部变量 lowerCamelCase；静态常量 UPPER_SNAKE_CASE。真实类型使用领域语言替换 Ddd，不保留会员积分等无关样例语义。启动类固定 Application，公共包装器固定 Result。 | P：大小写；真实业务命名需评审 |
| NAM-002 | 对应包类型后缀：Aggregate、Entity（不用 Entiry）、Value、Param、Command、Request、Response；Application Service 为 XxApplication，DomainService 为 XxDomainService；DO/PO 为 XxDO/XxPO。 | P：已检查前八类后缀和服务名；DO/PO需评审 |
| NAM-003 | Controller 的 Request、Application/OutAdaptor 的 Command 参数按完整类型 lowerCamelCase；DomainService 输入统一 param；OutAdaptor 接口和实现参数名一致。 | P：参数命名形式自动；简单签名前缀一致自动；复杂类型及接口/实现关联需评审 |
| NAM-004 | 方法使用 create/query/write/cancel/calculate 等明确业务动作；不能机械统一 execute。不同业务动作不强制同名，同一职责上下层尽量一致。 | P：禁止入口 execute；业务动词语义需评审 |
| DOC-001 | 所有公开声明的类型、字段/枚举常量和公开方法（含公开构造器、手写 getter/setter、Override）必须写多行中文 Javadoc，每个 Javadoc 必须有非空 author；默认 AIGenerator，真实项目可用实际维护者。 | P：缺失、中文存在、多行、author 自动；语义质量需评审 |
| DOC-002 | 方法逐项 param，非 void 有 return，泛型参数有类型参数说明；record 的组件在类型 Javadoc 逐项 param。已声明私有方法的 Javadoc 也必须匹配参数/返回值。 | C：已有文档标签与公开缺失；私有文档是否必须存在需评审 |
| DOC-003 | PO/Entity/Value 字段说明业务语义、映射或不变量；Controller/RPC 的契约说明用途、输入、输出、错误行为，不依靠实现类文档替代接口契约。 | P：字段文档存在；实际完整性需评审 |
| DOC-004 | Javadoc 内容变更与参数名同步；接口约定不能因实现已注释而省略。自动生成的访问器不用重复声明，只检查实际源码。 | C：源码声明 |

## 8. 代码格式与版本管理

| ID | 生效规则 | 检查 |
|---|---|---|
| FMT-001 | UTF-8、无 Tab/行尾空白、文件尾换行，单行上限 120 字符，import 和字符串不自动豁免。缩进 4 空格，续行保持清晰。 | P：编码声明、Tab/空白/尾换行/行宽自动；缩进需评审 |
| FMT-002 | 每行一个 import，禁止星号、冗余/未使用 import；按 JDK/第三方/项目内/静态导入分组排序。 | P：星号/冗余/未使用与多语句自动；分组排序需评审 |
| FMT-003 | 方法/构造器签名一行放得下时不提前换行，只有超过截止线才换行；record 组件按可读性布局。 | P：最大行宽自动；无必要换行需评审 |
| FMT-004 | 方法体/PO getter/setter 不压成单行，多语句分行；控制结构有大括号，操作符和标点空格一致。 | C：相应语法规则 |
| FMT-005 | 多职责流程显式按组装→调用→解析/转换拆为局部变量，用编号行内注释；禁止嵌套一行跨层调用。单一访问/纯计算无需凑步骤。 | P：同一行多语句/方法体自动；嵌套调用和编号语义需评审 |
| MAV-001 | 根 POM 集中管理依赖/BOM/插件版本；module dependency/plugin 禁止 version，唯独 parent.version 是 Maven 必需字段。各 dependency/plugin 使用多行 XML。 | R：Checkstyle 不解析 POM |
| MAV-002 | 基础平台使用根 Spring Boot parent 或经批准的 BOM 方案；新项目重新确认版本，不把当前 3.3.12/JDK17 当作永久生产标准。 | R |
| MAV-003 | 根 POM 的 build/plugins 实际声明并绑定 Checkstyle check 到 validate，子模块继承；只写 pluginManagement 不会触发检查。插件与引擎版本只在根管理。 | 安装器＋Maven 构建验证 |
| MAV-004 | 项目根必须携带 checkstyle.xml，独立于 Skill 安装目录；编译/测试/打包默认先检查主源码，违规构建失败。禁止在默认构建中关闭检查。 | Maven 构建验证 |

## 9. 生产适配与交付

| ID | 生效规则 | 检查 |
|---|---|---|
| PROD-001 | H2/org.h2.Driver 仅为本地可重复运行；生产连接须替换目标数据库官方驱动及配置，完善迁移/约束，凭据不入库。 | R、生产适配 |
| PROD-002 | 外部模拟响应仅为结构样例，生产必须接真实调用、超时与错误映射；不复制假结果当真实接口。 | R、生产适配 |
| PROD-003 | 事务、并发、幂等、授权、性能、兼容和回滚按需求确认，不从最小样例推断已完成。 | R、专项测试 |
| DEL-001 | 人类文档使用 Markdown、编号中文名，输入在 AI/input、输出在 AI/output；SKILL.md/checkstyle.xml/pom.xml/机器状态等协议文件保留标准名。 | R |
| DEL-002 | 变更同步技术方案、开发计划、自测、追踪矩阵、README 与交接；保持开发自测→代码审查→独立功能/性能测试的职责分离。 | R |
| DEL-003 | 发现规范与实际代码矛盾时主动指出：能在已授权范围安全修正则修正；涉及业务/架构新决策则记录待确认。不等待用户逐类发现。 | R |

## 10. 自动检查的真实边界与使用

根目录执行：

```bash
mvn validate
mvn clean compile
mvn clean test
mvn clean package
```

上述生命周期命令先执行 Checkstyle；扫描所有继承根 POM 的 module 的 `src/main/java`，本版不扫描测试源码、生成源码、scripts、POM、SQL/XML 或 Markdown。JDK 17 可用，插件 3.6.0 / Checkstyle 引擎 10.26.1 在根集中固定。子模块单独操作推荐仍从根使用 `mvn -pl <module> -am compile`；不要以脱离聚合根的执行方式另找一份配置。

Checkstyle 实现采用包结构/注解和单文件 AST，不做 Java 类型解析：
- 后缀、四层输入/输出、参数名形式检查覆盖推荐包中的简单类型名；Request/Command 的简单入口签名由 NAM-TYPE-PARAM 校验变量前缀与类型前缀对应；复杂全限定写法、别名、跨文件接口继承不是已证明的完整类型保障。
- 框架 import、字段注入及 SQL 注解检查只是风险特征，不验证 Maven 依赖图、所有原生 SQL 调用或真实 DDD 内聚。
- 中文存在、作者标签、文档完整性不代表描述正确；业务动作、编号流程、领域边界、事务/日志仍需评审。
- 接口与实现类的公开方法 Javadoc 统一由 Maven Checkstyle 门禁检查，不再维护独立接口诊断脚本。
- 不得声称 Checkstyle 通过等于架构、功能、性能或生产验收通过；其余 R/P 规则进入代码审查及专项测试。

新项目使用 solo-delivery 的 Java DDD 模板时，AI 在根 POM 建好、正式编码前自动执行 Skill 的 `scripts/install_java_ddd_checks.py --project <项目根>`，生成根 checkstyle.xml、继承执行的 Maven 配置和本规范快照 `AI/output/19 Java DDD开发规范.md`，无需用户每次重复指定。没有执行能力的宿主按同一资源生成文件，不能谎称构建已执行。

## 11. 历史约定收敛结论

| 历史分歧 | 当前唯一生效规则 | 来源 |
|---|---|---|
| 单个 src 下按层分包 / 多 Maven module | 多 module，项目名-层名 | SRC-008、009 |
| 会员积分 / 公共占位 | 仅 Ddd 占位，真实项目替换领域语言 | SRC-013 |
| Entiry 拼写 | Entity | SRC-010 的已落实约定 |
| 构造器 / 字段 Autowired / 构造器 | 最终单一构造器；框架基类已有注入不重复 | SRC-024 覆盖 017 |
| Aggregate 持有标量或注入仓储 / Entity 容器 | Aggregate 只容纳 Entity，DomainService 构造器持有仓储 | SRC-024、025、027 |
| 内存仓储 / 正式仓储结构 | 正式 MyBatis-Plus 仓储，即使规则表为空 | SRC-020、021 |
| Clock 与手工转换器 Bean / 按需配置 | 默认扫描 Component，直接取当前时间 | SRC-022 |
| Domain 专属返回 / 全层共同包装 | common Result，模块枚举错误码，Domain/Out 返回 DO | SRC-026、029 |
| OutAdaptor 再加重复 Param / 直接 Command | 无独立语义直接 Application Command | SRC-031 覆盖 030 的冗余转换 |
| Repository 使用领域读取 Param / 标识类型 | Repository CRUD 标识例外；分页自有域内对象 | SRC-033 |
| 通用 request/command/execute / 具体参数与动作 | 四层签名及完整类型变量名，明确业务动作 | SRC-034 |
| 手动接口 Javadoc 检查 / 构建约束 | Checkstyle 生命周期门禁；独立接口检查脚本已移除 | SRC-035、SRC-036 |

不把历史版本日志中的旧选择当作新项目规则。规则变更应更新本文件版本、对应 checkstyle.xml、安装资源和项目快照，并重新验证正向构建与反向违规用例。

实现参考：[Maven 插件生命周期接入](https://maven.apache.org/plugins/maven-checkstyle-plugin/usage.html)、[Checkstyle MatchXpath](https://checkstyle.org/checks/coding/matchxpath.html)、[Javadoc 方法检查](https://checkstyle.org/checks/javadoc/javadocmethod.html)。这些资料用于解释检查能力；本项目的业务架构与偏好来自用户确认，不由第三方文档替代决定。
