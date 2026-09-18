# Java DDD 开发规范

版本：1.10。作为 solo 的 Java DDD 默认开发约定，适用于采用 Java/Spring/Maven 与 DDD 分层的项目，不绑定具体业务应用。`ddd` 是随技能提供的参考工程，不是适用项目名单；其他技术栈按其实际架构采用相关通用原则。已有项目的明确规范和用户决定优先，本文件不宣称是行业统一标准。

## 1. 使用方式与规则优先级

- 本文件是当前生效规范。输入材料清单、决策记录、技术方案版本日志是历史证据，不应直接按历史条目生成代码。
- 公共规范记录可复用规则、适用条件和验证边界；项目名称、项目内SRC编号、整改来源与执行证据留在各项目的交付记录，不能作为通用规则的适用范围或隐含依赖。
- “必须/禁止”是本模板约束；“按需”由业务方案决定；“样例限定”不得机械复制到生产。
- 新项目使用本模板时默认采用这些规则。已有项目的明确规范或用户后续确认的变更优先；发生冲突须说明并记录，不能静默覆盖。
- 单条规则的例外必须注明规则 ID、原因、影响和验证方式。不得通过全局关闭 Checkstyle 代替解决违规。
- 检查标记：**C**＝Checkstyle 已实现；**P**＝自动门禁只检查部分语法/结构，其余需要评审；**R**＝设计、AI/人工评审或专项测试，不由当前静态门禁完整实现。

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
| MOD-010 | 生成有前端的项目时，默认以用户项目名为服务端目录，自动创建同级`<项目名>-app`为独立前端项目，拥有自己的package.json、lockfile、源码、构建与README；禁止默认嵌套到服务端frontend/web目录。用户明确其他布局或已有仓库约定优先；目标已存在先检查保留，不覆盖。同步启动脚本、代理、文档和项目目录引用；不把服务端凭据复制到前端。 | R：目录与独立构建验证 |
| MOD-009 | start 的 POM 是本服务的显式装配清单，直接声明实际随服务运行的内部模块，不仅依靠传递依赖。完整模板列出 common、client、model、domain、application、infrastructure、adaptor；真实项目按服务边界裁剪，不引入无用途、其他服务或仅工具用途的模块。测试专用依赖使用 test scope，运行专用依赖按需使用 runtime scope；所有依赖版本仍由根 POM 管理。业务模块不得反向依赖 start。 | R：核对 POM、有效依赖树和运行验证，Checkstyle 不检查 |

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
| MAP-001 | Controller assembler：协议 Request/path/query → Command，Application Result → Response。Controller 不处理 DO/Aggregate/Entity/Value，不直接构造 Command/Response，也不借私有转换方法绕过 assembler。 | P：有限DTO构造位置扫描；职责及全部字段映射仍需R |
| MAP-002 | Application assembler：Command → DomainService Param，内部 DO/仓储 Aggregate → Application Result；Application Service/流程不手写字段映射，不直接构造 Command/Param/XxResult。 | P：有限DTO构造位置扫描；整体映射及返回含义仍需R |
| MAP-003 | OutAdaptor 接口放 Application，由 output 实现；直接接收调用所需的 Application Command，converter 转第三方请求，第三方响应转换为内部 DO；OutAdaptor负责IO及失败边界，不直接拼装内部 DO。 | P：签名及有限DO构造位置扫描；SDK请求字段/职责仍需R |
| MAP-004 | 无独立语义时禁止为了形式一致复制 Command 为额外 Param；有真正独立语义时类型归 Application adaptor，需记录对默认签名的例外。 | R |
| MAP-005 | 返回链为 Result<DO> → Result<Application 的 XxResult> → Result<Response>；结构相同也要转换，不能让 Controller 直接引用内部领域模型。 | P：输出后缀；真实类型依赖/转换需评审 |
| MAP-006 | assembler/converter按用途接收完整Request/Command/持久快照/SDK响应及少量必要上下文，提供有明确语义的转换方法；禁止仅将原长标量参数串搬进转换器。动作解释、协议默认值、字段投影和第三方协议预设由所属转换职责承担，业务状态/不变量归领域，部署配置归配置源；不为消灭字面量创建全局常量袋、无用途DTO或重复包装。完成时盘点所有类的转换点、默认值和异常创建位置，检查正常/失败行为，清理废弃实现。 | R：逐转换点及调用点复核；自动门禁不能证明完整对象设计 |

