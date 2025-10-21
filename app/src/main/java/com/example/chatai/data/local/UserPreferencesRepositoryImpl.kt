package com.example.chatai.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.chatai.config.ApiConfig
import com.example.chatai.data.local.security.EncryptionHelper
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
        private const val TAG = "UserPrefsRepo"
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
        val encryptedApiKey = sharedPreferences.getString(KEY_API_KEY, null)
        !encryptedApiKey.isNullOrBlank() || !ApiConfig.DEFAULT_API_KEY.isNullOrBlank()
    }

    // Issue #134 & #135: Encrypt API Key before storing
    override suspend fun setApiKey(apiKey: String): Unit = withContext(Dispatchers.IO) {
        try {
            // TEMPORAL: Guardar sin encriptar para debugging del error 401
            // TODO: Restaurar encriptación una vez resuelto el problema
            sharedPreferences.edit().putString(KEY_API_KEY, apiKey).apply()
            
            // Verificar que se guardó correctamente
            val saved = sharedPreferences.getString(KEY_API_KEY, null)
            Log.d(TAG, "API Key saved successfully - Length: ${apiKey.length}")
            Log.d(TAG, "API Key verified in storage - Length: ${saved?.length ?: 0}")
            Log.d(TAG, "API Key masked: ${EncryptionHelper.maskForLogging(apiKey)}")
        } catch (e: Exception) {
            // Issue #135: Log error without exposing the key
            Log.e(TAG, "Failed to save API Key: ${e.message}")
            throw e
        }
    }

    // Issue #134 & #135: Decrypt API Key when retrieving
    override suspend fun getApiKey(): String? = withContext(Dispatchers.IO) {
        try {
            // Priority: 1. User-provided API key, 2. Default API key from config
            val userApiKey = sharedPreferences.getString(KEY_API_KEY, null)
            
            when {
                !userApiKey.isNullOrBlank() -> {
                    // TEMPORAL: Leer sin desencriptar para debugging del error 401
                    // TODO: Restaurar desencriptación una vez resuelto el problema
                    Log.d(TAG, "API Key retrieved from storage - Length: ${userApiKey.length}")
                    Log.d(TAG, "API Key masked: ${EncryptionHelper.maskForLogging(userApiKey)}")
                    Log.d(TAG, "API Key starts with: ${userApiKey.take(8)}")
                    userApiKey
                }
                !ApiConfig.DEFAULT_API_KEY.isNullOrBlank() -> {
                    // Issue #135: Mask default API key in logs
                    Log.d(TAG, "Using default API Key: ${EncryptionHelper.maskForLogging(ApiConfig.DEFAULT_API_KEY)}")
                    Log.w(TAG, "WARNING: Using hardcoded default API key - configure your own key in settings")
                    ApiConfig.DEFAULT_API_KEY
                }
                else -> {
                    Log.w(TAG, "No API Key found")
                    null
                }
            }
        } catch (e: Exception) {
            // Issue #135: Log error without exposing keys
            Log.e(TAG, "Failed to retrieve API Key: ${e.message}")
            // Fallback to default if retrieval fails
            Log.w(TAG, "Falling back to default API Key")
            ApiConfig.DEFAULT_API_KEY
        }
    }

    override suspend fun clearApiKey(): Unit = withContext(Dispatchers.IO) {
        sharedPreferences.edit().remove(KEY_API_KEY).apply()
        // Issue #135: Safe logging
        Log.d(TAG, "API Key cleared")
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
