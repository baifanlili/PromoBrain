"""AI 服务通用请求模型。

第一版先固定 FastAPI 与 Spring Boot 的接口契约，后续替换真实解析、向量化和 Agent 编排时保持字段兼容。
"""

from pydantic import BaseModel, Field


class KnowledgeIndexRequest(BaseModel):
    """知识库索引请求。"""

    doc_id: int = Field(alias="docId")
    merchant_id: int = Field(alias="merchantId")
    product_id: int | None = Field(default=None, alias="productId")
    doc_type: str = Field(alias="docType")
    file_url: str = Field(alias="fileUrl")


class RetrievalSearchRequest(BaseModel):
    """语义检索请求。"""

    merchant_id: int = Field(alias="merchantId")
    product_id: int | None = Field(default=None, alias="productId")
    query: str
    top_k: int = Field(default=10, alias="topK")


class CreativeAuditRequest(BaseModel):
    """素材审核请求。"""

    creative_id: int | None = Field(default=None, alias="creativeId")
    title: str
    content: str
    platform: str | None = None


class PromotionPlanRequest(BaseModel):
    """推广 Agent 请求。"""

    product_id: int = Field(alias="productId")
    goal: str
    budget: float
    requirement: str | None = None
