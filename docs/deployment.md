# 部署说明

## 本地依赖

- Docker
- Docker Compose
- Java 17
- Maven 3.9+
- Node.js 20+
- Python 3.10+

## 中间件启动

```bash
docker-compose up -d mysql redis rabbitmq minio qdrant
```

## 后端启动

```bash
cd backend
mvn spring-boot:run
```

后端默认端口：`8080`。

## AI 服务启动

```bash
cd ai-service
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8001
```

AI 服务默认端口：`8001`。

## 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端默认端口：`5173`。

## 一键部署目标

最终版本需要支持：

```bash
docker-compose up -d
```

并启动 MySQL、Redis、RabbitMQ、MinIO、Qdrant、Backend、AI Service 和 Frontend。