## 4. 领域模型与业务模式

| ID | 生效规则 | 检查 |
|---|---|---|
| DDD-001 | Aggregate 是场景一致性容器，业务状态只由其中的 Entity 持有；Aggregate 不重复保存 id、状态、版本等标量状态，不注入 Spring/Repository。复杂场景可持有多个 Entity/实体集合。 | P：模型后缀；容器字段及注入依赖需评审 |
| DDD-002 | Entity 内聚自身状态、初始化、校验、计算和变更；复杂属性用 Value 表达不变量。Aggregate 用语义方法委托 Entity，不进行属性式状态修改。按真实状态转换建模，不把校验散落到应用长流程，不以空容器或无职责包装充当面向对象。 | R：对象职责、不变量和转换反例复核 |
| DDD-003 | DomainService 通过 Aggregate 语义方法协作，不用 aggregate.entity().xxx() 编排状态；仓储恢复/保存和只读 assembler 可访问 Entity 快照。主流程负责协调，实体负责自身规则。 | P：QUALITY-DOMAIN 禁止推荐 domain/service 包调用 entity()；完整语义及跨文件别名仍需复核 |
| DDD-004 | 写入 Param 只持有输入 Aggregate；Application assembler 将命令原始数据交给 Aggregate 工厂，由 Entity 构造 Value/子实体。Application 不创建或计算 Value，不在编排方法拼装 Entity 全字段快照或固定状态；通过聚合创建/变更语义表达业务意图。仓储/只读映射不混入业务状态规则。 | P：QUALITY-DOMAIN 禁止 application 非 assembler 包显式 new *Entity；Value、工厂、别名与完整职责仍需复核 |
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
| REPO-003 | 查询缺失语义由业务明确：参考样例的主聚合返回可首次写入的未持久化空聚合；规则仓储返回缺失快照，由领域服务判断并返回领域错误。真实项目不能无条件套用“查不到就创建”。 | R、样例限定 |
| REPO-004 | 复用 MyBatis-Plus CrudRepository，项目 BaseRepository 提供公共能力；Mapper 继承 BaseMapper 并由 start 扫描。框架基类已有 Mapper 注入时禁止重复声明注入或复杂继承。 | R |
| REPO-005 | 禁止自定义 SQL：不写 XML SQL、SQL 注解、Provider、拼接原生 SQL；使用 MyBatis-Plus CRUD/安全条件构造器，多表关联按业务编排。不得使用 last/apply 等方法绕过此约束。 | P：禁止 SQL/Provider 注解，其余需评审 |
| REPO-006 | XXPO 仅为 infrastructure 数据库表映射，Repository 负责 PO ↔ Aggregate，PO 不进入 domain/application/adaptor/client。 | P：类型命名；完整跨层引用需评审 |
| REPO-007 | 分页接收 domain 自有页码/页大小/筛选/排序对象，返回域内总数/页码/页大小/Aggregate 列表；infrastructure 双向转换 MyBatis-Plus Page/IPage，框架模型不能越界。 | R，当前无分页实现 |
| REPO-008 | 普通分页由 Application 调 Repository 并转换应用结果；业务过滤由 DomainService 编排，仍用域内分页契约。影响总数的过滤必须先于分页，排序白名单与页大小必须校验。 | R、专项测试 |
| REPO-009 | schema、索引、唯一键、事务、幂等和乐观锁按真实业务设计；当前单表 JSON 快照不是生产数据库范式要求，不复制无用第二张表或内存仓储。 | R、样例限定 |

## 6. 注入、结果、异常与日志

