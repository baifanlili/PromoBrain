# PromoBrain Codex 项目约定

## 语言与注释

- 本项目的业务代码、配置说明和文档默认使用中文说明。
- 新增或修改代码时，关键注释优先使用中文。
- 新增源码文件必须包含文件级或类级中文说明，说明该文件在第一版架构中的职责。
- 新增公开函数、Controller 接口、Service 方法、配置 Bean、Python 路由函数必须包含中文注释或 docstring。
- 必须为以下内容添加维护性注释：
  - 架构入口与模块边界，例如后端、AI 服务、消息队列、向量库的接入点。
  - 高并发关键逻辑，例如 Redis Lua 扣减、幂等、分布式锁、MQ 异步削峰。
  - 降级逻辑，例如 AI 服务不可用时的 mock、模板兜底或转人工审核。
  - 后续版本会替换或扩展的临时实现，例如 v1 mock、内存实现、占位接口。
- 不要写无意义逐行注释，例如“创建变量”“返回结果”这类代码本身已经表达清楚的内容。
- 注释应解释“为什么这样设计”和“维护时要注意什么”，而不是重复代码表面含义。

## 架构阶段

当前优先实现第一版架构：

- Spring Boot 单体后端。
- Redis Lua 预算原子扣减。
- RabbitMQ 异步消息队列。
- Vue3 管理前端。
- FastAPI AI 服务。
- Qdrant 向量库。

后续版本再逐步加入：

- 第二版：Caffeine 多级缓存、Sentinel、Prometheus/Grafana、Elasticsearch。
- 第三版：Gateway/Auth/Ad/Knowledge/Dashboard 微服务拆分、Nacos、OpenFeign。
- 第四版：ClickHouse 广告日志分析。

## 可维护性

- 第一版可以使用 mock 或降级实现，但接口形状要贴近最终架构，方便后续替换为真实 MySQL、Redis、RabbitMQ、Qdrant 和 LLM 实现。
- 新增配置时要集中放在配置类或配置文件中，避免散落硬编码。
- 同一业务能力尽量保持清晰边界，例如预算扣减归 `budget`，AI 调用归 `ai`，RabbitMQ 拓扑归 `common.config`。
- 避免为了展示技术而过早拆微服务，第一版先保持模块化单体。

## 验证要求

涉及代码变更时，优先运行对应验证：

- 后端：`mvn -DskipTests package`
- 前端：`npm run build`
- AI 服务：`.venv` 中导入或启动 FastAPI 健康检查
