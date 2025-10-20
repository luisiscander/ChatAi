package com.example.chatai.domain.usecase

import com.example.chatai.domain.model.Conversation
import com.example.chatai.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetConversationByIdUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(conversationId: String): Conversation? {
        return conversationRepository.getConversationById(conversationId).first()
    }
}
