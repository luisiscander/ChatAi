package com.example.chatai.data.local

import com.example.chatai.data.local.dao.AiModelDao
import com.example.chatai.data.local.entity.AiModelEntity
import com.example.chatai.domain.model.AiModel
import com.example.chatai.domain.model.ModelPricing
import com.example.chatai.domain.model.RateLimits
import com.example.chatai.domain.repository.AiModelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiModelRepositoryImpl @Inject constructor(
    private val aiModelDao: AiModelDao // Issue #127-130: DAO for caching
) : AiModelRepository {
    
    // Issue #129: Cache validity period (24 hours)
    private val CACHE_VALIDITY_PERIOD = 24 * 60 * 60 * 1000L // 24 hours in milliseconds

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

    // Issue #127-128: Load from cache first, fetch if empty
    override suspend fun getAllModels(): Flow<List<AiModel>> {
        return aiModelDao.getAllModels()
            .map { entities -> entities.map { it.toDomain() } }
            .onStart {
                // Issue #127: First load - populate cache if empty
                if (aiModelDao.getModelsCount() == 0) {
                    loadInitialModels()
                } else {
                    // Issue #129: Check for background update
                    checkAndUpdateCacheInBackground()
                }
            }
    }

    override suspend fun getModelsByCompany(company: String): Flow<List<AiModel>> {
        return aiModelDao.getModelsByCompany(company)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun searchModels(query: String): Flow<List<AiModel>> {
        return aiModelDao.searchModels(query)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getModelById(id: String): Flow<AiModel?> {
        return aiModelDao.getModelById(id)
            .map { entity -> entity?.toDomain() }
    }

    // Issue #130: Force refresh (pull-to-refresh)
    override suspend fun refreshModels(): Result<Unit> {
        return try {
            // Simulate API call delay
            kotlinx.coroutines.delay(1500)
            
            // Clear old cache and load new models
            aiModelDao.clearAllModels()
            loadInitialModels()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Issue #127: Load initial models into cache
    private suspend fun loadInitialModels() {
        val entities = simulatedModels.map { AiModelEntity.fromDomain(it) }
        aiModelDao.insertModels(entities)
    }
    
    // Issue #129: Background update check
    private fun checkAndUpdateCacheInBackground() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val lastUpdate = aiModelDao.getLastUpdateTimestamp() ?: 0L
                val currentTime = System.currentTimeMillis()
                
                // If cache is older than 24 hours, update in background
                if (currentTime - lastUpdate > CACHE_VALIDITY_PERIOD) {
                    // Simulate checking for new models
                    kotlinx.coroutines.delay(2000)
                    // In a real implementation, fetch from API and update cache
                    loadInitialModels()
                }
            } catch (e: Exception) {
                // Silent failure for background update
            }
        }
    }
}
