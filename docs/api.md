# API 设计

## Backend API

所有后端接口统一以 `/api` 为前缀，响应结构使用：

```json
{
  "success": true,
  "code": "OK",
  "message": "success",
  "data": {}
}
```

## Auth

- `POST /api/auth/login`
- `POST /api/auth/logout`
- `GET /api/auth/me`
- `GET /api/auth/menu`

## Product

- `POST /api/product`
- `PUT /api/product/{id}`
- `GET /api/product/page`
- `GET /api/product/{id}`
- `DELETE /api/product/{id}`

## Knowledge

- `POST /api/knowledge/upload`
- `GET /api/knowledge/list`
- `GET /api/knowledge/{id}`
- `POST /api/knowledge/{id}/reindex`
- `DELETE /api/knowledge/{id}`

## Creative

- `POST /api/creative/generate`
- `POST /api/creative/save`
- `GET /api/creative/list`
- `POST /api/creative/{id}/submit-audit`

## Keyword

- `POST /api/keyword/recommend`

## Campaign

- `POST /api/campaign`
- `PUT /api/campaign/{id}`
- `POST /api/campaign/{id}/publish`
- `POST /api/campaign/{id}/pause`
- `GET /api/campaign/page`

## Serving

- `POST /api/serving/request`

## Budget

- `POST /api/budget/deduct`
- `GET /api/budget/transactions`

## Dashboard

- `GET /api/dashboard/overview`
- `GET /api/dashboard/campaign/{id}`
- `GET /api/dashboard/product/{id}`
- `GET /api/dashboard/keyword`

## AI Service API

- `POST /ai/knowledge/index`
- `POST /ai/retrieval/search`
- `POST /ai/creative/generate`
- `POST /ai/keyword/recommend`
- `POST /ai/audit/creative`
- `POST /ai/agent/promotion-plan`

