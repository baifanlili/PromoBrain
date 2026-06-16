# 本地开发说明

本文档记录 Windows + VSCode 本地开发方式。

## 已验证环境

- Java 17: Temurin JDK 17
- Maven 3.9.9
- Node.js 24
- npm 11
- Python 3.12
- Docker Desktop 29

## VSCode 任务

已提供 `.vscode/tasks.json`，可在 VSCode 中运行：

- `PromoBrain: middleware`
- `PromoBrain: backend`
- `PromoBrain: ai-service`
- `PromoBrain: frontend`
- `PromoBrain: dev all`

## 手动启动

### 中间件

```powershell
docker compose up -d mysql redis rabbitmq minio qdrant
```

第二版完整中间件还包括：

```powershell
docker compose up -d elasticsearch prometheus grafana
```

如果 Docker Hub 拉镜像超时，先在 Docker Desktop 中配置代理：

```text
Settings -> Resources -> Proxies
HTTP proxy:  http://127.0.0.1:7897
HTTPS proxy: http://127.0.0.1:7897
```

端口需要按你的代理软件实际端口调整。

### 后端

```powershell
cd backend
mvn spring-boot:run
```

### AI 服务

```powershell
cd ai-service
.\.venv\Scripts\python.exe -m uvicorn app.main:app --reload --port 8001
```

健康检查：

```powershell
Invoke-RestMethod http://127.0.0.1:8001/health
```

### 前端

```powershell
cd frontend
npm run dev
```

## 端到端测试

```powershell
cd frontend
npm run test:e2e
```

Playwright 会自动启动前端开发服务器，但后端、AI 服务和 Redis 需要提前启动。

第二版混合检索测试在 Elasticsearch 未启动时也能通过，因为后端会返回降级结果；启动 Elasticsearch 后可以验证真实连通状态。

## 验证命令

```powershell
cd frontend
npm run build

cd ..\backend
mvn -DskipTests package

cd ..\ai-service
.\.venv\Scripts\python.exe -c "from app.main import app; print(app.title)"
```

## 第二版验证入口

后端业务健康检查优先使用项目自己的轻量接口，避免本地未启动 RabbitMQ、Elasticsearch 等中间件时被 Actuator 健康项误判：

```powershell
Invoke-RestMethod http://127.0.0.1:8080/api/health
Invoke-RestMethod http://127.0.0.1:8080/api/architecture
```

Prometheus 指标入口用于监控采集：

```powershell
Invoke-RestMethod http://127.0.0.1:8080/actuator/prometheus
```

混合检索 smoke test：

```powershell
Invoke-RestMethod -Uri http://127.0.0.1:8080/api/search/hybrid `
  -Method Post `
  -ContentType 'application/json; charset=utf-8' `
  -Body '{"merchantId":1,"productId":101,"query":"冰丝防晒衣"}'
```