| ID | 生效规则 | 检查 |
|---|---|---|
| DI-001 | 自有 Spring 组件统一单一构造器注入，依赖字段 final；禁止字段 Autowired/Resource。domain 使用自定义 DomainService 标记，start 定向扫描并构造装配。纯依赖装配构造器及其注入字段不写重复注释，见 DOC-005/006。 | P：字段注解禁止；构造器数量/扫描需评审 |
| DI-002 | 无状态 assembler/converter 直接用 Component 扫描，不为已有扫描能力手动写重复 Bean；不创建无真实职责的 Configuration。 | R |
| DI-003 | 当前取时间直接 Instant.now，不引入仅转发当前时间的 Clock Bean；需固定时钟测试、多时区策略时经方案确认后引入。 | R、按需 |
| ERR-001 | common 统一 Result<T>；success/code/message/data 语义一致，不在 client/domain 各写一套 Result。XxResult 是应用业务 DTO，不是公共包装器。 | P：入口类型；统一实现与语义需评审 |
| ERR-002 | DomainService/OutAdaptor 公共入口返回 Result<XXDO>；错误码用 Domain/Application/Infrastructure/AdaptorErrorCode 枚举，实现 common ErrorCode。禁止 ExternalErrorCode 等歧义模块名。 | P：返回签名；枚举与错误映射需评审 |
| ERR-003 | common BaseException 统一携带内部 ErrorCode；DomainException、ApplicationException、InfrastructureException、AdaptorException 按实际本层失败需求定义；Application无本层失败用途时不为凑层级创建异常，有本层校验/编排失败时使用Application自己的分类。 | R |
| ERR-004 | Controller/RPC/输入端口、Application Service、DomainService及OutAdaptor实现的每个主调用入口，必须在本方法中用try-catch覆盖参数转换、校验、调用和结果转换；捕获已分类业务异常及Exception，不向上抛出。接口声明无方法体不写catch，实现类必须写；内部Entity/Aggregate/仓储/私有协作可抛异常，但由本层主入口捕获，不能以全局异常处理器或上层catch替代。 | P：实体实现入口完整外层try/catch语法已检查；错误分类/覆盖语义需评审 |
| ERR-006 | 对外及四层主入口禁止声明throws、catch后重抛、捕获后返回成功或泄漏异常消息/堆栈；失败统一Result.failure，保留或明确转换稳定错误分类。HTTP失败按协议设置状态，不能为了禁止抛出而全部变成200。SSE/流式协议已提交时使用安全错误帧及关闭连接，入口仍须捕获；方法进入前的绑定/BeanValidation由协议兜底处理。 | P：throws及catch重抛已检查；Result/状态/流终止需专项验证 |
| ERR-007 | 捕获与事务边界必须相容：应用入口的catch包住TransactionTemplate执行及commit，内部异常先触发回滚再转换Result；事务内部收到失败Result须明确rollbackOnly或转为内部异常中断。禁止catch吞掉错误使部分写入提交，禁止在公开入口依靠注解代理于方法返回后才捕获提交失败。 | R：事务失败/提交失败回归验证 |
| ERR-008 | 主动创建异常必须使用所属模块异常或标准参数校验异常，不在Controller/过滤器/OutAdaptor/Application/Infrastructure创建DomainException，也不主动创建其他模块异常。下层已经分类的BaseException或失败Result保留稳定分类，不因模块隔离强制改写成未知失败；HTTP输入与路由校验使用本层Adaptor分类，领域不变量由领域自己定义。仓储返回缺失快照时，是否业务失败由领域决定；保留端口语义并更新相关调用与回归。 | P：已知四类模块异常创建位置；标准校验、分类保持、行为归属仍需R |
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
| DOC-001 | 所有公开声明的类型、字段/枚举常量（构造器注入的依赖字段按 DOC-006 例外）和公开方法（含业务构造器、手写 getter/setter、Override；纯注入构造器按 DOC-005 例外）必须写多行中文 Javadoc，每个 Javadoc 必须有非空 author；默认 AIGenerator，真实项目可用实际维护者。 | P：缺失、中文存在、多行、author 自动；语义质量需评审 |
| DOC-002 | 方法逐项 param，非 void 有 return，泛型参数有类型参数说明；record 的组件在类型 Javadoc 逐项 param。已声明私有方法的 Javadoc 也必须匹配参数/返回值。 | C：已有文档标签与公开缺失；私有文档是否必须存在需评审 |
| DOC-003 | PO/Entity/Value 字段说明业务语义、映射或不变量；Controller/RPC 的契约说明用途、输入、输出、错误行为，不依靠实现类文档替代接口契约。 | P：字段文档存在；实际完整性需评审 |
| DOC-004 | Javadoc 内容变更与参数名同步；接口约定不能因实现已注释而省略。自动生成的访问器不用重复声明，只检查实际源码。 | C：源码声明 |
| DOC-005 | 被 Spring 扫描的组件（含 start 定向扫描的 DomainService），单一构造器仅将输入依赖赋给 final 字段时，不写重复的构造器 Javadoc/行内注释；类型仍需说明，注入依赖字段按 DOC-006 处理。构造器包含校验、初始化、计算、转换或其他行为则必须写 Javadoc，普通业务对象/异常构造器不在例外内。公开业务方法、Controller 入口、RPC/Repository 接口注释不能省略。 | P：窄范围构造器 AST 豁免；字段/参数对应关系与真实扫描需评审 |
| DOC-006 | 被 Spring 扫描类中，通过构造器接收并保存的注入依赖字段不写重复 Javadoc/行内注释，以清晰类型名和字段名表达职责；不删除字段或 final，也不改变构造器注入方式。业务状态、配置语义字段、PO/Entity/Value 属性、常量与日志字段仍保留说明；不能因为类被扫描就豁免全部字段。复杂构造器中的注入依赖仍按此语义判断，但构造器行为注释按 DOC-005 保留。此约定用于 solo 后续生成的 Spring 项目，不局限 ddd 参考工程。 | P：纯装配组件的未初始化非静态 final 字段窄范围豁免；复杂构造器、真实扫描/依赖语义需评审与定向适配 |

