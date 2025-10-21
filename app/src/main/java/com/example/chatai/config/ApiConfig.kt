package com.example.chatai.config

object ApiConfig {
    // Default API key - can be overridden by user input
    // TODO: Replace with your VALID API key from https://openrouter.ai/keys
    const val DEFAULT_API_KEY = "TU_NUEVA_API_KEY_AQUI"
    
    // OpenRouter API endpoints
    const val OPENROUTER_BASE_URL = "https://openrouter.ai/api/v1/"
    const val OPENROUTER_MODELS_ENDPOINT = "models"
    const val OPENROUTER_CHAT_ENDPOINT = "chat/completions"
    
    // API configuration
    const val DEFAULT_MODEL = "gpt-4"
    const val MAX_TOKENS = 4000
    const val TEMPERATURE = 0.7
}
