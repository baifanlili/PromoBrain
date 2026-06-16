<script setup lang="ts">
// 第一版最小闭环页面。
// 页面聚合商品、素材、关键词、广告请求和预算扣减，目的是验证 v1 架构链路已经接通。
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  deductBudget,
  fetchDemoSnapshot,
  generateCreative,
  hybridSearch,
  initBudget,
  recommendKeyword,
  requestServing,
  type DeductResult,
  type DemoSnapshot,
  type HybridSearchResult,
  type ServingResult,
} from './api/promobrain'

const snapshot = ref<DemoSnapshot>()
const loading = ref(false)
const keyword = ref('冰丝防晒衣')
const creativeResult = ref<Record<string, unknown>>()
const keywordResult = ref<Record<string, unknown>>()
const servingResult = ref<ServingResult>()
const deductResult = ref<DeductResult>()
const searchResult = ref<HybridSearchResult>()

const campaignId = computed(() => snapshot.value?.campaign.id ?? 1001)
const productId = computed(() => snapshot.value?.product.id ?? 101)

/**
 * 统一执行异步动作。
 * 本地依赖可能未完全启动，所以这里集中处理 loading、错误提示和降级说明。
 */
async function runAction<T>(label: string, action: () => Promise<T>, after?: (data: T) => void) {
  loading.value = true
  try {
    const data = await action()
    after?.(data)
    ElMessage.success(`${label}成功`)
  } catch (error) {
    ElMessage.error(`${label}失败，请确认后端、Redis 或 AI 服务是否启动`)
    console.error(error)
  } finally {
    loading.value = false
  }
}

/**
 * 加载第一版演示快照。
 * 后续接入真实登录和 RBAC 后，该接口可以替换为 dashboard 聚合接口。
 */
async function loadSnapshot() {
  await runAction('加载演示数据', fetchDemoSnapshot, (data) => {
    snapshot.value = data
  })
}

/**
 * 触发 AI 素材生成链路。
 * 前端只调用 Spring Boot，真实模型服务地址由后端统一管理。
 */
async function handleGenerateCreative() {
  await runAction('生成素材', () => generateCreative(productId.value), (data) => {
    creativeResult.value = data
  })
}

/**
 * 触发关键词推荐链路。
 * 第一版用于验证 FastAPI 接口形状，后续替换为真实多路召回结果。
 */
async function handleRecommendKeyword() {
  await runAction('推荐关键词', () => recommendKeyword(productId.value), (data) => {
    keywordResult.value = data
  })
}

/**
 * 模拟广告在线请求。
 * 返回结果中 mqSent 可以看出 RabbitMQ 异步曝光日志是否发送成功。
 */
async function handleServingRequest() {
  await runAction('广告请求', () => requestServing(keyword.value), (data) => {
    servingResult.value = data
  })
}

/**
 * 执行第二版混合检索。
 * Elasticsearch 不可用时后端会降级，页面仍能展示 Qdrant/mock 语义召回结果。
 */
async function handleHybridSearch() {
  await runAction('混合检索', () => hybridSearch(keyword.value), (data) => {
    searchResult.value = data
  })
}

/**
 * 初始化预算缓存。
 * 这一步先把广告计划预算写入 Redis，后续点击扣费才能执行 Lua 扣减。
 */
async function handleInitBudget() {
  await runAction('初始化预算', () => initBudget(campaignId.value, 5000))
}

/**
 * 执行点击扣费。
 * 每次点击生成唯一 requestId，用于验证 Redis Lua 原子扣减和幂等键写入。
 */
async function handleDeductBudget() {
  await runAction('点击扣费', () => deductBudget(campaignId.value, 0.8), (data) => {
    deductResult.value = data
  })
}

/**
 * 把对象格式化为 JSON，方便第一版页面直接观察接口契约。
 */
function prettyJson(value: unknown) {
  return JSON.stringify(value, null, 2)
}

onMounted(loadSnapshot)
</script>

