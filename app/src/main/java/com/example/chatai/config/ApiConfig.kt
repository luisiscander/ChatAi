package com.example.chatai.config

object ApiConfig {
    // Default API key - can be overridden by user input
    const val DEFAULT_API_KEY = "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"
    
    // OpenRouter API endpoints
    const val OPENROUTER_BASE_URL = "https://openrouter.ai/api/v1/"
    const val OPENROUTER_MODELS_ENDPOINT = "models"
    const val OPENROUTER_CHAT_ENDPOINT = "chat/completions"
    
    // API configuration
    const val DEFAULT_MODEL = "gpt-4"
    const val MAX_TOKENS = 4000
    const val TEMPERATURE = 0.7
}
