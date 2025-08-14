package com.brokenprotocol.firebaseauthdemo.network

import com.google.gson.annotations.SerializedName

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

data class TokenRequest(
    val id_token: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val username: String = "",
    val first_name: String = "",
    val last_name: String = ""
)

data class ForgotPasswordRequest(
    val email: String
)

data class CheckEmailVerificationRequest(
    val firebase_uid: String
)

data class ResendVerificationEmailRequest(
    val email: String
)

// Endpoint-specific response classes
data class VerifyTokenResponse(
    val success: Boolean,
    val user_id: Int? = null,
    val firebase_uid: String? = null,
    val email: String? = null,
    val username: String? = null,
    val created: Boolean? = null,
    val error: String? = null
)

data class RegisterResponse(
    val success: Boolean,
    val user_id: Int? = null,
    val firebase_uid: String? = null,
    val email: String? = null,
    val username: String? = null,
    val message: String? = null,
    val error: String? = null
)

data class ForgotPasswordResponse(
    val success: Boolean,
    val message: String? = null,
    val error: String? = null
)

data class EmailVerificationResponse(
    val success: Boolean,
    val email_verified: Boolean? = null,
    val email: String? = null,
    val error: String? = null
)

data class ResendVerificationResponse(
    val success: Boolean,
    val message: String? = null,
    val verification_link: String? = null,
    val error: String? = null
)



data class UpdateProfileRequest(
    val firebase_uid: String,
    val username: String?,
    val first_name: String?,
    val last_name: String?,
    val phone_number: String?
)

data class UserProfileResponse(
    val success: Boolean,
    val user: UserProfileData? = null,
    val message: String? = null,
    val error: String? = null
)

data class UserProfileData(
    val id: Int,
    val email: String,
    val username: String?,
    @SerializedName("first_name")
    val firstName: String?,
    @SerializedName("last_name")
    val lastName: String?,
    @SerializedName("phone_number")
    val phoneNumber: String?,
    @SerializedName("profile_picture_url")
    val profilePictureUrl: String?,
    val role: String,
    @SerializedName("is_admin")
    val isAdmin: Boolean,
    @SerializedName("email_verified")
    val emailVerified: Boolean?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
)

data class DeleteAccountRequest(
    val firebase_uid: String
)

data class DeleteAccountResponse(
    val success: Boolean,
    val message: String? = null,
    val error: String? = null
)

interface DjangoApiInterface {
    @POST("auth/verify-token/")
    suspend fun verifyFirebaseToken(@Body request: TokenRequest): Response<VerifyTokenResponse>
    
    @POST("auth/register/")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>
    
    @POST("auth/forgot-password/")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ForgotPasswordResponse>
    
    @POST("auth/check-email-verification/")
    suspend fun checkEmailVerification(@Body request: CheckEmailVerificationRequest): Response<EmailVerificationResponse>
    
    @POST("auth/resend-verification-email/")
    suspend fun resendVerificationEmail(@Body request: ResendVerificationEmailRequest): Response<ResendVerificationResponse>
    
    @GET("auth/profile/")
    suspend fun getUserProfile(@Query("firebase_uid") firebaseUid: String): Response<UserProfileResponse>
    
    @PUT("auth/profile/")
    suspend fun updateUserProfile(@Body request: UpdateProfileRequest): Response<UserProfileResponse>
    
    @POST("auth/delete-account/")
    suspend fun deleteAccount(@Body request: DeleteAccountRequest): Response<DeleteAccountResponse>
} 