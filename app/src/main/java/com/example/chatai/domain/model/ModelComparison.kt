package com.example.chatai.domain.model

import java.util.*

/**
 * Representa una comparación de respuestas entre múltiples modelos de IA
 */
data class ModelComparison(
    val id: String = UUID.randomUUID().toString(),
    val conversationId: String,
    val userMessage: String,
    val selectedModels: List<String>, // IDs de los modelos seleccionados
    val responses: Map<String, ModelResponse> = emptyMap(), // Modelo ID -> Respuesta
    val primaryResponseModelId: String? = null, // El modelo marcado como mejor
    val timestamp: Date = Date()
)

/**
 * Respuesta individual de un modelo en una comparación
 */
data class ModelResponse(
    val modelId: String,
    val modelName: String,
    val content: String,
    val isStreaming: Boolean = false,
    val streamingChunks: List<String> = emptyList(),
    val tokensUsed: Int? = null,
    val inputTokens: Int? = null,
    val outputTokens: Int? = null,
    val estimatedCost: Double? = null,
    val responseTimeMs: Long? = null,
    val timestamp: Date = Date(),
    val isComplete: Boolean = false,
    val error: String? = null
)

/**
 * Estado de la interfaz de comparación
 */
data class ComparisonMode(
    val isActive: Boolean = false,
    val selectedModels: List<AiModel> = emptyList(),
    val maxModels: Int = 4,
    val totalEstimatedCost: Double = 0.0
)

