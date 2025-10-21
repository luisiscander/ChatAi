package com.example.chatai.data.local.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Issue #134 & #135: Secure API Key encryption using Android Keystore
 * Provides AES-256 encryption for sensitive data like API keys
 */
class EncryptionHelper {
    
    companion object {
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS = "ChatAiApiKeyAlias"
        private const val TRANSFORMATION = "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_GCM}/${KeyProperties.ENCRYPTION_PADDING_NONE}"
        private const val GCM_TAG_LENGTH = 128
        private const val IV_SEPARATOR = "]" // Separator between IV and encrypted data
        
        /**
         * Issue #134: Encrypt data using Android Keystore
         * @param plainText The text to encrypt (e.g., API key)
         * @return Base64 encoded string containing IV and encrypted data
         */
        fun encrypt(plainText: String): String {
            try {
                val cipher = Cipher.getInstance(TRANSFORMATION)
                cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
                
                val iv = cipher.iv
                val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
                
                // Combine IV and encrypted data
                val ivString = Base64.encodeToString(iv, Base64.NO_WRAP)
                val encryptedString = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
                
                return "$ivString$IV_SEPARATOR$encryptedString"
            } catch (e: Exception) {
                // Issue #135: Never log the plainText
                android.util.Log.e("EncryptionHelper", "Encryption failed: ${e.message}")
                throw SecurityException("Failed to encrypt data", e)
            }
        }
        
        /**
         * Issue #134: Decrypt data using Android Keystore
         * @param encryptedData Base64 encoded string containing IV and encrypted data
         * @return Decrypted plaintext
         */
        fun decrypt(encryptedData: String): String {
            try {
                val parts = encryptedData.split(IV_SEPARATOR)
                if (parts.size != 2) {
                    throw IllegalArgumentException("Invalid encrypted data format")
                }
                
                val iv = Base64.decode(parts[0], Base64.NO_WRAP)
                val encryptedBytes = Base64.decode(parts[1], Base64.NO_WRAP)
                
                val cipher = Cipher.getInstance(TRANSFORMATION)
                val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
                cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), spec)
                
                val decryptedBytes = cipher.doFinal(encryptedBytes)
                return String(decryptedBytes, Charsets.UTF_8)
            } catch (e: Exception) {
                // Issue #135: Never log the encrypted data or result
                android.util.Log.e("EncryptionHelper", "Decryption failed: ${e.message}")
                throw SecurityException("Failed to decrypt data", e)
            }
        }
        
        /**
         * Issue #134: Get or create secret key from Android Keystore
         * Uses AES-256 encryption
         */
        private fun getOrCreateSecretKey(): SecretKey {
            val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
            keyStore.load(null)
            
            // Check if key already exists
            if (keyStore.containsAlias(KEY_ALIAS)) {
                return keyStore.getKey(KEY_ALIAS, null) as SecretKey
            }
            
            // Create new key
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                KEYSTORE_PROVIDER
            )
            
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256) // Issue #134: AES-256
                .setUserAuthenticationRequired(false) // No biometric for API keys
                .build()
            
            keyGenerator.init(keyGenParameterSpec)
            return keyGenerator.generateKey()
        }
        
        /**
         * Issue #135: Mask sensitive data for logging
         * @param sensitiveData The data to mask
         * @return Masked string (e.g., "sk-****...****abcd")
         */
        fun maskForLogging(sensitiveData: String): String {
            return when {
                sensitiveData.length <= 8 -> "****"
                else -> {
                    val start = sensitiveData.substring(0, 3)
                    val end = sensitiveData.substring(sensitiveData.length - 4)
                    "$start****...****$end"
                }
            }
        }
    }
}

