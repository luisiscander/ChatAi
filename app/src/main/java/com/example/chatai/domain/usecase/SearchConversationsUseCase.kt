package com.example.chatai.domain.usecase

import com.example.chatai.domain.model.Conversation
import com.example.chatai.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchConversationsUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    operator fun invoke(query: String): Flow<SearchResult> {
        return conversationRepository.getAllConversations().map { conversations ->
            if (query.isBlank()) {
                SearchResult.Success(conversations)
            } else {
                val filteredConversations = conversations.filter { conversation ->
                    conversation.title.contains(query, ignoreCase = true) ||
                    conversation.lastMessage?.contains(query, ignoreCase = true) == true
                }
                
                if (filteredConversations.isEmpty()) {
                    SearchResult.NoResults(query)
                } else {
                    SearchResult.Success(filteredConversations)
                }
            }
        }
    }
}

sealed class SearchResult {
    data class Success(val conversations: List<Conversation>) : SearchResult()
    data class NoResults(val query: String) : SearchResult()
}
