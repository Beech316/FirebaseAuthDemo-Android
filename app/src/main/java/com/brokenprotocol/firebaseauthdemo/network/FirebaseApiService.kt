package com.brokenprotocol.firebaseauthdemo.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.tasks.await

data class FirebaseAuthResult(
    val success: Boolean,
    val user: FirebaseUser? = null,
    val idToken: String? = null,
    val error: String? = null,
    val errorCode: String? = null,
    val requiresEmailVerification: Boolean = false
)

class FirebaseApiService {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    
    suspend fun signInWithEmailAndPassword(email: String, password: String): FirebaseAuthResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            
            if (user != null) {
                val idToken = user.getIdToken(false).await().token
                FirebaseAuthResult(
                    success = true,
                    user = user,
                    idToken = idToken
                )
            } else {
                FirebaseAuthResult(
                    success = false,
                    error = "User is null after successful authentication"
                )
            }
        } catch (e: FirebaseAuthInvalidUserException) {
            when (e.errorCode) {
                "ERROR_USER_NOT_FOUND" -> {
                    FirebaseAuthResult(
                        success = false,
                        error = "No account found with this email address",
                        errorCode = "USER_NOT_FOUND"
                    )
                }
                "ERROR_USER_DISABLED" -> {
                    FirebaseAuthResult(
                        success = false,
                        error = "This account has been disabled",
                        errorCode = "USER_DISABLED"
                    )
                }
                "ERROR_EMAIL_NOT_VERIFIED" -> {
                    FirebaseAuthResult(
                        success = false,
                        error = "Please verify your email address before signing in",
                        errorCode = "EMAIL_NOT_VERIFIED",
                        requiresEmailVerification = true
                    )
                }
                else -> {
                    FirebaseAuthResult(
                        success = false,
                        error = "Invalid user: ${e.message}",
                        errorCode = e.errorCode
                    )
                }
            }
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            when (e.errorCode) {
                "ERROR_INVALID_PASSWORD" -> {
                    FirebaseAuthResult(
                        success = false,
                        error = "Incorrect password",
                        errorCode = "INVALID_PASSWORD"
                    )
                }
                "ERROR_INVALID_EMAIL" -> {
                    FirebaseAuthResult(
                        success = false,
                        error = "Invalid email format",
                        errorCode = "INVALID_EMAIL"
                    )
                }
                else -> {
                    FirebaseAuthResult(
                        success = false,
                        error = "Invalid credentials: ${e.message}",
                        errorCode = e.errorCode
                    )
                }
            }
        } catch (e: Exception) {
            FirebaseAuthResult(
                success = false,
                error = e.message ?: "Unknown authentication error"
            )
        }
    }
    
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
    
    fun signOut() {
        auth.signOut()
    }
} 