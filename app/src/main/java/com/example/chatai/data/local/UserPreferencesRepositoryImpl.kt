package com.example.chatai.data.local

import android.content.Context
import android.content.SharedPreferences
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
    }

    override suspend fun isFirstTimeUser(): Boolean = withContext(Dispatchers.IO) {
        sharedPreferences.getBoolean(KEY_FIRST_TIME_USER, true)
    }

    override suspend fun setFirstTimeUserCompleted() = withContext(Dispatchers.IO) {
        sharedPreferences.edit().putBoolean(KEY_FIRST_TIME_USER, false).apply()
    }

    override suspend fun hasApiKey(): Boolean = withContext(Dispatchers.IO) {
        val apiKey = sharedPreferences.getString(KEY_API_KEY, null)
        !apiKey.isNullOrBlank()
    }

    override suspend fun setApiKey(apiKey: String) = withContext(Dispatchers.IO) {
        sharedPreferences.edit().putString(KEY_API_KEY, apiKey).apply()
    }

    override suspend fun getApiKey(): String? = withContext(Dispatchers.IO) {
        sharedPreferences.getString(KEY_API_KEY, null)
    }

    override suspend fun clearApiKey() = withContext(Dispatchers.IO) {
        sharedPreferences.edit().remove(KEY_API_KEY).apply()
    }
}