## 8. 代码格式与版本管理

| ID | 生效规则 | 检查 |
|---|---|---|
| FMT-001 | UTF-8、无 Tab/行尾空白、文件尾换行，单行上限 120 字符，import 和字符串不自动豁免。缩进 4 空格，续行保持清晰。 | P：编码声明、Tab/空白/尾换行/行宽自动；缩进需评审 |
| FMT-002 | 每行一个 import，禁止星号、冗余/未使用 import；按 JDK/第三方/项目内/静态导入分组排序。 | P：星号/冗余/未使用与多语句自动；分组排序需评审 |
| FMT-003 | 方法/构造器签名一行放得下时不提前换行，只有超过截止线才换行；record 组件按可读性布局。 | P：最大行宽自动；无必要换行需评审 |
| FMT-004 | 方法体/PO getter/setter 不压成单行，多语句分行；控制结构有大括号，操作符和标点空格一致。 | C：相应语法规则 |
| FMT-005 | 所有业务逻辑方法，包括私有辅助方法、事务/异步回调和有业务行为的构造器，按实际职责写中文行内编号步骤，如“// 1. 核验业务对象归属与当前版本”。独立流程从1连续编号；说明做什么、业务约束或为何这样做，禁止“处理数据/第二步”等占位或错误描述。按逻辑阶段分组，不逐行翻译赋值、不为简单getter/纯装配凑步骤。长方法先划分对象职责和有意义的阶段，禁止仅补注释、无意义抽方法或无节制加类；跨层调用、结果校验和转换显式分开。 | P：DOC-007 编号/规模扫描；含义、完整覆盖及内聚必须逐方法复核 |
| MAV-001 | 根 POM 集中管理依赖/BOM/插件版本；module dependency/plugin 禁止 version。Maven 3 子模块保留 parent.version，但采用 MAV-005 的 ${revision}，不重复硬编码项目版本。各 dependency/plugin 使用多行 XML。 | R：Checkstyle 不解析 POM |
| MAV-002 | 基础平台使用根 Spring Boot parent 或经批准的 BOM 方案；新项目重新确认版本，不把当前 3.3.12/JDK17 当作永久生产标准。 | R |
| MAV-003 | 根 POM 的 build/plugins 实际声明并绑定 Checkstyle check 到 validate，子模块继承；只写 pluginManagement 不会触发检查。插件与引擎版本只在根管理。 | 安装器＋Maven 构建验证 |
| MAV-004 | 项目根必须携带 checkstyle.xml，独立于 Skill 安装目录；编译/测试/打包默认先检查主源码，违规构建失败。禁止在默认构建中关闭检查。 | Maven 构建验证 |
| MAV-005 | Maven 3 同版本多模块工程以根 properties/revision 单点声明项目版本，根 project.version 与各子模块 parent.version 均为 ${revision}；子模块不重复声明 revision 或自身 version。根管理内部模块依赖仍用 ${project.version}，不将其用于 parent.version。允许命令行 -Drevision 覆盖；外部 Spring Boot parent 的版本保持根集中声明。 | R：POM、默认/覆盖版本构建验证 |
| MAV-006 | 根 build/plugins 声明 flatten-maven-plugin 并固定版本，子模块继承；updatePomFile=true、flattenMode=resolveCiFriendliesOnly，flatten 绑定 process-resources、clean 绑定 clean。Maven 3 install/deploy 使用已解析 CI 版本占位符的 POM，源码 POM 不被改写；.flattened-pom.xml 加入 Git 忽略且不进入 Skill 快照。发布适配须验证安装后独立消费者，无须发布到远程验证。 | R：安装/消费及 clean 行为验证；Checkstyle 不检查 |
| MAV-007 | 源码、配置、文档仍为 UTF-8；Checkstyle 自定义诊断统一 ASCII 英文并保留稳定规则 ID，内置诊断固定 en/US，减少不同宿主/IDE 控制台解码差异。此约定只作用于规范门禁，不改中文 Javadoc、业务错误信息或日志；inputEncoding/charset 只是输入编码，不是控制台输出编码修复。门禁变更须验证正常 UTF-8 与 US-ASCII 输出下违规仍失败且消息可读。 | Maven 正/反向及编码兼容测试 |

