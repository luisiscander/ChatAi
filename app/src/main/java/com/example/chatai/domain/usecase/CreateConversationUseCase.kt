package com.example.chatai.domain.usecase

import com.example.chatai.domain.model.Conversation
import com.example.chatai.domain.repository.ConversationRepository
import javax.inject.Inject

class CreateConversationUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(
        title: String = "Nueva conversación",
        model: String = "gpt-4"
    ): CreateConversationResult {
        return try {
            val conversation = conversationRepository.createConversation(title, model)
            CreateConversationResult.Success(conversation)
        } catch (e: Exception) {
            CreateConversationResult.Error(e.message ?: "Error al crear conversación")
        }
    }
}

sealed class CreateConversationResult {
    data class Success(val conversation: Conversation) : CreateConversationResult()
    data class Error(val message: String) : CreateConversationResult()
}