<template>
  <main class="app-shell">
    <header class="topbar">
      <div>
        <p class="eyebrow">PromoBrain v1</p>
        <h1>智能广告推广最小闭环</h1>
      </div>
      <el-button :loading="loading" type="primary" @click="loadSnapshot">刷新数据</el-button>
    </header>

    <section class="metrics" v-if="snapshot">
      <div class="metric">
        <span>曝光</span>
        <strong>{{ snapshot.dashboard.impressions.toLocaleString() }}</strong>
      </div>
      <div class="metric">
        <span>点击</span>
        <strong>{{ snapshot.dashboard.clicks.toLocaleString() }}</strong>
      </div>
      <div class="metric">
        <span>CTR</span>
        <strong>{{ (snapshot.dashboard.ctr * 100).toFixed(2) }}%</strong>
      </div>
      <div class="metric">
        <span>ROI</span>
        <strong>{{ snapshot.dashboard.roi.toFixed(2) }}</strong>
      </div>
    </section>

    <section class="layout" v-if="snapshot">
      <div class="panel">
        <h2>商品与广告计划</h2>
        <dl>
          <dt>商品</dt>
          <dd>{{ snapshot.product.productName }} / {{ snapshot.product.category }} / ¥{{ snapshot.product.price }}</dd>
          <dt>卖点</dt>
          <dd>{{ snapshot.product.sellingPoints.join('、') }}</dd>
          <dt>人群</dt>
          <dd>{{ snapshot.product.targetUsers.join('、') }}</dd>
          <dt>计划</dt>
          <dd>{{ snapshot.campaign.campaignName }} / {{ snapshot.campaign.status }} / ¥{{ snapshot.campaign.totalBudget }}</dd>
        </dl>
      </div>

      <div class="panel">
        <h2>第一版架构</h2>
        <ul class="highlights">
          <li v-for="item in snapshot.architectureHighlights" :key="item">{{ item }}</li>
        </ul>
      </div>
    </section>

    <section class="workflow">
      <div class="step">
        <h3>1. AI 素材生成</h3>
        <p>Spring Boot 代理 FastAPI，模型不可用时返回可解释降级结果。</p>
        <el-button :loading="loading" @click="handleGenerateCreative">生成素材</el-button>
        <pre v-if="creativeResult">{{ prettyJson(creativeResult) }}</pre>
      </div>

      <div class="step">
        <h3>2. 关键词推荐</h3>
        <p>第一版固定接口形状，后续接入多路召回、Qdrant 和 rerank。</p>
        <el-button :loading="loading" @click="handleRecommendKeyword">推荐关键词</el-button>
        <pre v-if="keywordResult">{{ prettyJson(keywordResult) }}</pre>
      </div>

      <div class="step">
        <h3>3. 广告请求模拟</h3>
        <p>返回广告候选，并尝试通过 RabbitMQ 异步写曝光日志。</p>
        <div class="inline-form">
          <el-input v-model="keyword" placeholder="搜索词" />
          <el-button :loading="loading" type="primary" @click="handleServingRequest">请求广告</el-button>
        </div>
        <pre v-if="servingResult">{{ prettyJson(servingResult) }}</pre>
      </div>

      <div class="step">
        <h3>4. Redis Lua 预算扣减</h3>
        <p>先初始化预算，再模拟点击扣费 0.8 元，验证原子扣减链路。</p>
        <div class="actions">
          <el-button :loading="loading" @click="handleInitBudget">初始化 5000 元预算</el-button>
          <el-button :loading="loading" type="danger" @click="handleDeductBudget">点击扣费 0.8 元</el-button>
        </div>
        <pre v-if="deductResult">{{ prettyJson(deductResult) }}</pre>
      </div>

      <div class="step">
        <h3>5. 第二版混合检索</h3>
        <p>Elasticsearch 做关键词召回，Qdrant 做向量召回，后端统一合并排序。</p>
        <el-button :loading="loading" @click="handleHybridSearch">执行混合检索</el-button>
        <pre v-if="searchResult">{{ prettyJson(searchResult) }}</pre>
      </div>
    </section>
  </main>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
  padding: 28px;
  color: #172033;
  background: #f5f7fb;
  font-family: Inter, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
}

.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  max-width: 1280px;
  margin: 0 auto 22px;
}

.eyebrow {
  margin: 0 0 6px;
  color: #2563eb;
  font-size: 13px;
  font-weight: 700;
}

h1,
h2,
h3,
p {
  margin: 0;
}

h1 {
  font-size: 30px;
  letter-spacing: 0;
}

h2 {
  margin-bottom: 14px;
  font-size: 18px;
}

h3 {
  margin-bottom: 8px;
  font-size: 16px;
}

.metrics,
.layout,
.workflow {
  max-width: 1280px;
  margin: 0 auto;
}

.metrics {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.metric,
.panel,
.step {
  border: 1px solid #dbe3ef;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
}

.metric {
  padding: 16px;
}

.metric span {
  display: block;
  margin-bottom: 8px;
  color: #64748b;
  font-size: 13px;
}

.metric strong {
  font-size: 24px;
}

.layout {
  display: grid;
  grid-template-columns: 1.2fr 0.8fr;
  gap: 16px;
  margin-bottom: 16px;
}

.panel,
.step {
  padding: 18px;
}

dl {
  display: grid;
  grid-template-columns: 76px 1fr;
  gap: 10px 14px;
  margin: 0;
}

dt {
  color: #64748b;
}

dd {
  margin: 0;
}

.highlights {
  display: grid;
  gap: 10px;
  margin: 0;
  padding-left: 18px;
}

.workflow {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.step p {
  min-height: 42px;
  color: #64748b;
  line-height: 1.6;
}

.step :deep(.el-button) {
  margin-top: 12px;
}

.inline-form,
.actions {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-top: 12px;
}

pre {
  max-height: 260px;
  overflow: auto;
  margin: 14px 0 0;
  padding: 12px;
  border-radius: 8px;
  color: #d9e8ff;
  background: #172033;
  font-size: 12px;
  line-height: 1.6;
  white-space: pre-wrap;
}

@media (max-width: 920px) {
  .topbar,
  .inline-form,
  .actions {
    align-items: stretch;
    flex-direction: column;
  }

  .metrics,
  .layout,
  .workflow {
    grid-template-columns: 1fr;
  }
}
</style>
