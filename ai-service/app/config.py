from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    app_name: str = "PromoBrain AI Service"
    mock_enabled: bool = True
    qdrant_url: str = "http://localhost:6333"
    llm_base_url: str | None = None
    llm_api_key: str | None = None


settings = Settings()

