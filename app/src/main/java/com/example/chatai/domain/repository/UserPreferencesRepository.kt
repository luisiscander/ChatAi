package com.example.chatai.domain.repository

interface UserPreferencesRepository {
    suspend fun isFirstTimeUser(): Boolean
    suspend fun setFirstTimeUserCompleted()
    suspend fun hasApiKey(): Boolean
    suspend fun setApiKey(apiKey: String)
    suspend fun getApiKey(): String?
    suspend fun clearApiKey()
}
