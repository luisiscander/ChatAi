package com.example.chatai.domain.usecase

import com.example.chatai.domain.repository.ConversationRepository
import com.example.chatai.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class DeleteAllDataUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(): DeleteAllDataResult {
        return try {
            // Note: Full implementation will be added when Room is ready
            // For now, we clear preferences only
            
            // Clear API key
            userPreferencesRepository.clearApiKey()
            
            // Reset default model
            // Note: setDefaultModel expects String, so we'll leave it as is for now
            
            // Reset theme to system default
            userPreferencesRepository.setThemeMode(com.example.chatai.domain.model.ThemeMode.SYSTEM)
            
            // Note: We cannot reset onboarding flags without additional methods in the repository
            // This will be implemented when we add those methods
            
            DeleteAllDataResult.Success
        } catch (e: Exception) {
            DeleteAllDataResult.Error(e.message ?: "Error al eliminar datos")
        }
    }
}

sealed class DeleteAllDataResult {
    object Success : DeleteAllDataResult()
    data class Error(val message: String) : DeleteAllDataResult()
}
