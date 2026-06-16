# 开发手册

## 注释规范

PromoBrain 是一个用于展示广告推广业务和后端架构能力的开源项目。代码需要方便自己后续维护，也方便面试时快速讲清楚设计取舍。

### 必须写中文注释的地方

- 每个新增源码文件：说明该文件在第一版架构中的职责。
- 每个公开函数、Controller 接口、Service 方法、配置 Bean、Python 路由函数：说明用途和维护注意点。
- Redis Lua、幂等、分布式锁、MQ 异步削峰、AI 降级、Qdrant 检索等关键架构逻辑。
- 第一版 mock 或占位实现：必须说明后续会替换成什么真实实现。
- 第二版缓存、限流、监控、混合检索相关代码：必须说明和第一版主链路的关系，避免为了展示技术而堆组件。

### 不要写的注释

不要写单纯重复代码字面含义的注释，例如：

```java
// 返回成功
return Result.ok();
```

更好的注释应该解释设计原因：

```java
// 第一版先返回稳定结构，后续接 MySQL 后保持响应结构不变，只替换数据来源。
return Result.ok(snapshot);
```

## 第一版架构边界

第一版实现以下架构能力：

- Spring Boot 单体后端。
- Redis Lua 预算原子扣减。
- RabbitMQ 异步消息队列。
- Vue3 管理前端。
- FastAPI AI 服务。
- Qdrant 向量库。

第二版在单体内扩展以下能力：

- Caffeine 本地缓存。
- Sentinel 限流降级。
- Prometheus/Grafana 监控。
- Elasticsearch + Qdrant 混合检索。

第三版、第四版技术只写入规划，不提前混进当前代码。

## 验证命令

```powershell
cd backend
mvn -DskipTests package

cd ..\ai-service
.\.venv\Scripts\python.exe -c "from app.main import app; print(app.title)"

cd ..\frontend
npm run build
npm run test:e2e
```

## 自动化测试

第一版使用 Playwright 做端到端测试，测试文件位于：

```text
frontend/e2e/minimal-loop.spec.ts
```

该测试覆盖最小闭环：

- 首页加载商品和广告计划。
- 生成广告素材。
- 推荐关键词。
- 模拟广告请求。
- 初始化预算。
- Redis Lua 点击扣费。

运行前需要保证：

- 后端监听 `http://127.0.0.1:8080`。
- AI 服务监听 `http://127.0.0.1:8001`。
- Redis 已启动。

运行命令：

```powershell
cd frontend
npm run test:e2e
```
