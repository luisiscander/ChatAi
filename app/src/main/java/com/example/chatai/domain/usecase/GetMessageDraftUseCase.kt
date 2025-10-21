package com.example.chatai.domain.usecase

import com.example.chatai.domain.repository.UserPreferencesRepository
import javax.inject.Inject

/**
 * Use Case para obtener un borrador de mensaje guardado
 */
class GetMessageDraftUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(conversationId: String): String? {
        return try {
            userPreferencesRepository.getDraft(conversationId)
        } catch (e: Exception) {
            null
        }
    }
}

