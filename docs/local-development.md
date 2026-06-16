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

## 验证命令

```powershell
cd frontend
npm run build

cd ..\backend
mvn -DskipTests package

cd ..\ai-service
.\.venv\Scripts\python.exe -c "from app.main import app; print(app.title)"
```

