package com.brokenprotocol.firebaseauthdemo.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brokenprotocol.firebaseauthdemo.network.DjangoApiService
import com.brokenprotocol.firebaseauthdemo.network.FirebaseApiService
import com.brokenprotocol.firebaseauthdemo.security.SimpleSecureStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseApiService: FirebaseApiService,
    private val djangoApiService: DjangoApiService,
    private val secureStorage: SimpleSecureStorage
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _currentUser = MutableStateFlow<AuthUser?>(null)
    val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()
    
    init {
        // Check Firebase session (handled automatically by Firebase)
        checkFirebaseSession()
    }
    
    /**
     * Check if user is logged in via Firebase
     */
    private fun checkFirebaseSession() {
        val firebaseUser = firebaseApiService.getCurrentUser()
        
        if (firebaseUser != null) {
            // User is logged in via Firebase
            val user = AuthUser(
                email = firebaseUser.email ?: "",
                firebaseUid = firebaseUser.uid,
                username = firebaseUser.displayName,
                isEmailVerified = firebaseUser.isEmailVerified
            )
            _currentUser.value = user
            
            // Check if we have a valid Django session
            if (secureStorage.hasValidDjangoSession()) {
                _authState.value = AuthState.Success(user)
            } else {
                // Need to verify with Django - launch in coroutine
                viewModelScope.launch {
                    val firebaseToken = firebaseUser.getIdToken(false).await().token
                    verifyWithDjango(firebaseToken)
                }
            }
        } else {
            // No Firebase session
            _currentUser.value = null
            _authState.value = AuthState.Initial
        }
    }
    
    /**
     * Verify Firebase token with Django
     */
    private suspend fun verifyWithDjango(firebaseToken: String?) {
        if (firebaseToken == null) {
            _authState.value = AuthState.Error("No Firebase token available")
            return
        }
        
        try {
            val response = djangoApiService.verifyFirebaseToken(firebaseToken)
            
            if (response.success) {
                // Store user session data (Django doesn't return a token, just user info)
                val expiryTimestamp = System.currentTimeMillis() + (24 * 60 * 60 * 1000) // 24 hours
                secureStorage.storeUserEmail(_currentUser.value?.email ?: "")
                secureStorage.storeTokenExpiry(expiryTimestamp)
                
                // For Django, we'll use the Firebase token as the session identifier
                // since Django just verifies the Firebase token
                secureStorage.storeDjangoToken(firebaseToken)
                
                _authState.value = AuthState.Success(_currentUser.value!!)
            } else {
                _authState.value = AuthState.Error("Django verification failed: ${response.error}")
            }
        } catch (e: Exception) {
            _authState.value = AuthState.Error("Django verification error: ${e.message}")
        }
    }
    
    fun signIn(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Please enter email and password")
            return
        }
        
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                
                // Firebase authentication
                val firebaseResult = firebaseApiService.signInWithEmailAndPassword(email, password)
                
                if (firebaseResult.success && firebaseResult.idToken != null) {
                    // Django token verification
                    val djangoResponse = djangoApiService.verifyFirebaseToken(firebaseResult.idToken)
                    
                    if (djangoResponse.success) {
                        // Create AuthUser from successful response
                        val user = AuthUser(
                            email = firebaseResult.user?.email ?: email,
                            firebaseUid = firebaseResult.user?.uid ?: "",
                            username = firebaseResult.user?.displayName,
                            isEmailVerified = firebaseResult.user?.isEmailVerified ?: false
                        )
                        
                        // Store Django session data
                        val expiryTimestamp = System.currentTimeMillis() + (24 * 60 * 60 * 1000) // 24 hours
                        secureStorage.storeUserEmail(user.email)
                        secureStorage.storeTokenExpiry(expiryTimestamp)
                        
                        // For Django, we'll use the Firebase token as the session identifier
                        // since Django just verifies the Firebase token
                        secureStorage.storeDjangoToken(firebaseResult.idToken)
                        
                        // Update ViewModel state
                        _currentUser.value = user
                        _authState.value = AuthState.Success(user)
                    } else {
                        _authState.value = AuthState.Error(
                            "Django Error: ${djangoResponse.error ?: djangoResponse.responseBody}"
                        )
                    }
                } else {
                    _authState.value = AuthState.Error("Firebase auth failed: ${firebaseResult.error}")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error: ${e.message}")
            }
        }
    }
    
    fun signOut() {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                
                // Sign out from Firebase (handles its own session)
                firebaseApiService.signOut()
                
                // Clear Django session data
                secureStorage.clearDjangoData()
                
                // Update ViewModel state
                _currentUser.value = null
                _authState.value = AuthState.Initial
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error signing out: ${e.message}")
            }
        }
    }
    
    fun refreshDjangoSession() {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                
                val firebaseUser = firebaseApiService.getCurrentUser()
                if (firebaseUser != null) {
                    val firebaseToken = firebaseUser.getIdToken(false).await().token
                    verifyWithDjango(firebaseToken)
                } else {
                    _authState.value = AuthState.Error("No Firebase user available")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Session refresh failed: ${e.message}")
            }
        }
    }
    
    fun getCurrentUser(): AuthUser? {
        return _currentUser.value
    }
    
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Initial
        }
    }
    
    /**
     * Get session information for debugging
     */
    fun getSessionInfo(): Map<String, Any> {
        val firebaseUser = firebaseApiService.getCurrentUser()
        return mapOf(
            "firebaseUser" to (firebaseUser?.email ?: "null"),
            "hasDjangoToken" to (secureStorage.getDjangoToken() != null),
            "isDjangoTokenExpired" to secureStorage.isDjangoTokenExpired(),
            "hasValidDjangoSession" to secureStorage.hasValidDjangoSession(),
            "currentUser" to (_currentUser.value?.email ?: "null"),
            "tokenExpiry" to secureStorage.getTokenExpiry()
        )
    }
} 