package com.example.chatai.domain.usecase

import com.example.chatai.domain.model.ModelComparison
import com.example.chatai.domain.model.ModelResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

/**
 * Use Case para enviar un mensaje a múltiples modelos simultáneamente
 */
class SendMessageToMultipleModelsUseCase @Inject constructor(
    private val streamAiResponseUseCase: StreamAiResponseUseCase
) {
    
    suspend operator fun invoke(
        conversationId: String,
        userMessage: String,
        modelIds: List<String>,
        apiKey: String
    ): Flow<MultiModelResponseChunk> {
        return flow {
            val startTime = System.currentTimeMillis()
            
            // Emitir estado inicial
            emit(MultiModelResponseChunk.Started(modelIds))
            
            // Simular respuestas para cada modelo
            // TODO: Implementar llamadas reales a la API en paralelo
            modelIds.forEach { modelId ->
                try {
                    val modelName = getModelName(modelId)
                    val responseStartTime = System.currentTimeMillis()
                    
                    // Emit que este modelo está empezando
                    emit(MultiModelResponseChunk.ModelStarted(modelId, modelName))
                    
                    // Simular streaming de respuesta
                    val simulatedResponse = generateSimulatedResponse(modelId, userMessage)
                    val words = simulatedResponse.split(" ")
                    val chunks = mutableListOf<String>()
                    
                    words.forEachIndexed { index, word ->
                        kotlinx.coroutines.delay(50) // Simular delay de streaming
                        val chunk = word + if (index < words.size - 1) " " else ""
                        chunks.add(chunk)
                        
                        emit(MultiModelResponseChunk.ContentChunk(
                            modelId = modelId,
                            chunk = chunk,
                            accumulatedContent = chunks.joinToString("")
                        ))
                    }
                    
                    val responseTime = System.currentTimeMillis() - responseStartTime
                    
                    // Emitir respuesta completa del modelo
                    emit(MultiModelResponseChunk.ModelComplete(
                        modelId = modelId,
                        response = ModelResponse(
                            modelId = modelId,
                            modelName = modelName,
                            content = simulatedResponse,
                            isStreaming = false,
                            tokensUsed = estimateTokens(simulatedResponse),
                            inputTokens = estimateTokens(userMessage),
                            outputTokens = estimateTokens(simulatedResponse),
                            estimatedCost = estimateCost(modelId, userMessage, simulatedResponse),
                            responseTimeMs = responseTime,
                            isComplete = true
                        )
                    ))
                    
                } catch (e: Exception) {
                    emit(MultiModelResponseChunk.ModelError(
                        modelId = modelId,
                        error = e.message ?: "Error desconocido"
                    ))
                }
            }
            
            // Emitir finalización de todas las respuestas
            val totalTime = System.currentTimeMillis() - startTime
            emit(MultiModelResponseChunk.AllComplete(totalTime))
        }
    }
    
    private fun getModelName(modelId: String): String {
        return when {
            modelId.contains("gpt-4-turbo", ignoreCase = true) -> "GPT-4 Turbo"
            modelId.contains("gpt-4", ignoreCase = true) -> "GPT-4"
            modelId.contains("gpt-3.5", ignoreCase = true) -> "GPT-3.5 Turbo"
            modelId.contains("claude-3-opus", ignoreCase = true) -> "Claude 3 Opus"
            modelId.contains("claude-3-sonnet", ignoreCase = true) -> "Claude 3 Sonnet"
            modelId.contains("llama-3-70b", ignoreCase = true) -> "Llama 3 70B"
            else -> modelId.split("/").lastOrNull() ?: modelId
        }
    }
    
    private fun generateSimulatedResponse(modelId: String, userMessage: String): String {
        // Generar diferentes respuestas según el modelo para simular variedad
        return when {
            modelId.contains("gpt", ignoreCase = true) -> 
                "Esta es una respuesta detallada de $modelId. Como modelo de OpenAI, puedo ayudarte con: ${userMessage.take(50)}..."
            modelId.contains("claude", ignoreCase = true) -> 
                "Como Claude, ofrezco una perspectiva diferente sobre: ${userMessage.take(50)}. Mi enfoque es analítico y considerado..."
            modelId.contains("llama", ignoreCase = true) -> 
                "Desde la perspectiva de Llama: ${userMessage.take(50)}. Mi respuesta es open-source y eficiente..."
            else -> 
                "Respuesta del modelo $modelId para: ${userMessage.take(50)}..."
        }
    }
    
    private fun estimateTokens(text: String): Int {
        // Estimación simple: ~1 token por 4 caracteres
        return (text.length / 4).coerceAtLeast(1)
    }
    
    private fun estimateCost(modelId: String, input: String, output: String): Double {
        val inputTokens = estimateTokens(input)
        val outputTokens = estimateTokens(output)
        
        // Precios aproximados por 1K tokens
        val (inputPrice, outputPrice) = when {
            modelId.contains("gpt-4-turbo", ignoreCase = true) -> 0.01 to 0.03
            modelId.contains("gpt-4", ignoreCase = true) -> 0.03 to 0.06
            modelId.contains("gpt-3.5", ignoreCase = true) -> 0.0005 to 0.0015
            modelId.contains("claude-3-opus", ignoreCase = true) -> 0.015 to 0.075
            modelId.contains("claude-3-sonnet", ignoreCase = true) -> 0.003 to 0.015
            modelId.contains("llama-3-70b", ignoreCase = true) -> 0.0007 to 0.0009
            else -> 0.01 to 0.01
        }
        
        val inputCost = (inputTokens / 1000.0) * inputPrice
        val outputCost = (outputTokens / 1000.0) * outputPrice
        
        return inputCost + outputCost
    }
}

/**
 * Chunks de respuesta para múltiples modelos
 */
sealed class MultiModelResponseChunk {
    data class Started(val modelIds: List<String>) : MultiModelResponseChunk()
    data class ModelStarted(val modelId: String, val modelName: String) : MultiModelResponseChunk()
    data class ContentChunk(
        val modelId: String,
        val chunk: String,
        val accumulatedContent: String
    ) : MultiModelResponseChunk()
    data class ModelComplete(val modelId: String, val response: ModelResponse) : MultiModelResponseChunk()
    data class ModelError(val modelId: String, val error: String) : MultiModelResponseChunk()
    data class AllComplete(val totalTimeMs: Long) : MultiModelResponseChunk()
}

