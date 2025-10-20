package com.example.chatai.domain.usecase

import com.example.chatai.domain.model.Message
import com.example.chatai.domain.repository.ConversationRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(
        message: Message,
        apiKey: String,
        model: String = "gpt-4"
    ): SendMessageResult {
        return try {
            // TODO: Implement real API call to OpenRouter
            // For now, simulate successful message sending
            conversationRepository.addMessage(message)
            SendMessageResult.Success(message)
        } catch (e: Exception) {
            SendMessageResult.Error(e.message ?: "Error al enviar mensaje")
        }
    }
}

sealed class SendMessageResult {
    data class Success(val message: Message) : SendMessageResult()
    data class Error(val message: String) : SendMessageResult()
}
