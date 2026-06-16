# 数据库设计

数据库使用 MySQL 8。初始化脚本位于 `sql/init.sql`，Demo 数据位于 `sql/demo_data.sql`。

## 通用字段

核心业务表统一包含：

- `merchant_id`: 商家隔离字段。
- `created_by`: 创建人。
- `created_at`: 创建时间。
- `updated_at`: 更新时间。
- `is_deleted`: 逻辑删除标记。

## 核心表

- `sys_user`: 用户。
- `sys_role`: 角色。
- `sys_permission`: 权限点。
- `merchant`: 商家。
- `product`: 商品。
- `knowledge_doc`: 知识库文档。
- `knowledge_chunk`: 知识库切片。
- `ad_campaign`: 广告计划。
- `ad_creative`: 广告素材。
- `ad_keyword`: 广告关键词。
- `budget_transaction`: 预算流水。
- `ad_event_log`: 曝光、点击、转化日志。
- `async_task`: 异步任务。
- `feedback_record`: 反馈记录。

## 关键约束

预算扣减链路必须包含：

```sql
unique key uk_request_id(request_id)
```

该唯一索引用于在 MySQL 层兜底避免重复扣费。Redis 侧通过 `ad:click:deduct:{requestId}` 做幂等拦截。

## 索引原则

- 商家隔离查询优先建立 `merchant_id` 相关索引。
- 广告计划按 `merchant_id + status` 查询。
- 日志表按 `campaign_id + event_type` 和 `created_at` 查询。
- 预算流水按 `campaign_id` 和 `request_id` 查询。

