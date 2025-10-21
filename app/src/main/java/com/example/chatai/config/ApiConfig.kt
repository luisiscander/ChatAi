package com.example.chatai.config

object ApiConfig {
    // No default API key for security reasons
    // Users MUST configure their own API key from https://openrouter.ai/keys
    // Go to Settings -> Manage API Key in the app to configure
    val DEFAULT_API_KEY: String? = null
    
    // OpenRouter API endpoints
    const val OPENROUTER_BASE_URL = "https://openrouter.ai/api/v1/"
    const val OPENROUTER_MODELS_ENDPOINT = "models"
    const val OPENROUTER_CHAT_ENDPOINT = "chat/completions"
    
    // API configuration
    const val DEFAULT_MODEL = "google/gemini-2.0-flash-exp:free"
    const val MAX_TOKENS = 4000
    const val TEMPERATURE = 0.7
}
