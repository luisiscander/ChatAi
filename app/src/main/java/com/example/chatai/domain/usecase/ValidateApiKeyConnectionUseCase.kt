package com.example.chatai.domain.usecase

import com.example.chatai.data.remote.api.OpenRouterApiService
import retrofit2.HttpException
import javax.inject.Inject

class ValidateApiKeyConnectionUseCase @Inject constructor(
    private val openRouterApiService: OpenRouterApiService
) {
    suspend operator fun invoke(apiKey: String): ValidateApiKeyConnectionResult {
        return try {
            // Validate API key format first
            if (!isValidFormat(apiKey)) {
                return ValidateApiKeyConnectionResult.Invalid("Formato de API key inválido")
            }
            
            // Make real API call to validate the key
            val response = openRouterApiService.getModels("Bearer $apiKey")
            
            when {
                response.isSuccessful -> {
                    ValidateApiKeyConnectionResult.Valid
                }
                response.code() == 401 -> {
                    ValidateApiKeyConnectionResult.Invalid("API key inválida o no autorizada")
                }
                response.code() == 403 -> {
                    ValidateApiKeyConnectionResult.Invalid("API key no tiene permisos suficientes")
                }
                else -> {
                    ValidateApiKeyConnectionResult.Error("Error del servidor: ${response.code()}")
                }
            }
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> ValidateApiKeyConnectionResult.Invalid("API key inválida o no autorizada")
                403 -> ValidateApiKeyConnectionResult.Invalid("API key no tiene permisos suficientes")
                else -> ValidateApiKeyConnectionResult.Error("Error HTTP: ${e.code()}")
            }
        } catch (e: Exception) {
            ValidateApiKeyConnectionResult.Error("Error de conexión: ${e.message ?: "Error desconocido"}")
        }
    }
    
    private fun isValidFormat(apiKey: String): Boolean {
        // Validate OpenRouter API key format
        return apiKey.startsWith("sk-or-v1-") && apiKey.length > 20
    }
}

sealed class ValidateApiKeyConnectionResult {
    object Valid : ValidateApiKeyConnectionResult()
    data class Invalid(val message: String) : ValidateApiKeyConnectionResult()
    data class Error(val message: String) : ValidateApiKeyConnectionResult()
}
