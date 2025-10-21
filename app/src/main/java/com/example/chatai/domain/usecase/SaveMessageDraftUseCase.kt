package com.example.chatai.domain.usecase

import com.example.chatai.domain.repository.UserPreferencesRepository
import javax.inject.Inject

/**
 * Use Case para guardar un mensaje como borrador
 */
class SaveMessageDraftUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(conversationId: String, draftText: String): SaveDraftResult {
        return try {
            userPreferencesRepository.saveDraft(conversationId, draftText)
            SaveDraftResult.Success
        } catch (e: Exception) {
            SaveDraftResult.Error("Error al guardar borrador: ${e.message}")
        }
    }
}

sealed class SaveDraftResult {
    object Success : SaveDraftResult()
    data class Error(val message: String) : SaveDraftResult()
}

