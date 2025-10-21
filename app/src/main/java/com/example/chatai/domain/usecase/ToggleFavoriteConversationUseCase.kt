package com.example.chatai.domain.usecase

import com.example.chatai.domain.repository.ConversationRepository
import javax.inject.Inject

/**
 * Use Case para marcar/desmarcar conversación como favorita
 */
class ToggleFavoriteConversationUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(conversationId: String): ToggleFavoriteResult {
        return try {
            conversationRepository.toggleFavorite(conversationId)
            ToggleFavoriteResult.Success
        } catch (e: Exception) {
            ToggleFavoriteResult.Error("Error al actualizar favorito: ${e.message}")
        }
    }
}

sealed class ToggleFavoriteResult {
    object Success : ToggleFavoriteResult()
    data class Error(val message: String) : ToggleFavoriteResult()
}