### DOC-007 业务质量扫描与语义复核

Java DDD项目在根validate阶段默认执行scripts/JavaBusinessQuality.java（JDK17源码启动，exec-maven-plugin），与Checkstyle各自承担不同检查。扫描所有模块src/main/java，列出公开/私有方法、构造器及流程分类；有至少两个直接语句的主流程、try或块式lambda须有至少两个非空中文职责编号且连续。纯单操作/getter和纯装配不凑编号；有业务行为的构造器、复杂单语句链、catch/finally以及条件/循环内部的语义覆盖仍须复核。测试、生成目录和脚本不作为业务源码扫描。

默认单业务方法不超过45个AST语句节点（含嵌套流程），超限以QUALITY-SIZE阻止构建；拆分依据业务职责，不以绕过计数为目标。编号缺失/不连续报QUALITY-STEPS，DDD-003/004的有限结构特征报QUALITY-DOMAIN；解析失败阻止交付。可执行`java scripts/JavaBusinessQuality.java . --report AI/output/docs/verification/quality-method-inventory.csv`保存逐方法清单。清单必须包含未自动覆盖类别的复核记录，语义复核指出所负责对象、约束、事务/错误与适用的反例；自动扫描不证明全部注释正确或面向对象设计成立。

编码完成检查必须对照所有适用C/P/R规则写“规则→实现→验证→尚未证明的边界”，不能拿编译、Checkstyle或编号通过替代R项。编号/规模缺失先阻止本次编码交付，语义问题修改后复查整个受影响链路；不要拖到用户逐类指出或借机伪报已完成独立CR。

## 9. 生产适配与交付

