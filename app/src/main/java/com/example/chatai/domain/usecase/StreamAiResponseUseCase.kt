package com.example.chatai.domain.usecase

import android.util.Log
import com.example.chatai.data.remote.api.OpenRouterApiService
import com.example.chatai.data.remote.dto.ChatCompletionRequest
import com.example.chatai.data.remote.dto.ChatCompletionResponse
import com.example.chatai.data.remote.dto.ChatMessage
import com.example.chatai.domain.model.Message
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StreamAiResponseUseCase @Inject constructor(
    private val apiService: OpenRouterApiService,
    private val gson: Gson
) {
    suspend operator fun invoke(
        userMessage: Message,
        apiKey: String,
        model: String = "google/gemini-2.0-flash-exp:free"
    ): Flow<StreamChunk> {
        return flow {
            try {
                val request = ChatCompletionRequest(
                    model = model,
                    messages = listOf(
                        ChatMessage(
                            role = "user",
                            content = userMessage.content
                        )
                    ),
                    stream = true
                )
                
                Log.d("StreamAiResponse", "Sending request to model: $model")
                Log.d("StreamAiResponse", "API Key length: ${apiKey.length}")
                
                val response = apiService.createChatCompletion(
                    authorization = "Bearer $apiKey",
                    request = request
                )
                
                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()?.string()
                    Log.e("StreamAiResponse", "Error ${response.code()}: ${response.message()}")
                    Log.e("StreamAiResponse", "Error body: $errorBody")
                    
                    val errorMessage = when (response.code()) {
                        401 -> "Error de autenticación (401). Verifica tu API Key en configuración."
                        403 -> "Acceso denegado (403). Este modelo puede no estar disponible con tu API key."
                        429 -> "Límite de solicitudes excedido (429). Intenta más tarde."
                        else -> "Error: ${response.code()} - ${response.message()}"
                    }
                    
                    emit(StreamChunk.Error(errorMessage))
                    return@flow
                }
                
                val responseBody = response.body()
                if (responseBody == null) {
                    emit(StreamChunk.Error("Empty response from API"))
                    return@flow
                }
                
                // Parse SSE (Server-Sent Events) stream
                val reader = responseBody.byteStream().bufferedReader()
                var usage: com.example.chatai.data.remote.dto.Usage? = null
                
                reader.useLines { lines ->
                    lines.forEach { line ->
                        if (line.startsWith("data: ")) {
                            val data = line.substring(6)
                            
                            // Skip [DONE] message
                            if (data == "[DONE]") {
                                return@forEach
                            }
                            
                            try {
                                val chunk = gson.fromJson(data, ChatCompletionResponse::class.java)
                                
                                // Extract usage if available
                                chunk.usage?.let { usage = it }
                                
                                // Extract content from delta
                                chunk.choices.firstOrNull()?.delta?.content?.let { content ->
                                    if (content.isNotBlank()) {
                                        emit(StreamChunk.Text(content))
                                    }
                                }
                            } catch (e: Exception) {
                                // Ignore parsing errors for individual chunks
                            }
                        }
                    }
                }
                
                // Emit completion with usage stats
                usage?.let {
                    val estimatedCost = (it.totalTokens / 1000.0) * 0.01 // Rough estimate
                    emit(StreamChunk.Complete(
                        inputTokens = it.promptTokens,
                        outputTokens = it.completionTokens,
                        totalTokens = it.totalTokens,
                        estimatedCost = estimatedCost
                    ))
                }
                
            } catch (e: Exception) {
                emit(StreamChunk.Error("Error: ${e.message}"))
            }
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
