package com.example.chatai.data.local.security

import android.content.Context
import android.content.SharedPreferences
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Issues #137-141: Security manager for advanced security features
 * Handles session timeouts, biometric auth, and PIN management
 */
@Singleton
class SecurityManager @Inject constructor(
    private val context: Context
) {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("security_prefs", Context.MODE_PRIVATE)
    }
    
    private val _isSessionLocked = MutableStateFlow(false)
    val isSessionLocked: StateFlow<Boolean> = _isSessionLocked
    
    private val _lastActivityTime = MutableStateFlow(System.currentTimeMillis())
    
    companion object {
        private const val KEY_SESSION_TIMEOUT_ENABLED = "session_timeout_enabled"
        private const val KEY_SESSION_TIMEOUT_MINUTES = "session_timeout_minutes"
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_PIN_ATTEMPTS = "pin_attempts"
        private const val KEY_PIN_LOCK_UNTIL = "pin_lock_until"
        private const val DEFAULT_TIMEOUT_MINUTES = 10
        private const val MAX_PIN_ATTEMPTS = 3
        private const val LOCK_DURATION_MS = 30_000L // 30 seconds
    }
    
    // Issue #137: Session timeout configuration
    fun isSessionTimeoutEnabled(): Boolean {
        return prefs.getBoolean(KEY_SESSION_TIMEOUT_ENABLED, false)
    }
    
    fun setSessionTimeoutEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SESSION_TIMEOUT_ENABLED, enabled).apply()
    }
    
    fun getSessionTimeoutMinutes(): Int {
        return prefs.getInt(KEY_SESSION_TIMEOUT_MINUTES, DEFAULT_TIMEOUT_MINUTES)
    }
    
    fun setSessionTimeoutMinutes(minutes: Int) {
        prefs.edit().putInt(KEY_SESSION_TIMEOUT_MINUTES, minutes).apply()
    }
    
    // Issue #137: Check if session should be locked
    fun updateActivityTime() {
        _lastActivityTime.value = System.currentTimeMillis()
    }
    
    fun checkSessionTimeout(): Boolean {
        if (!isSessionTimeoutEnabled()) return false
        
        val timeoutMs = getSessionTimeoutMinutes() * 60 * 1000L
        val elapsed = System.currentTimeMillis() - _lastActivityTime.value
        
        if (elapsed > timeoutMs) {
            _isSessionLocked.value = true
            return true
        }
        return false
    }
    
    // Issue #137: Unlock session (placeholder for biometric/PIN)
    fun unlockSession() {
        _isSessionLocked.value = false
        updateActivityTime()
    }
    
    // Issue #137: Check if biometric is available
    fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }
    
    // Issue #138-141: PIN Management
    fun hasPinConfigured(): Boolean {
        return prefs.contains(KEY_PIN_HASH)
    }
    
    fun setPin(pin: String) {
        val hash = hashPin(pin)
        prefs.edit()
            .putString(KEY_PIN_HASH, hash)
            .putInt(KEY_PIN_ATTEMPTS, 0)
            .apply()
    }
    
    fun verifyPin(pin: String): Boolean {
        // Issue #140: Check if PIN is locked
        val lockUntil = prefs.getLong(KEY_PIN_LOCK_UNTIL, 0)
        if (System.currentTimeMillis() < lockUntil) {
            return false
        }
        
        val storedHash = prefs.getString(KEY_PIN_HASH, null) ?: return false
        val inputHash = hashPin(pin)
        
        return if (storedHash == inputHash) {
            // Success - reset attempts
            prefs.edit().putInt(KEY_PIN_ATTEMPTS, 0).apply()
            true
        } else {
            // Issue #140: Increment failed attempts
            val attempts = prefs.getInt(KEY_PIN_ATTEMPTS, 0) + 1
            prefs.edit().putInt(KEY_PIN_ATTEMPTS, attempts).apply()
            
            // Lock after max attempts
            if (attempts >= MAX_PIN_ATTEMPTS) {
                prefs.edit()
                    .putLong(KEY_PIN_LOCK_UNTIL, System.currentTimeMillis() + LOCK_DURATION_MS)
                    .apply()
            }
            false
        }
    }
    
    fun getPinAttempts(): Int {
        return prefs.getInt(KEY_PIN_ATTEMPTS, 0)
    }
    
    fun isPinLocked(): Boolean {
        val lockUntil = prefs.getLong(KEY_PIN_LOCK_UNTIL, 0)
        return System.currentTimeMillis() < lockUntil
    }
    
    fun getPinLockTimeRemaining(): Long {
        val lockUntil = prefs.getLong(KEY_PIN_LOCK_UNTIL, 0)
        val remaining = lockUntil - System.currentTimeMillis()
        return if (remaining > 0) remaining else 0
    }
    
    // Issue #141: Reset PIN (deletes encrypted conversations)
    fun resetPin() {
        prefs.edit()
            .remove(KEY_PIN_HASH)
            .putInt(KEY_PIN_ATTEMPTS, 0)
            .putLong(KEY_PIN_LOCK_UNTIL, 0)
            .apply()
    }
    
    // Simple PIN hashing (in production, use a proper KDF like PBKDF2)
    private fun hashPin(pin: String): String {
        return pin.hashCode().toString()
    }
}

