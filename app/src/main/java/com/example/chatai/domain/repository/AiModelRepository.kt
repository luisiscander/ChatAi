package com.example.chatai.domain.repository

import com.example.chatai.domain.model.AiModel
import kotlinx.coroutines.flow.Flow

interface AiModelRepository {
    suspend fun getAllModels(): Flow<List<AiModel>>
    suspend fun getModelsByCompany(company: String): Flow<List<AiModel>>
    suspend fun searchModels(query: String): Flow<List<AiModel>>
    suspend fun getModelById(id: String): Flow<AiModel?>
    suspend fun refreshModels(): Result<Unit>
}
