package com.example.chatai.domain.usecase

import com.example.chatai.domain.model.Message
import com.example.chatai.domain.repository.ConversationRepository
import javax.inject.Inject

class GetAiResponseUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(
        userMessage: Message,
        apiKey: String,
        model: String = "gpt-4"
    ): GetAiResponseResult {
        return try {
            // TODO: Implement real API call to OpenRouter
            // For now, simulate AI response
            val aiResponse = Message(
                id = java.util.UUID.randomUUID().toString(),
                conversationId = userMessage.conversationId,
                content = "Hola! Soy un asistente de IA. ¿En qué puedo ayudarte? Esta es una respuesta simulada.",
                isFromUser = false,
                timestamp = java.util.Date(),
                model = model
            )
            
            conversationRepository.addMessage(aiResponse)
            GetAiResponseResult.Success(aiResponse)
        } catch (e: Exception) {
            GetAiResponseResult.Error(e.message ?: "Error al obtener respuesta de IA")
        }
    }
}

sealed class GetAiResponseResult {
    data class Success(val message: Message) : GetAiResponseResult()
    data class Error(val message: String) : GetAiResponseResult()
}
