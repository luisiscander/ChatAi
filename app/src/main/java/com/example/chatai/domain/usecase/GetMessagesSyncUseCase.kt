package com.example.chatai.domain.usecase

import com.example.chatai.domain.model.Message
import com.example.chatai.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetMessagesSyncUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(conversationId: String): List<Message> {
        return conversationRepository.getMessagesByConversationId(conversationId).first()
    }
}
