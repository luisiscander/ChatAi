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
    
    // Usage limit preferences
    suspend fun getMonthlyUsageLimit(): Double?
    suspend fun setMonthlyUsageLimit(limit: Double)
    
    // Draft preferences
    suspend fun saveDraft(conversationId: String, draftText: String)
    suspend fun getDraft(conversationId: String): String?
    suspend fun clearDraft(conversationId: String)
}
