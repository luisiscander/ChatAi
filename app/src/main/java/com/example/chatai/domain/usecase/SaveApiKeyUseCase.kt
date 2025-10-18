package com.example.chatai.domain.usecase

import com.example.chatai.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class SaveApiKeyUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(apiKey: String): SaveApiKeyResult {
        return try {
            userPreferencesRepository.setApiKey(apiKey)
            SaveApiKeyResult.Success
        } catch (e: Exception) {
            SaveApiKeyResult.Error(e.message ?: "Error al guardar API key")
        }
    }
}

sealed class SaveApiKeyResult {
    object Success : SaveApiKeyResult()
    data class Error(val message: String) : SaveApiKeyResult()
}
