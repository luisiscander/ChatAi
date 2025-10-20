package com.example.chatai.domain.model

data class AiModel(
    val id: String,
    val name: String,
    val company: String,
    val context: String,
    val pricing: ModelPricing,
    val description: String? = null,
    val capabilities: List<String> = emptyList(),
    val rateLimits: RateLimits? = null,
    val documentationUrl: String? = null
)

data class ModelPricing(
    val input: String, // e.g., "$0.01 / 1K tokens"
    val output: String, // e.g., "$0.03 / 1K tokens"
    val inputPricePer1k: Double = 0.0,
    val outputPricePer1k: Double = 0.0
)

data class RateLimits(
    val requestsPerMinute: Int? = null,
    val tokensPerMinute: Int? = null
)

enum class ModelCompany(val displayName: String) {
    OPENAI("OpenAI"),
    ANTHROPIC("Anthropic"),
    META("Meta"),
    GOOGLE("Google"),
    COHERE("Cohere"),
    UNKNOWN("Unknown");
    
    companion object {
        fun fromString(company: String): ModelCompany {
            return when (company.lowercase()) {
                "openai" -> OPENAI
                "anthropic" -> ANTHROPIC
                "meta" -> META
                "google" -> GOOGLE
                "cohere" -> COHERE
                else -> UNKNOWN
            }
        }
    }
}
