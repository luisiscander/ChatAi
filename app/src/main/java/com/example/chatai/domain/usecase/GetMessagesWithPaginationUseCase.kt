package com.example.chatai.domain.usecase

import com.example.chatai.domain.model.Message
import com.example.chatai.domain.repository.ConversationRepository
import javax.inject.Inject

/**
 * Issue #131: Load conversation with pagination
 * Loads only the most recent messages for better performance
 */
class GetMessagesWithPaginationUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(
        conversationId: String,
        limit: Int = 50 // Issue #131: Default to 50 messages
    ): GetMessagesWithPaginationResult {
        return try {
            val messages = conversationRepository.getRecentMessages(conversationId, limit)
            val totalCount = conversationRepository.getMessageCount(conversationId)
            
            GetMessagesWithPaginationResult.Success(
                messages = messages.reversed(), // Reverse to show oldest to newest
                totalCount = totalCount,
                hasMore = totalCount > messages.size
            )
        } catch (e: Exception) {
            GetMessagesWithPaginationResult.Error(e.message ?: "Error al cargar mensajes")
        }
    }
}

sealed class GetMessagesWithPaginationResult {
    data class Success(
        val messages: List<Message>,
        val totalCount: Int = 0,
        val hasMore: Boolean = false
    ) : GetMessagesWithPaginationResult()
    
    data class Error(val message: String) : GetMessagesWithPaginationResult()
}