| ID | 生效规则 | 检查 |
|---|---|---|
| PROD-001 | H2/org.h2.Driver 仅为本地可重复运行；生产连接须替换目标数据库官方驱动及配置，完善迁移/约束，凭据不入库。 | R、生产适配 |
| PROD-002 | 外部模拟响应仅为结构样例，生产必须接真实调用、超时与错误映射；不复制假结果当真实接口。 | R、生产适配 |
| PROD-003 | 事务、并发、幂等、授权、性能、兼容和回滚按需求确认，不从最小样例推断已完成。 | R、专项测试 |
| DEL-001 | 人类文档使用 Markdown、编号中文名，输入在 AI/input、输出在 AI/output；SKILL.md/checkstyle.xml/pom.xml/机器状态等协议文件保留标准名。 | R |
| DEL-002 | 变更同步技术方案（默认内含计划）、开发交付记录、交付工作台与必要的 README；测试、发布运行、效果评估文档按阶段生成。编码→开发自测→CR→独立测试保持独立状态，发布准备→初始发布→观测→放量→完整确认各有真实证据，文档合并不合并职责或验收。 | R |
| DEL-003 | 发现规范与实际代码矛盾时主动指出：能在已授权范围安全修正则修正；涉及业务/架构新决策则记录待确认。不等待用户逐类发现。 | R |
| DEL-004 | 技术方案完整草案生成后、交付确认前，对整个方案进行风险相称的反思与反例推演，覆盖实际相关的架构/依赖、数据/契约、关键链路、并发/幂等、失败恢复、安全/隔离、资源及迁移/验证。记录具体发现、修订和重新推演；区分逻辑推演、已执行实验、待验证条件。仅DDL/编译/Checkstyle通过不证明整体可行；本规则不替代自测、CR或独立测试。 | R |
| DEL-005 | 技术方案与开发计划默认不生成人天/工时估算或合计工期，实际排期由当事人依当前情况决定；保留任务依赖、实现结果和验证。用户明确请求估算时说明依据与不确定性，不作无依据承诺。 | R |


### DEL-006 AI输出与运行资产区分

AI生成的设计说明、接口说明、数据模型说明、SQL设计草稿、验证证据和补充资料，默认归入项目AI/output（如AI/output/docs、AI/output/database、AI/output/knowledge），主文档链接唯一来源；不得为此在代码根新增docs/database目录。AI输入归AI/input，机器状态归.ai-delivery。现有项目明确文档布局或用户明确指定其他位置优先。

Flyway/Liquibase正式迁移、运行schema、配置、前端源码、自动化测试与启动/构建脚本是构建运行资产，保留实际模块约定位置，不能因AI生成就移入AI/output；项目根README作为运行入口保留。已执行迁移不得改写或维护第二份可编辑DDL。迁移AI资料目录时同步相对链接、脚本和忽略规则，保留历史证据日期/原命令，明确旧路径为归档记录。

### DEL-007 迭代前后整体复核

需求修改前，阅读现有完整用例链路与对象职责，记录受影响契约、状态转换、权限/隔离、事务、并发/幂等、失败恢复和删除生命周期；简单改动按风险简写，不机械新建文档。先决定哪些对象或方法应复用、重构、替换及删除，再生成增量。禁止只追加分支、重复校验和新包装来满足局部需求。

修改后检查完整受影响方法和上下游，移除失效/重复代码，核对命名、注释与行为，覆盖本次变更的正常及失败反例。交付记录说明原来→现在、复用/删除/新增职责的理由、实际验证和遗留边界。快速生成不降低质量要求；代码增量要能解释其必要性，不能把文件/注释数量当质量证据。本检查是编码职责，完整开发自测、独立CR及测试仍按既定阶段执行。

## 10. 自动检查的真实边界与使用

根目录执行：

```bash
mvn validate
mvn clean compile
mvn clean test
mvn clean package
```

上述生命周期命令先执行业务质量扫描与 Checkstyle；扫描所有继承根 POM 的 module 的 `src/main/java`，两种门禁均不扫描测试源码、生成源码、scripts、POM、SQL/XML 或 Markdown。JDK 17 可用，插件 3.6.0 / Checkstyle 引擎 10.26.1 在根集中固定。子模块单独操作推荐仍从根使用 `mvn -pl <module> -am compile`；不要以脱离聚合根的执行方式另找一份配置。

