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

    // Google Gemini models from OpenRouter
    // https://openrouter.ai/models
    private val simulatedModels = listOf(
        AiModel(
            id = "google/gemini-2.0-flash-exp:free",
            name = "Gemini 2.0 Flash (Experimental)",
            company = "Google",
            context = "1M tokens",
            pricing = ModelPricing(
                input = "Gratis",
                output = "Gratis",
                inputPricePer1k = 0.0,
                outputPricePer1k = 0.0
            ),
            description = "Versión experimental gratuita del Gemini 2.0 Flash con capacidades multimodales",
            capabilities = listOf("Text generation", "Multimodal", "Code generation", "Analysis"),
            rateLimits = RateLimits(
                requestsPerMinute = 60,
                tokensPerMinute = 60000
            ),
            documentationUrl = "https://ai.google.dev/gemini-api/docs"
        ),
        AiModel(
            id = "google/gemini-exp-1206:free",
            name = "Gemini Experimental 1206",
            company = "Google",
            context = "2M tokens",
            pricing = ModelPricing(
                input = "Gratis",
                output = "Gratis",
                inputPricePer1k = 0.0,
                outputPricePer1k = 0.0
            ),
            description = "Versión experimental gratuita con ventana de contexto extendida",
            capabilities = listOf("Text generation", "Long context", "Analysis", "Reasoning"),
            rateLimits = RateLimits(
                requestsPerMinute = 60,
                tokensPerMinute = 120000
            ),
            documentationUrl = "https://ai.google.dev/gemini-api/docs"
        ),
        AiModel(
            id = "google/gemini-flash-1.5-8b",
            name = "Gemini Flash 1.5 8B",
            company = "Google",
            context = "1M tokens",
            pricing = ModelPricing(
                input = "$0.000075 / 1K tokens",
                output = "$0.0003 / 1K tokens",
                inputPricePer1k = 0.000075,
                outputPricePer1k = 0.0003
            ),
            description = "Modelo rápido y económico ideal para tareas frecuentes",
            capabilities = listOf("Text generation", "Code completion", "Fast responses"),
            rateLimits = RateLimits(
                requestsPerMinute = 120,
                tokensPerMinute = 120000
            ),
            documentationUrl = "https://ai.google.dev/gemini-api/docs"
        ),
        AiModel(
            id = "google/gemini-flash-1.5",
            name = "Gemini Flash 1.5",
            company = "Google",
            context = "1M tokens",
            pricing = ModelPricing(
                input = "$0.00015 / 1K tokens",
                output = "$0.0006 / 1K tokens",
                inputPricePer1k = 0.00015,
                outputPricePer1k = 0.0006
            ),
            description = "Balance perfecto entre velocidad y capacidad",
            capabilities = listOf("Text generation", "Code generation", "Analysis", "Multimodal"),
            rateLimits = RateLimits(
                requestsPerMinute = 100,
                tokensPerMinute = 100000
            ),
            documentationUrl = "https://ai.google.dev/gemini-api/docs"
        ),
        AiModel(
            id = "google/gemini-pro-1.5",
            name = "Gemini Pro 1.5",
            company = "Google",
            context = "2M tokens",
            pricing = ModelPricing(
                input = "$0.00125 / 1K tokens",
                output = "$0.005 / 1K tokens",
                inputPricePer1k = 0.00125,
                outputPricePer1k = 0.005
            ),
            description = "Modelo más capaz de Google para razonamiento complejo y análisis profundo",
            capabilities = listOf("Text generation", "Advanced reasoning", "Multimodal", "Code generation", "Long context"),
            rateLimits = RateLimits(
                requestsPerMinute = 80,
                tokensPerMinute = 160000
            ),
            documentationUrl = "https://ai.google.dev/gemini-api/docs"
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
