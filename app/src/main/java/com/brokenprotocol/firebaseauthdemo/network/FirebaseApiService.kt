package com.brokenprotocol.firebaseauthdemo.network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

data class FirebaseAuthResult(
    val success: Boolean,
    val user: FirebaseUser? = null,
    val idToken: String? = null,
    val error: String? = null
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