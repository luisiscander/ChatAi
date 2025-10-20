package com.example.chatai.domain.usecase

import com.example.chatai.domain.model.AiModel
import com.example.chatai.domain.repository.AiModelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAvailableModelsUseCase @Inject constructor(
    private val aiModelRepository: AiModelRepository
) {
    suspend operator fun invoke(): Flow<GetModelsResult> {
        return aiModelRepository.getAllModels().map { models ->
            if (models.isEmpty()) {
                GetModelsResult.Loading
            } else {
                GetModelsResult.Success(models)
            }
        }
    }
}

sealed class GetModelsResult {
    object Loading : GetModelsResult()
    data class Success(val models: List<AiModel>) : GetModelsResult()
    data class Error(val message: String) : GetModelsResult()
}
