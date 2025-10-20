package com.example.chatai.domain.usecase

import javax.inject.Inject

class ValidateApiKeyConnectionUseCase @Inject constructor() {
    suspend operator fun invoke(apiKey: String): ValidateApiKeyConnectionResult {
        return try {
            // TODO: Implement real API key validation with OpenRouter
            // For now, simulate validation based on format
            if (apiKey.startsWith("sk-or-v1-") && apiKey.length > 30) {
                ValidateApiKeyConnectionResult.Valid
            } else {
                ValidateApiKeyConnectionResult.Invalid("API key inválida")
            }
        } catch (e: Exception) {
            ValidateApiKeyConnectionResult.Error(e.message ?: "Error al validar API key")
        }
    }
}

sealed class ValidateApiKeyConnectionResult {
    object Valid : ValidateApiKeyConnectionResult()
    data class Invalid(val message: String) : ValidateApiKeyConnectionResult()
    data class Error(val message: String) : ValidateApiKeyConnectionResult()
}
