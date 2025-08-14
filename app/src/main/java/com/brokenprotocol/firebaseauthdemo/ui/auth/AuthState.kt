package com.brokenprotocol.firebaseauthdemo.ui.auth

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Success(val user: AuthUser? = null, val message: String? = null) : AuthState()
    data class Error(val message: String, val code: Int? = null) : AuthState()
}

data class AuthUser(
    val email: String,
    val firebaseUid: String,
    val username: String? = null,
    val isEmailVerified: Boolean = false
)

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
} 