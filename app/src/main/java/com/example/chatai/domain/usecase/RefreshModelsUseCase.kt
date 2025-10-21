package com.example.chatai.domain.usecase

import com.example.chatai.domain.repository.AiModelRepository
import javax.inject.Inject

/**
 * Use case for Issue #130: Force refresh models (pull-to-refresh)
 */
class RefreshModelsUseCase @Inject constructor(
    private val aiModelRepository: AiModelRepository
) {
    suspend operator fun invoke(): RefreshModelsResult {
        return try {
            val result = aiModelRepository.refreshModels()
            if (result.isSuccess) {
                RefreshModelsResult.Success
            } else {
                RefreshModelsResult.Error(
                    result.exceptionOrNull()?.message ?: "Error al actualizar modelos"
                )
            }
        } catch (e: Exception) {
            RefreshModelsResult.Error(e.message ?: "Error desconocido")
        }
    }
}

sealed class RefreshModelsResult {
    object Success : RefreshModelsResult()
    data class Error(val message: String) : RefreshModelsResult()
}

