package com.example.chatai.domain.usecase

import com.example.chatai.domain.repository.UserPreferencesRepository
import javax.inject.Inject

/**
 * Use Case para limpiar un borrador de mensaje
 */
class ClearMessageDraftUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(conversationId: String) {
        try {
            userPreferencesRepository.clearDraft(conversationId)
        } catch (e: Exception) {
            // Silent fail
        }
    }
}

