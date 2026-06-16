import axios from 'axios'

/**
 * 前端统一 API 适配层。
 * 第一版先集中封装 demo、AI 代理和预算扣减接口，后续接入真实业务模块时优先扩展这里。
 */
const http = axios.create({
  baseURL: '/api',
  timeout: 8000,
})

export interface ApiResult<T> {
  success: boolean
  code: string
  message: string
  data: T
}

export interface DemoSnapshot {
  product: {
    id: number
    productName: string
    category: string
    price: number
    sellingPoints: string[]
    targetUsers: string[]
  }
  campaign: {
    id: number
    campaignName: string
    goal: string
    totalBudget: number
    status: string
  }
  keywords: string[]
  dashboard: {
    impressions: number
    clicks: number
    ctr: number
    cvr: number
    cost: number
    gmv: number
    roi: number
  }
  architectureHighlights: string[]
}

export interface DeductResult {
  success: boolean
  code: string
  remainingBudget: number | null
}

export interface ServingResult {
  ads: Array<{
    campaignId: number
    productId: number
    creativeId: number
    title: string
    content: string
    rankScore: number
  }>
  mqSent: boolean
}

/**
 * 获取第一版演示快照。
 * 页面初始化依赖该接口展示商品、计划、看板和架构亮点。
 */
export async function fetchDemoSnapshot() {
  const response = await http.get<ApiResult<DemoSnapshot>>('/demo/snapshot')
  return response.data.data
}

/**
 * 生成广告素材。
 * 调用后端代理接口，后端再转发到 FastAPI AI 服务。
 */
export async function generateCreative(productId: number) {
  const response = await http.post<ApiResult<Record<string, unknown>>>('/demo/creative/generate', { productId })
  return response.data.data
}

/**
 * 推荐关键词。
 * 第一版返回 mock 排序结果，后续可替换为真实多路召回。
 */
export async function recommendKeyword(productId: number) {
  const response = await http.post<ApiResult<Record<string, unknown>>>('/demo/keyword/recommend', { productId })
  return response.data.data
}

/**
 * 模拟广告请求。
 * 后端会尝试发送曝光日志到 RabbitMQ，并把是否成功写入 mqSent。
 */
export async function requestServing(keyword: string) {
  const response = await http.post<ApiResult<ServingResult>>('/demo/serving/request', {
    requestId: `req_${Date.now()}`,
    keyword,
  })
  return response.data.data
}

/**
 * 初始化 Redis 中的广告计划预算。
 * 这是第一版本地演示入口，后续由广告计划发布流程负责写入。
 */
export async function initBudget(campaignId: number, amount: number) {
  const response = await http.post<ApiResult<void>>('/budget/init', { campaignId, amount })
  return response.data
}

/**
 * 执行 Redis Lua 点击扣费。
 * requestId 每次点击保持唯一，用于验证幂等和预算不会超扣。
 */
export async function deductBudget(campaignId: number, cost: number) {
  const response = await http.post<ApiResult<DeductResult>>('/budget/deduct', {
    requestId: `click_${Date.now()}`,
    campaignId,
    creativeId: 5001,
    cost,
  })
  return response.data.data
}
