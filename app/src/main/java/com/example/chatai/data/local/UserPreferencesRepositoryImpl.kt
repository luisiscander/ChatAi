package com.example.chatai.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.chatai.config.ApiConfig
import com.example.chatai.domain.model.ThemeMode
import com.example.chatai.domain.repository.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserPreferencesRepository {

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    companion object {
        private const val PREFS_NAME = "chat_ai_preferences"
        private const val KEY_FIRST_TIME_USER = "first_time_user"
        private const val KEY_API_KEY = "api_key"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_DEFAULT_MODEL = "default_model"
        private const val KEY_MONTHLY_USAGE_LIMIT = "monthly_usage_limit"
    }

    override suspend fun isFirstTimeUser(): Boolean = withContext(Dispatchers.IO) {
        sharedPreferences.getBoolean(KEY_FIRST_TIME_USER, true)
    }

    override suspend fun setFirstTimeUserCompleted() = withContext(Dispatchers.IO) {
        sharedPreferences.edit().putBoolean(KEY_FIRST_TIME_USER, false).apply()
    }

    override suspend fun hasApiKey(): Boolean = withContext(Dispatchers.IO) {
        // Check if API key is available in configuration or user input
        val userApiKey = sharedPreferences.getString(KEY_API_KEY, null)
        !userApiKey.isNullOrBlank() || !ApiConfig.DEFAULT_API_KEY.isNullOrBlank()
    }

    override suspend fun setApiKey(apiKey: String) = withContext(Dispatchers.IO) {
        // Store user-provided API key in SharedPreferences
        sharedPreferences.edit().putString(KEY_API_KEY, apiKey).apply()
    }

    override suspend fun getApiKey(): String? = withContext(Dispatchers.IO) {
        // Priority: 1. User-provided API key, 2. Default API key from config
        val userApiKey = sharedPreferences.getString(KEY_API_KEY, null)
        
        when {
            !userApiKey.isNullOrBlank() -> userApiKey
            !ApiConfig.DEFAULT_API_KEY.isNullOrBlank() -> ApiConfig.DEFAULT_API_KEY
            else -> null
        }
    }

    override suspend fun clearApiKey() = withContext(Dispatchers.IO) {
        sharedPreferences.edit().remove(KEY_API_KEY).apply()
    }

    override suspend fun getThemeMode(): ThemeMode = withContext(Dispatchers.IO) {
        val themeModeString = sharedPreferences.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name)
        try {
            ThemeMode.valueOf(themeModeString ?: ThemeMode.SYSTEM.name)
        } catch (e: IllegalArgumentException) {
            ThemeMode.SYSTEM
        }
    }

    override suspend fun setThemeMode(themeMode: ThemeMode) = withContext(Dispatchers.IO) {
        sharedPreferences.edit().putString(KEY_THEME_MODE, themeMode.name).apply()
    }

    override suspend fun getDefaultModel(): String? = withContext(Dispatchers.IO) {
        sharedPreferences.getString(KEY_DEFAULT_MODEL, null)
    }

    override suspend fun setDefaultModel(modelId: String) = withContext(Dispatchers.IO) {
        sharedPreferences.edit().putString(KEY_DEFAULT_MODEL, modelId).apply()
    }
    
    override suspend fun getMonthlyUsageLimit(): Double? = withContext(Dispatchers.IO) {
        val limit = sharedPreferences.getFloat(KEY_MONTHLY_USAGE_LIMIT, -1f)
        if (limit == -1f) null else limit.toDouble()
    }
    
    override suspend fun setMonthlyUsageLimit(limit: Double) = withContext(Dispatchers.IO) {
        sharedPreferences.edit().putFloat(KEY_MONTHLY_USAGE_LIMIT, limit.toFloat()).apply()
    }
    
    override suspend fun saveDraft(conversationId: String, draftText: String) = withContext(Dispatchers.IO) {
        sharedPreferences.edit().putString("draft_$conversationId", draftText).apply()
    }
    
    override suspend fun getDraft(conversationId: String): String? = withContext(Dispatchers.IO) {
        sharedPreferences.getString("draft_$conversationId", null)
    }
    
    override suspend fun clearDraft(conversationId: String) = withContext(Dispatchers.IO) {
        sharedPreferences.edit().remove("draft_$conversationId").apply()
    }
}
