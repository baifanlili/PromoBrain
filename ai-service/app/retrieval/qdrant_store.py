"""Qdrant 向量库适配层。

第一版只做健康检查和连接边界，后续真实检索、向量写入、集合初始化都应优先放在这里。
"""

from qdrant_client import QdrantClient
from qdrant_client.http.exceptions import UnexpectedResponse

from app.config import settings


COLLECTION_NAME = "promobrain_knowledge"


class QdrantVectorStore:
    """Small Qdrant wrapper used only for readiness and future retrieval wiring."""

    def __init__(self) -> None:
        """创建 Qdrant 客户端。

        timeout 保持较短，避免本地 Qdrant 未启动时拖慢 AI 服务健康检查。
        """
        self.client = QdrantClient(url=settings.qdrant_url, timeout=3)

    def health(self) -> dict:
        """检查 Qdrant 可用性。

        Qdrant 不可用时返回 degraded，而不是让 FastAPI 启动失败，保证第一版 mock demo 可运行。
        """
        try:
            collections = self.client.get_collections()
            return {
                "status": "ok",
                "collections": [item.name for item in collections.collections],
            }
        except (UnexpectedResponse, OSError, ValueError) as exc:
            return {
                "status": "degraded",
                "reason": exc.__class__.__name__,
                "mock": settings.mock_enabled,
            }


vector_store = QdrantVectorStore()