Checkstyle 实现采用包结构/注解和单文件 AST，不做 Java 类型解析：
- 后缀、四层输入/输出、参数名形式检查覆盖推荐包中的简单类型名；Request/Command 的简单入口签名由 NAM-TYPE-PARAM 校验变量前缀与类型前缀对应；复杂全限定写法、别名、跨文件接口继承不是已证明的完整类型保障。
- 框架 import、字段注入及 SQL 注解检查只是风险特征，不验证 Maven 依赖图、所有原生 SQL 调用或真实 DDD 内聚。
- 中文存在、作者标签、文档完整性不代表描述正确；DOC-007补充编号与规模的有限自动扫描；业务动作、注释含义、领域边界、事务/日志仍需评审。
- 接口与实现类的公开方法 Javadoc 统一由 Maven Checkstyle 门禁检查，不再维护独立接口诊断脚本。
- DOC-005 对 MissingJavadocMethod、DOC-006 对 JavadocVariable 分别设置 SuppressionXpathSingleFilter：识别 Component、Service、Repository、Controller、RestController、Configuration、DomainService 标记；仅豁免单一、非空参数、只有 this 字段直接赋值的构造器。检查参数/赋值数量与非静态 final 字段数量；不做跨节点类型/字段归属解析，字段与参数的真实对应仍需评审。字段豁免进一步限定同一纯装配组件中未初始化、非静态 final 字段：它们在合法 Java 构造器中必须被赋值。已初始化的状态字段、静态常量及可变字段不豁免；真实依赖语义及复杂注入构造器需评审并按确认添加定向例外。其他自定义扫描标记需经确认增加窄范围适配，不以全局排除 CTOR_DEF 代替；已有 Javadoc 仍检查中文/作者/标签。
- 不得声称 Checkstyle 通过等于架构、功能、性能或生产验收通过；其余 R/P 规则进入代码审查及专项测试。

新项目使用 solo 的 Java DDD 模板时，AI 在根 POM 建好、正式编码前自动执行 Skill 的 `scripts/install_java_ddd_checks.py --project <项目根>`，生成根 checkstyle.xml、scripts/JavaBusinessQuality.java、validate阶段两种门禁的 Maven 配置和本规范快照 `AI/output/19 Java DDD开发规范.md`，无需用户每次重复指定。没有执行能力的宿主按同一资源生成文件，不能谎称构建已执行。

## 11. 参考代码的使用边界

编码参考随 Skill 存放在 `assets/java-ddd/reference-project/`，包含八个 Maven module 的源码、配置、测试、根 POM 与 Checkstyle。开发计划按任务选择参考链路；编码前读取该快照 README 及相关实际文件。规范负责约束，代码负责示例，Checkstyle 负责可自动检查的语法；代码与规则冲突时指出并以确认后的规则处理，不从样例擅自新增强制约束。

真实项目不能机械复制所有演示链路或生产未适配配置；已有项目约定优先。快照是经用户确认的静态版本，不依赖原 ddd 工程的绝对路径，不随新业务开发自动更新。

## 12. 规则收敛与版本记录

