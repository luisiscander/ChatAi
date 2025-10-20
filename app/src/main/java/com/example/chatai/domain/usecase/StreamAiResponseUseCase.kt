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
            
            emit(StreamChunk.Complete)
        }
    }
}

sealed class StreamChunk {
    data class Text(val content: String) : StreamChunk()
    object Complete : StreamChunk()
    data class Error(val message: String) : StreamChunk()
}
