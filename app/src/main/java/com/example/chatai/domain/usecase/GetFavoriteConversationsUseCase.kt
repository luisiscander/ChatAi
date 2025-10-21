package com.example.chatai.domain.usecase

import com.example.chatai.domain.model.Conversation
import com.example.chatai.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case para obtener solo conversaciones favoritas
 */
class GetFavoriteConversationsUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    operator fun invoke(): Flow<List<Conversation>> {
        return conversationRepository.getFavoriteConversations()
    }
}

