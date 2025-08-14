package com.brokenprotocol.firebaseauthdemo.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DjangoApiService(
    private val apiInterface: DjangoApiInterface
) {
    suspend fun verifyFirebaseToken(idToken: String): VerifyTokenResponse = withContext(Dispatchers.IO) {
        try {
            val request = TokenRequest(id_token = idToken)
            val response = apiInterface.verifyFirebaseToken(request)
            
            if (response.isSuccessful) {
                response.body() ?: VerifyTokenResponse(
                    success = false,
                    error = "Empty response body"
                )
            } else {
                VerifyTokenResponse(
                    success = false,
                    error = "HTTP ${response.code()}: ${response.message()}"
                )
            }
        } catch (e: Exception) {
            VerifyTokenResponse(
                success = false,
                error = "Network error: ${e.message}"
            )
        }
    }
    
    suspend fun registerUser(
        email: String,
        password: String,
        username: String = "",
        firstName: String = "",
        lastName: String = ""
    ): RegisterResponse = withContext(Dispatchers.IO) {
        try {
            val request = RegisterRequest(
                email = email,
                password = password,
                username = username,
                first_name = firstName,
                last_name = lastName
            )
            val response = apiInterface.registerUser(request)
            
            if (response.isSuccessful) {
                response.body() ?: RegisterResponse(
                    success = false,
                    error = "Empty response body"
                )
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                RegisterResponse(
                    success = false,
                    error = "HTTP ${response.code()}: $errorBody"
                )
            }
        } catch (e: Exception) {
            RegisterResponse(
                success = false,
                error = "Network error: ${e.message}"
            )
        }
    }
    
    suspend fun forgotPassword(email: String): ForgotPasswordResponse = withContext(Dispatchers.IO) {
        try {
            val request = ForgotPasswordRequest(email = email)
            val response = apiInterface.forgotPassword(request)
            
            if (response.isSuccessful) {
                response.body() ?: ForgotPasswordResponse(
                    success = false,
                    error = "Empty response body"
                )
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                ForgotPasswordResponse(
                    success = false,
                    error = "HTTP ${response.code()}: $errorBody"
                )
            }
        } catch (e: Exception) {
            ForgotPasswordResponse(
                success = false,
                error = "Network error: ${e.message}"
            )
        }
    }
    
    suspend fun checkEmailVerification(firebaseUid: String): EmailVerificationResponse = withContext(Dispatchers.IO) {
        try {
            val request = CheckEmailVerificationRequest(firebase_uid = firebaseUid)
            val response = apiInterface.checkEmailVerification(request)
            
            if (response.isSuccessful) {
                response.body() ?: EmailVerificationResponse(
                    success = false,
                    error = "Empty response body"
                )
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                EmailVerificationResponse(
                    success = false,
                    error = "HTTP ${response.code()}: $errorBody"
                )
            }
        } catch (e: Exception) {
            EmailVerificationResponse(
                success = false,
                error = "Network error: ${e.message}"
            )
        }
    }
    
    suspend fun resendVerificationEmail(email: String): ResendVerificationResponse = withContext(Dispatchers.IO) {
        try {
            val request = ResendVerificationEmailRequest(email = email)
            val response = apiInterface.resendVerificationEmail(request)
            
            if (response.isSuccessful) {
                response.body() ?: ResendVerificationResponse(
                    success = false,
                    error = "Empty response body"
                )
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                ResendVerificationResponse(
                    success = false,
                    error = "HTTP ${response.code()}: $errorBody"
                )
            }
        } catch (e: Exception) {
            ResendVerificationResponse(
                success = false,
                error = "Network error: ${e.message}"
            )
        }
    }
    
    suspend fun getUserProfile(firebaseUid: String): UserProfileResponse = withContext(Dispatchers.IO) {
        try {
            val response = apiInterface.getUserProfile(firebaseUid)
            
            if (response.isSuccessful) {
                response.body() ?: UserProfileResponse(
                    success = false,
                    error = "Empty response body"
                )
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                UserProfileResponse(
                    success = false,
                    error = "HTTP ${response.code()}: $errorBody"
                )
            }
        } catch (e: Exception) {
            UserProfileResponse(
                success = false,
                error = "Network error: ${e.message}"
            )
        }
    }
    
    suspend fun updateUserProfile(
        firebaseUid: String,
        username: String?,
        firstName: String?,
        lastName: String?,
        phoneNumber: String?
    ): UserProfileResponse = withContext(Dispatchers.IO) {
        try {
            val request = UpdateProfileRequest(
                firebase_uid = firebaseUid,
                username = username,
                first_name = firstName,
                last_name = lastName,
                phone_number = phoneNumber
            )
            val response = apiInterface.updateUserProfile(request)
            
            if (response.isSuccessful) {
                response.body() ?: UserProfileResponse(
                    success = false,
                    error = "Empty response body"
                )
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                UserProfileResponse(
                    success = false,
                    error = "HTTP ${response.code()}: $errorBody"
                )
            }
        } catch (e: Exception) {
            UserProfileResponse(
                success = false,
                error = "Network error: ${e.message}"
            )
        }
    }
    
    suspend fun deleteAccount(firebaseUid: String): DeleteAccountResponse = withContext(Dispatchers.IO) {
        try {
            val request = DeleteAccountRequest(firebase_uid = firebaseUid)
            val response = apiInterface.deleteAccount(request)
            
            if (response.isSuccessful) {
                response.body() ?: DeleteAccountResponse(
                    success = false,
                    error = "Empty response body"
                )
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                DeleteAccountResponse(
                    success = false,
                    error = "HTTP ${response.code()}: $errorBody"
                )
            }
        } catch (e: Exception) {
            DeleteAccountResponse(
                success = false,
                error = "Network error: ${e.message}"
            )
        }
    }
} 