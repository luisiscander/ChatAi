package com.example.chatai.domain.usecase

import com.example.chatai.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class ValidateApiKeyUseCase @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(apiKey: String): ApiKeyValidationResult {
        return when {
            apiKey.isBlank() -> ApiKeyValidationResult.Empty
            !isValidFormat(apiKey) -> ApiKeyValidationResult.InvalidFormat
            else -> ApiKeyValidationResult.Valid
        }
    }
    
    private fun isValidFormat(apiKey: String): Boolean {
        // Validar formato básico de OpenRouter API key
        return apiKey.startsWith("sk-or-v1-") && apiKey.length > 20
    }
}

sealed class ApiKeyValidationResult {
    object Empty : ApiKeyValidationResult()
    object InvalidFormat : ApiKeyValidationResult()
    object Valid : ApiKeyValidationResult()
}
