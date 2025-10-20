package com.example.chatai.domain.usecase

import com.example.chatai.domain.model.Message
import com.example.chatai.domain.repository.ConversationRepository
import javax.inject.Inject

class GetMessagesWithPaginationUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(
        conversationId: String,
        offset: Int = 0,
        limit: Int = 20
    ): GetMessagesWithPaginationResult {
        return try {
            // TODO: Implement real pagination with Room database
            // For now, simulate pagination with empty results
            val messages = emptyList<Message>()
            GetMessagesWithPaginationResult.Success(messages)
        } catch (e: Exception) {
            GetMessagesWithPaginationResult.Error(e.message ?: "Error al cargar mensajes")
        }
    }
}

sealed class GetMessagesWithPaginationResult {
    data class Success(val messages: List<Message>) : GetMessagesWithPaginationResult()
    data class Error(val message: String) : GetMessagesWithPaginationResult()
}
