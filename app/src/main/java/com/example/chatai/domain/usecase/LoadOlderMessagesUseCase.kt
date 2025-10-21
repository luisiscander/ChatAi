package com.example.chatai.domain.usecase

import com.example.chatai.domain.model.Message
import com.example.chatai.domain.repository.ConversationRepository
import javax.inject.Inject

/**
 * Use case for Issue #132: Load older messages when scrolling up
 * Implements infinite scroll by loading messages before a certain timestamp
 */
class LoadOlderMessagesUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(
        conversationId: String,
        beforeTimestamp: Long,
        limit: Int = 30
    ): LoadOlderMessagesResult {
        return try {
            val messages = conversationRepository.getMessagesBeforeTimestamp(
                conversationId = conversationId,
                beforeTimestamp = beforeTimestamp,
                limit = limit
            )
            
            LoadOlderMessagesResult.Success(
                messages = messages.reversed(), // Reverse to show oldest to newest
                hasMore = messages.size == limit // If we got a full page, there might be more
            )
        } catch (e: Exception) {
            LoadOlderMessagesResult.Error(e.message ?: "Error al cargar mensajes antiguos")
        }
    }
}

sealed class LoadOlderMessagesResult {
    data class Success(
        val messages: List<Message>,
        val hasMore: Boolean
    ) : LoadOlderMessagesResult()
    
    data class Error(val message: String) : LoadOlderMessagesResult()
}

