package com.brokenprotocol.firebaseauthdemo.security

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Simplified secure storage for Django token and user session state.
 * Firebase handles its own session management automatically.
 */
@Singleton
class SimpleSecureStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "auth_prefs",
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val KEY_ALIAS = "AuthDemoKey"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val GCM_IV_LENGTH = 12
        private const val GCM_TAG_LENGTH = 16
        
        // Storage keys
        private const val KEY_DJANGO_TOKEN = "django_token"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_TOKEN_EXPIRY = "token_expiry"
    }
    
    /**
     * Store Django token securely
     */
    fun storeDjangoToken(token: String): Boolean {
        return try {
            val encryptedToken = encrypt(token)
            sharedPreferences.edit()
                .putString(KEY_DJANGO_TOKEN, encryptedToken)
                .apply()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Retrieve Django token
     */
    fun getDjangoToken(): String? {
        return try {
            val encryptedToken = sharedPreferences.getString(KEY_DJANGO_TOKEN, null)
            encryptedToken?.let { decrypt(it) }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Store user email for reference
     */
    fun storeUserEmail(email: String): Boolean {
        return try {
            val encryptedEmail = encrypt(email)
            sharedPreferences.edit()
                .putString(KEY_USER_EMAIL, encryptedEmail)
                .apply()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Retrieve user email
     */
    fun getUserEmail(): String? {
        return try {
            val encryptedEmail = sharedPreferences.getString(KEY_USER_EMAIL, null)
            encryptedEmail?.let { decrypt(it) }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Store token expiry timestamp
     */
    fun storeTokenExpiry(timestamp: Long) {
        sharedPreferences.edit()
            .putLong(KEY_TOKEN_EXPIRY, timestamp)
            .apply()
    }
    
    /**
     * Get token expiry timestamp
     */
    fun getTokenExpiry(): Long {
        return sharedPreferences.getLong(KEY_TOKEN_EXPIRY, 0L)
    }
    
    /**
     * Check if Django token is expired
     */
    fun isDjangoTokenExpired(): Boolean {
        val expiry = getTokenExpiry()
        return expiry > 0 && System.currentTimeMillis() > expiry
    }
    
    /**
     * Clear all stored data
     */
    fun clearAllData() {
        sharedPreferences.edit().clear().apply()
    }
    
    /**
     * Clear only Django-related data
     */
    fun clearDjangoData() {
        sharedPreferences.edit()
            .remove(KEY_DJANGO_TOKEN)
            .remove(KEY_USER_EMAIL)
            .remove(KEY_TOKEN_EXPIRY)
            .apply()
    }
    
    /**
     * Check if we have a valid Django session
     */
    fun hasValidDjangoSession(): Boolean {
        val token = getDjangoToken()
        return token != null && !isDjangoTokenExpired()
    }
    
    /**
     * Encrypt data using AES/GCM
     */
    private fun encrypt(data: String): String {
        val secretKey = getOrCreateSecretKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        
        val encryptedBytes = cipher.doFinal(data.toByteArray())
        val combined = cipher.iv + encryptedBytes
        
        return Base64.encodeToString(combined, Base64.DEFAULT)
    }
    
    /**
     * Decrypt data using AES/GCM
     */
    private fun decrypt(encryptedData: String): String {
        val secretKey = getOrCreateSecretKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        
        val decoded = Base64.decode(encryptedData, Base64.DEFAULT)
        val iv = decoded.copyOfRange(0, GCM_IV_LENGTH)
        val encrypted = decoded.copyOfRange(GCM_IV_LENGTH, decoded.size)
        
        val spec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        
        val decryptedBytes = cipher.doFinal(encrypted)
        return String(decryptedBytes)
    }
    
    /**
     * Get or create the secret key for encryption
     */
    private fun getOrCreateSecretKey(): SecretKey {
        return if (keyStore.containsAlias(KEY_ALIAS)) {
            keyStore.getKey(KEY_ALIAS, null) as SecretKey
        } else {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore"
            )
            
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setUserAuthenticationRequired(false)
                .build()
            
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
    }
} 