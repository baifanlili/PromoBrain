import { expect, test } from '@playwright/test'

/**
 * 最小闭环端到端测试。
 * 这个用例不追求覆盖所有页面细节，只守住第一版最重要的业务链路：
 * 商品/计划展示 -> AI 生成 -> 关键词推荐 -> 广告请求 -> Redis Lua 扣费。
 */
test('runs PromoBrain v1 minimal ad loop', async ({ page }) => {
  await page.goto('/')

  await expect(page.getByRole('heading', { name: '智能广告推广最小闭环' })).toBeVisible()
  await expect(page.getByText('夏季轻薄防晒衣')).toBeVisible()
  await expect(page.getByText('Redis Lua 保证点击扣费原子性')).toBeVisible()

  // 素材生成通过 Spring Boot 代理 FastAPI，真实模型不可用时也应返回 mock 结果。
  await page.getByRole('button', { name: '生成素材' }).click()
  await expect(page.getByText('riskLevel')).toBeVisible()
  await expect(page.getByText('LOW')).toBeVisible()

  // 关键词推荐返回排序字段，确保多路召回接口形状没有被破坏。
  await page.getByRole('button', { name: '推荐关键词' }).click()
  await expect(page.getByText('finalScore')).toBeVisible()
  await expect(page.getByText('VECTOR_RECALL')).toBeVisible()

  // 广告请求会返回候选广告；RabbitMQ 未启动时 mqSent=false 也属于可接受降级。
  await page.getByRole('button', { name: '请求广告' }).click()
  await expect(page.getByText('rankScore')).toBeVisible()
  await expect(page.getByText('creativeId')).toBeVisible()

  // Redis Lua 扣减需要先初始化预算，再执行点击扣费。
  await page.getByRole('button', { name: '初始化 5000 元预算' }).click()
  await page.getByRole('button', { name: '点击扣费 0.8 元' }).click()
  await expect(page.getByText('DEDUCT_SUCCESS')).toBeVisible()
  await expect(page.getByText('4999.2')).toBeVisible()
})

