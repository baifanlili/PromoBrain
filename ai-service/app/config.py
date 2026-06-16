"""AI 服务配置模块。

第一版通过集中配置控制 mock、Qdrant 和模型服务地址，避免路由函数里散落硬编码。
"""

from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    """FastAPI 服务配置。

    后续接入真实 LLM、Embedding 服务或多环境部署时，优先扩展这里。
    """

    app_name: str = "PromoBrain AI Service"
    mock_enabled: bool = True
    qdrant_url: str = "http://localhost:6333"
    llm_base_url: str | None = None
    llm_api_key: str | None = None


settings = Settings()
