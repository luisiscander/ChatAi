package com.example.chatai.data.local.dao

import androidx.room.*
import com.example.chatai.data.local.entity.AiModelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AiModelDao {
    // Issue #127-128: Get all models from cache
    @Query("SELECT * FROM ai_models ORDER BY company, name")
    fun getAllModels(): Flow<List<AiModelEntity>>
    
    // Get models by company
    @Query("SELECT * FROM ai_models WHERE company = :company ORDER BY name")
    fun getModelsByCompany(company: String): Flow<List<AiModelEntity>>
    
    // Search models
    @Query("SELECT * FROM ai_models WHERE name LIKE '%' || :query || '%' OR company LIKE '%' || :query || '%' OR id LIKE '%' || :query || '%' ORDER BY company, name")
    fun searchModels(query: String): Flow<List<AiModelEntity>>
    
    // Get model by id
    @Query("SELECT * FROM ai_models WHERE id = :id")
    fun getModelById(id: String): Flow<AiModelEntity?>
    
    // Issue #127: Insert or update models (for caching)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModels(models: List<AiModelEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModel(model: AiModelEntity)
    
    // Check if cache exists
    @Query("SELECT COUNT(*) FROM ai_models")
    suspend fun getModelsCount(): Int
    
    // Issue #129: Check cache age for background updates
    @Query("SELECT MAX(lastUpdated) FROM ai_models")
    suspend fun getLastUpdateTimestamp(): Long?
    
    // Clear all models (for refresh)
    @Query("DELETE FROM ai_models")
    suspend fun clearAllModels()
}

