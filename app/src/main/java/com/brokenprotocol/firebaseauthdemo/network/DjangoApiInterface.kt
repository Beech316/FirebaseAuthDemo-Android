package com.brokenprotocol.firebaseauthdemo.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class TokenRequest(
    val id_token: String
)

data class DjangoApiResponse(
    val success: Boolean,
    val user_id: Int? = null,
    val firebase_uid: String? = null,
    val email: String? = null,
    val username: String? = null,
    val created: Boolean? = null,
    val error: String? = null
)

interface DjangoApiInterface {
    @POST("auth/verify-token/")
    suspend fun verifyFirebaseToken(@Body request: TokenRequest): Response<DjangoApiResponse>
} 