package com.example.chatai.domain.usecase

import com.example.chatai.domain.model.Message
import com.example.chatai.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    operator fun invoke(conversationId: String): Flow<List<Message>> {
        return conversationRepository.getMessagesByConversationId(conversationId)
    }
}
