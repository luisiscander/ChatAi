package com.example.chatai.domain.usecase

import com.example.chatai.domain.model.Message
import com.example.chatai.domain.repository.ConversationRepository
import javax.inject.Inject

/**
 * Use case for Issue #131: Load conversation with paginated messages
 * Loads only the most recent N messages for better performance
 */
class LoadPaginatedMessagesUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(conversationId: String, limit: Int = 50): PaginatedMessagesResult {
        return try {
            val messages = conversationRepository.getRecentMessages(conversationId, limit)
            val totalCount = conversationRepository.getMessageCount(conversationId)
            val hasMore = totalCount > messages.size
            
            PaginatedMessagesResult.Success(
                messages = messages.reversed(), // Reverse to show oldest to newest
                totalCount = totalCount,
                hasMore = hasMore
            )
        } catch (e: Exception) {
            PaginatedMessagesResult.Error(e.message ?: "Error al cargar mensajes")
        }
    }
}

sealed class PaginatedMessagesResult {
    data class Success(
        val messages: List<Message>,
        val totalCount: Int,
        val hasMore: Boolean
    ) : PaginatedMessagesResult()
    
    data class Error(val message: String) : PaginatedMessagesResult()
}

