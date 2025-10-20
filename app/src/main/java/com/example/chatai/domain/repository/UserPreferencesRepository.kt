package com.example.chatai.domain.repository

import com.example.chatai.domain.model.ThemeMode

interface UserPreferencesRepository {
    suspend fun isFirstTimeUser(): Boolean
    suspend fun setFirstTimeUserCompleted()
    suspend fun hasApiKey(): Boolean
    suspend fun setApiKey(apiKey: String)
    suspend fun getApiKey(): String?
    suspend fun clearApiKey()
    
    // Theme preferences
    suspend fun getThemeMode(): ThemeMode
    suspend fun setThemeMode(themeMode: ThemeMode)
    
    // Default model preferences
    suspend fun getDefaultModel(): String?
    suspend fun setDefaultModel(modelId: String)
}
