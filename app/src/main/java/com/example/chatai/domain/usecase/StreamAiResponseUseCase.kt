package com.example.chatai.domain.usecase

import com.example.chatai.domain.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StreamAiResponseUseCase @Inject constructor() {
    suspend operator fun invoke(
        userMessage: Message,
        apiKey: String,
        model: String = "gpt-4"
    ): Flow<StreamChunk> {
        return flow {
            // TODO: Implement real streaming API call to OpenRouter
            // For now, simulate streaming by emitting chunks gradually
            
            val fullResponse = "Hola! Soy un asistente de IA. Esta es una respuesta simulada que se está transmitiendo en tiempo real. Puedo ayudarte con muchas tareas diferentes."
            val words = fullResponse.split(" ")
            
            words.forEachIndexed { index, word ->
                kotlinx.coroutines.delay(200) // Simulate network delay
                
                emit(StreamChunk.Text(word + if (index < words.size - 1) " " else ""))
            }
            
            // Simulate token usage (in a real implementation, this would come from the API)
            val inputTokenCount = userMessage.content.length / 4 // Rough estimate
            val outputTokenCount = fullResponse.length / 4
            val totalTokenCount = inputTokenCount + outputTokenCount
            val estimatedCost = (totalTokenCount / 1000.0) * 0.01 // Rough estimate at $0.01 per 1K tokens
            
            emit(StreamChunk.Complete(
                inputTokens = inputTokenCount,
                outputTokens = outputTokenCount,
                totalTokens = totalTokenCount,
                estimatedCost = estimatedCost
            ))
        }
    }
}

sealed class StreamChunk {
    data class Text(val content: String) : StreamChunk()
    data class Complete(
        val inputTokens: Int? = null,
        val outputTokens: Int? = null,
        val totalTokens: Int? = null,
        val estimatedCost: Double? = null
    ) : StreamChunk()
    data class Error(val message: String) : StreamChunk()
}
