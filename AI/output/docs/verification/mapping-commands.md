# 本轮执行口径

hello-travel：Maven根clean install（默认Checkstyle及JavaBusinessQuality），随后validate核对公共扫描器字节一致；35项JUnit。
DDD：最终根clean install；参考快照最终在临时副本clean test；各7项JUnit。
两独立无父POM消费者：clean compile，使用已安装Application。
前端：npm test、npm run build、npm run format:check，4项测试。
公共门禁：test_java_ddd_checks.py，11项；安装test_installation.py，16项。
发行：build_distributions.py，3宿主及portable；quick_validate.py结构通过。
AST只读检查：JavaBusinessQuality、逐类/构造位置清单、主入口50/14，未代替人工语义复核。
本轮Maven使用临时Central镜像settings，未改用户全局配置或依赖版本。
未调用真实供应商，未写业务数据库，未执行完整06/独立CR/发布。
