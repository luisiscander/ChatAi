package com.example.chatai.domain.usecase

import com.example.chatai.domain.repository.ConversationRepository
import javax.inject.Inject

class DeleteMessageUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(messageId: String): DeleteMessageResult {
        return try {
            conversationRepository.deleteMessage(messageId)
            DeleteMessageResult.Success
        } catch (e: Exception) {
            DeleteMessageResult.Error(e.message ?: "Error al eliminar mensaje")
        }
    }
}

sealed class DeleteMessageResult {
    object Success : DeleteMessageResult()
    data class Error(val message: String) : DeleteMessageResult()
}
