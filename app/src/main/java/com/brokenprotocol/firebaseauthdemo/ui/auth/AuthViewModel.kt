package com.brokenprotocol.firebaseauthdemo.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.brokenprotocol.firebaseauthdemo.network.DjangoApiService
import com.brokenprotocol.firebaseauthdemo.network.FirebaseApiService
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val firebaseApiService: FirebaseApiService,
    private val djangoApiService: DjangoApiService
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _currentUser = MutableStateFlow<AuthUser?>(null)
    val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()
    
    init {
        // Initialize current user on ViewModel creation
        _currentUser.value = getCurrentUserFromFirebase()
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
                firebaseApiService.signOut()
                _currentUser.value = null
                _authState.value = AuthState.Initial
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Error signing out: ${e.message}")
            }
        }
    }
    
    private fun getCurrentUserFromFirebase(): AuthUser? {
        val currentUser = firebaseApiService.getCurrentUser()
        return if (currentUser != null) {
            AuthUser(
                email = currentUser.email ?: "",
                firebaseUid = currentUser.uid,
                username = currentUser.displayName,
                isEmailVerified = currentUser.isEmailVerified
            )
        } else null
    }
    
    fun getCurrentUser(): AuthUser? {
        return _currentUser.value
    }
    
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Initial
        }
    }
} 