| 设计选择 | 当前生效规则 | 规则索引 |
|---|---|---|
| 单个 src 下按层分包 / 多 Maven module | 多 module，项目名-层名 | MOD-001 |
| 会员积分 / 公共占位 | 仅 Ddd 占位，真实项目替换领域语言 | NAM-001 |
| Entiry 拼写 | Entity | NAM-002 |
| 构造器 / 字段 Autowired / 构造器 | 最终单一构造器；框架基类已有注入不重复 | DI-001 |
| Aggregate 持有标量或注入仓储 / Entity 容器 | Aggregate 只容纳 Entity，DomainService 构造器持有仓储 | DDD-001/006 |
| 内存仓储 / 正式仓储结构 | 正式 MyBatis-Plus 仓储，即使规则表为空 | REPO-004/009 |
| Clock 与手工转换器 Bean / 按需配置 | 默认扫描 Component，直接取当前时间 | DI-002/003 |
| Domain 专属返回 / 全层共同包装 | common Result，模块枚举错误码，Domain/Out 返回 DO | ERR-001/002 |
| OutAdaptor 再加重复 Param / 直接 Command | 无独立语义直接 Application Command | MAP-003/004 |
| Repository 使用领域读取 Param / 标识类型 | Repository CRUD 标识例外；分页自有域内对象 | REPO-001/007 |
| 通用 request/command/execute / 具体参数与动作 | 四层签名及完整类型变量名，明确业务动作 | NAM-003/004 |
| 所有构造器一律注释 / 纯装配例外 | 纯依赖注入构造器无注释；业务行为构造器、公开方法和接口契约保留 | DOC-001/005 |
| 注入字段继续注释 / 去重复 | 构造器注入依赖字段不写重复注释；业务属性、常量、日志仍注释 | DOC-006 |
| 中文门禁诊断 / 跨宿主可读 | ASCII 英文诊断 + 稳定规则 ID，源码/中文 Javadoc 仍 UTF-8 | MAV-007 |
| 手动接口 Javadoc 检查 / 构建约束 | Checkstyle 生命周期门禁；独立接口检查脚本已移除 | MAV-003/004 |
| start 依靠传递依赖 / 显式装配清单 | 直接列出本服务实际运行模块，按需裁剪；不是全仓库依赖清单 | MOD-009 |
| 子模块固定 parent 版本 / 单点版本 | 根 revision + 子模块 ${revision}；Maven 3 使用 Flatten 适配发布 | MAV-005/006 |
| 默认估工时 / 当事人排期 | 默认不生成人天/工时或合计工期；显式估算请求需依据与不确定性 | DEL-005 |
| 局部表结构检查 / 整份方案生成后推演 | 主动贯穿整个技术方案的实际风险，回改矛盾并区分推理、实验和待验证 | DEL-004 |

不把历史版本日志中的旧选择当作新项目规则。规则变更更新本文件版本、受影响的安装资源/项目文档及快照说明；影响自动检查或源码时同步对应 checkstyle.xml/代码并验证正向构建与反向违规用例。仅R级交付文档规则修改时验证文档与发行包一致性、相关流程回归，不虚构门禁覆盖或重复宣称Java运行验证。

实现参考：[Maven 插件生命周期接入](https://maven.apache.org/plugins/maven-checkstyle-plugin/usage.html)、[Checkstyle MatchXpath](https://checkstyle.org/checks/coding/matchxpath.html)、[Javadoc 方法检查](https://checkstyle.org/checks/javadoc/javadocmethod.html)、[构造器缺失注释检查](https://checkstyle.org/checks/javadoc/missingjavadocmethod.html)、[窄范围 XPath 豁免](https://checkstyle.org/filters/suppressionxpathsinglefilter.html)、[Checker 编码与诊断语言](https://checkstyle.org/config.html)。这些资料用于解释检查能力；具体项目的业务架构与约定由该项目需求和用户决定，不能由第三方文档替代。

1.7：用户要求四层主入口逐层捕获及禁止向上抛出，同级-app前端、AI文档集中输出；新增语法门禁与事务回滚验证。历史源码/审批保留为原时点证据。

1.8：补充FMT-005的业务方法清单、编号/规模/有限领域结构门禁、语义复核和迭代整体审查，不将静态覆盖等同生产认证。

1.9：完善MAP转换点及ERR模块异常创建归属；静态门禁新增QUALITY-MAPPING、QUALITY-ERROR-OWNER，明确完整源对象和用途转换，不将构造位置通过等同对象设计合格。

1.10：明确公共规范对所有适用项目生效，移除项目名与项目内来源编号，使用稳定规则ID索引。具体改进来源、业务约定与执行证据保留在所属项目；本次不改变Java门禁和业务源码。
