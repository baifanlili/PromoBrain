from fastapi import FastAPI

from app.config import settings

app = FastAPI(title=settings.app_name)


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}


@app.post("/ai/creative/generate")
def generate_creative(payload: dict) -> dict:
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

