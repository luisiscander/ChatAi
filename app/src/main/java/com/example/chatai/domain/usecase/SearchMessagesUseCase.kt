package com.example.chatai.domain.usecase

import com.example.chatai.domain.model.Message
import com.example.chatai.domain.repository.ConversationRepository
import javax.inject.Inject

/**
 * Use case for Issue #133: Search in all messages of a conversation
 * Searches through the entire message history, not just loaded messages
 */
class SearchMessagesUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(conversationId: String, query: String): SearchMessagesResult {
        return try {
            if (query.isBlank()) {
                return SearchMessagesResult.Success(emptyList())
            }
            
            val messages = conversationRepository.searchMessagesInConversation(
                conversationId = conversationId,
                query = query
            )
            
            SearchMessagesResult.Success(messages.reversed()) // Reverse to show oldest to newest
        } catch (e: Exception) {
            SearchMessagesResult.Error(e.message ?: "Error al buscar mensajes")
        }
    }
}

sealed class SearchMessagesResult {
    data class Success(val messages: List<Message>) : SearchMessagesResult()
    data class Error(val message: String) : SearchMessagesResult()
}

