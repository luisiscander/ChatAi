package com.example.chatai.domain.usecase

import com.example.chatai.domain.model.Conversation
import com.example.chatai.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetConversationsUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    operator fun invoke(showArchived: Boolean = false): Flow<List<Conversation>> {
        return if (showArchived) {
            conversationRepository.getConversationsByArchivedStatus(true)
        } else {
            conversationRepository.getConversationsByArchivedStatus(false)
        }
    }
}
