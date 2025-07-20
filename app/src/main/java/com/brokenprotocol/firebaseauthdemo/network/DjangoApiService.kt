package com.brokenprotocol.firebaseauthdemo.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

data class DjangoResponse(
    val success: Boolean,
    val responseCode: Int,
    val responseBody: String,
    val error: String? = null
)

class DjangoApiService {
    companion object {
        private const val BASE_URL = "http://192.168.0.14:8000/"
    }
    
    private val apiInterface: DjangoApiInterface by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DjangoApiInterface::class.java)
    }
    
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