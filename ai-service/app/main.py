"""PromoBrain AI 服务入口。

第一版 FastAPI 服务负责固定 AI/RAG/Agent 接口形状，并在没有真实模型 Key 时提供可维护的 mock 输出。
"""

from fastapi import FastAPI

from app.config import settings
from app.retrieval.qdrant_store import vector_store
from app.schemas.common import (
    CreativeAuditRequest,
    KnowledgeIndexRequest,
    PromotionPlanRequest,
    RetrievalSearchRequest,
)

app = FastAPI(title=settings.app_name)


@app.get("/health")
def health() -> dict:
    """Return service and vector-store readiness for local smoke tests."""
    return {
        "status": "ok",
        "service": "promobrain-ai-service",
        "mock": settings.mock_enabled,
        "qdrant": vector_store.health(),
    }


@app.post("/ai/knowledge/index")
def index_knowledge(payload: KnowledgeIndexRequest) -> dict:
    """Create the v1 indexing contract; parsing and embedding can be swapped in later."""
    return {
        "success": True,
        "docId": payload.doc_id,
        "chunkCount": 3,
        "vectorStore": "qdrant",
        "collection": "promobrain_knowledge",
        "mock": settings.mock_enabled,
    }


@app.post("/ai/retrieval/search")
def search_knowledge(payload: RetrievalSearchRequest) -> dict:
    """Return a deterministic retrieval result so backend/frontend demos do not need a model key."""
    return {
        "results": [
            {
                "chunkId": "ck_demo_001",
                "content": "该防晒衣采用 UPF50+ 面料，主打轻薄、透气、冰丝凉感。",
                "score": 0.91,
                "metadata": {
                    "merchantId": payload.merchant_id,
                    "productId": payload.product_id,
                    "source": "mock-qdrant",
                },
            }
        ],
        "mock": settings.mock_enabled,
    }


@app.post("/ai/creative/generate")
def generate_creative(payload: dict) -> dict:
    """生成广告素材。

    目前返回稳定 mock 文案；后续实现会先检索商品知识和广告规则，再调用 LLM 生成候选素材。
    """
    return {
        "creatives": [
            {
                "title": "夏天通勤不闷热，这件防晒衣真的轻",
                "content": "UPF50+ 面料，轻薄透气，通勤、骑行、户外都能穿。",
                "riskLevel": "LOW",
                "hitRules": [],
            }
        ],
        "mock": settings.mock_enabled,
    }


@app.post("/ai/keyword/recommend")
def recommend_keywords(payload: dict) -> dict:
    """推荐广告关键词。

    第一版固定多路召回结果结构，后续可接入商品卖点召回、历史词召回、向量召回和 rerank。
    """
    return {
        "keywords": [
            {
                "keyword": "冰丝防晒衣",
                "recallSource": ["PRODUCT_SELLING_POINT", "VECTOR_RECALL"],
                "relevanceScore": 0.92,
                "estimatedCtr": 0.08,
                "estimatedCvr": 0.05,
                "finalScore": 0.86,
            }
        ],
        "mock": settings.mock_enabled,
    }


@app.post("/ai/audit/creative")
def audit_creative(payload: CreativeAuditRequest) -> dict:
    """First-pass rule audit. Later versions can add LLM review and knowledge consistency checks."""
    risk_words = ["全网最低", "永久有效", "100%治愈"]
    hits = [word for word in risk_words if word in payload.title or word in payload.content]
    return {
        "auditStatus": "REJECTED" if hits else "PASSED",
        "riskLevel": "HIGH" if hits else "LOW",
        "hitRules": hits,
        "mock": settings.mock_enabled,
    }


@app.post("/ai/agent/promotion-plan")
def promotion_plan(payload: PromotionPlanRequest) -> dict:
    """Mock the promotion-agent output shape before real tool orchestration is implemented."""
    return {
        "recommendedKeywords": ["冰丝防晒衣", "UPF50+", "通勤防晒", "骑行防晒"],
        "creativeSuggestions": [
            "突出冰丝凉感和轻薄透气",
            "避免绝对化承诺，强调通勤和户外短途场景",
        ],
        "audienceSuggestions": ["户外通勤女性", "学生", "骑行人群"],
        "budgetPlan": {
            "totalBudget": payload.budget,
            "testBudgetRatio": 0.2,
            "optimizeAfterClicks": 100,
        },
        "riskWarnings": ["避免使用全网最低、永久有效等极限词"],
        "mock": settings.mock_enabled,
    }
