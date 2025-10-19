package com.example.chatai.domain.usecase

import com.example.chatai.domain.repository.ConversationRepository
import javax.inject.Inject

class ArchiveConversationUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(conversationId: String): ArchiveConversationResult {
        return try {
            conversationRepository.archiveConversation(conversationId)
            ArchiveConversationResult.Success
        } catch (e: Exception) {
            ArchiveConversationResult.Error(e.message ?: "Error al archivar conversación")
        }
    }
}

class UnarchiveConversationUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(conversationId: String): ArchiveConversationResult {
        return try {
            conversationRepository.unarchiveConversation(conversationId)
            ArchiveConversationResult.Success
        } catch (e: Exception) {
            ArchiveConversationResult.Error(e.message ?: "Error al desarchivar conversación")
        }
    }
}

sealed class ArchiveConversationResult {
    object Success : ArchiveConversationResult()
    data class Error(val message: String) : ArchiveConversationResult()
}
