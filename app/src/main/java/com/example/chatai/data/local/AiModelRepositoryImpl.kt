package com.example.chatai.data.local

import com.example.chatai.domain.model.AiModel
import com.example.chatai.domain.model.ModelPricing
import com.example.chatai.domain.model.RateLimits
import com.example.chatai.domain.repository.AiModelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiModelRepositoryImpl @Inject constructor() : AiModelRepository {

    // Simulated models data
    private val simulatedModels = listOf(
        AiModel(
            id = "openai/gpt-4-turbo",
            name = "GPT-4 Turbo",
            company = "OpenAI",
            context = "128k tokens",
            pricing = ModelPricing(
                input = "$0.01 / 1K tokens",
                output = "$0.03 / 1K tokens",
                inputPricePer1k = 0.01,
                outputPricePer1k = 0.03
            ),
            description = "Most capable GPT-4 model with improved speed and lower costs",
            capabilities = listOf("Text generation", "Code completion", "Analysis"),
            rateLimits = RateLimits(
                requestsPerMinute = 500,
                tokensPerMinute = 150000
            ),
            documentationUrl = "https://platform.openai.com/docs/models/gpt-4"
        ),
        AiModel(
            id = "anthropic/claude-3-opus-20240229",
            name = "Claude 3 Opus",
            company = "Anthropic",
            context = "200k tokens",
            pricing = ModelPricing(
                input = "$0.015 / 1K tokens",
                output = "$0.075 / 1K tokens",
                inputPricePer1k = 0.015,
                outputPricePer1k = 0.075
            ),
            description = "Most powerful Claude model for complex tasks",
            capabilities = listOf("Text generation", "Analysis", "Reasoning"),
            rateLimits = RateLimits(
                requestsPerMinute = 5,
                tokensPerMinute = 7500
            ),
            documentationUrl = "https://docs.anthropic.com/claude/docs"
        ),
        AiModel(
            id = "meta-llama/llama-3-70b-instruct",
            name = "Llama 3 70B Instruct",
            company = "Meta",
            context = "8k tokens",
            pricing = ModelPricing(
                input = "$0.0008 / 1K tokens",
                output = "$0.0008 / 1K tokens",
                inputPricePer1k = 0.0008,
                outputPricePer1k = 0.0008
            ),
            description = "Large language model by Meta for instruction following",
            capabilities = listOf("Text generation", "Code completion"),
            rateLimits = RateLimits(
                requestsPerMinute = 30,
                tokensPerMinute = 30000
            ),
            documentationUrl = "https://huggingface.co/meta-llama/Llama-3-70B-Instruct"
        ),
        AiModel(
            id = "google/gemini-pro",
            name = "Gemini Pro",
            company = "Google",
            context = "32k tokens",
            pricing = ModelPricing(
                input = "$0.0005 / 1K tokens",
                output = "$0.0015 / 1K tokens",
                inputPricePer1k = 0.0005,
                outputPricePer1k = 0.0015
            ),
            description = "Google's most capable model for complex reasoning",
            capabilities = listOf("Text generation", "Multimodal", "Code generation"),
            rateLimits = RateLimits(
                requestsPerMinute = 60,
                tokensPerMinute = 60000
            ),
            documentationUrl = "https://ai.google.dev/docs"
        )
    )

    override suspend fun getAllModels(): Flow<List<AiModel>> {
        return flowOf(simulatedModels)
    }

    override suspend fun getModelsByCompany(company: String): Flow<List<AiModel>> {
        val filteredModels = simulatedModels.filter { it.company.equals(company, ignoreCase = true) }
        return flowOf(filteredModels)
    }

    override suspend fun searchModels(query: String): Flow<List<AiModel>> {
        val filteredModels = simulatedModels.filter { model ->
            model.name.contains(query, ignoreCase = true) ||
            model.company.contains(query, ignoreCase = true) ||
            model.id.contains(query, ignoreCase = true)
        }
        return flowOf(filteredModels)
    }

    override suspend fun getModelById(id: String): Flow<AiModel?> {
        val model = simulatedModels.find { it.id == id }
        return flowOf(model)
    }

    override suspend fun refreshModels(): Result<Unit> {
        // Simulate API call delay
        kotlinx.coroutines.delay(1000)
        return Result.success(Unit)
    }
}
