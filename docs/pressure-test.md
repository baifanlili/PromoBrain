# 压测说明

## 工具

第一版使用 k6，后续可补充 JMeter 测试计划。

## 压测接口

- `POST /api/serving/request`
- `POST /api/budget/deduct`

## 关注指标

- 并发用户数。
- QPS。
- 平均响应时间。
- P95 响应时间。
- 错误率。
- Redis 命中率。
- 预算扣减成功数。
- 重复扣减拦截数。
- 预算不足数。

## 压测目标

- 本地环境模拟 100、500、1000 并发请求。
- 验证预算不会扣成负数。
- 验证重复 requestId 不会重复扣费。
- 验证广告请求接口不会频繁查库。

## k6 脚本位置

预算扣减压测脚本规划放在：

```text
scripts/k6/budget-deduct.js
```

广告请求压测脚本规划放在：

```text
scripts/k6/serving-request.js
```

