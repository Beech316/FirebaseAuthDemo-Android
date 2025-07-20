package com.brokenprotocol.firebaseauthdemo.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class DjangoResponse(
    val success: Boolean,
    val responseCode: Int,
    val responseBody: String,
    val error: String? = null
)

class DjangoApiService(
    private val apiInterface: DjangoApiInterface
) {
    suspend fun verifyFirebaseToken(idToken: String): DjangoResponse = withContext(Dispatchers.IO) {
        try {
            val request = TokenRequest(id_token = idToken)
            val response = apiInterface.verifyFirebaseToken(request)
            
            if (response.isSuccessful) {
                val body = response.body()
                DjangoResponse(
                    success = true,
                    responseCode = response.code(),
                    responseBody = body?.toString() ?: "Empty response body"
                )
            } else {
                DjangoResponse(
                    success = false,
                    responseCode = response.code(),
                    responseBody = response.errorBody()?.string() ?: "Unknown error",
                    error = "HTTP ${response.code()}: ${response.message()}"
                )
            }
        } catch (e: Exception) {
            DjangoResponse(
                success = false,
                responseCode = -1,
                responseBody = "",
                error = "Network error: ${e.message}"
            )
        }
    }
} 