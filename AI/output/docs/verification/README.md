# SRC-043工程边界整改证据

当前14主入口审计、根4条开发回归、最终源码独立快照4条及实际失败回滚检查通过。日志与boundary-summary.json记录范围，非独立CR、验收或线上验证。根测试先于仅依赖字段位置/测试import格式调整；最终快照对照一致并重新执行4条测试。

## 当前业务质量整改证据（2026-09-18）

quality-*为当前规范1.8/快照1.6的源码业务质量扫描、全方法清单、入口异常审计、针对性回归、独立安装消费者与构建证据；solo-quality-*为门禁/流程/启动/安装回归。历史summary/boundary-*保留原日期与哈希，不拿旧摘要证明当前源码。质量清单的自动分类有明确限界：编号、规模和有限领域结构不证明注释正确、真实对象职责或完整业务/生产验收。当前权威结果见quality-summary.json与05交付记录。

## 2026-09-18当前映射与异常归属证据

mapping-summary.json为本轮范围与统计；mapping-*.log为实际构建/回归；mapping-junit保留全部模块JUnit原始结果；mapping-sha256.json核对日志/清单。hello-travel的mapping-class/constructor/method-inventory.csv及mapping-entry-audit.csv提供全量位置导航；DDD mapping-entry-audit.csv提供14主入口结构检查。旧quality-/boundary-文件保留历史证据，不将旧执行重复计入本轮。

## 2026-09-18业务垂直领域整改

vertical-summary.json是当前范围，vertical-*.log/CSV与vertical-junit为原始证据，vertical-sha256.json校验文件；hello-travel的vertical-java-before.zip保留整改前Java来源，仅作归档。当前39项后端与36主入口统计不混算旧轮35/50；DDD根与临时独立快照各7项。跨域事务使用mock管理器验证commit/rollback调用，不声称真实MySQL故障数据回滚。
