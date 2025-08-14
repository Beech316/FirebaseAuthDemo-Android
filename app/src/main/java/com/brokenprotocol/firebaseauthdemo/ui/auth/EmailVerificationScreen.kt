package com.brokenprotocol.firebaseauthdemo.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.brokenprotocol.firebaseauthdemo.ui.components.AuthHeader
import com.brokenprotocol.firebaseauthdemo.ui.components.AuthTextField
import com.brokenprotocol.firebaseauthdemo.ui.components.ErrorMessage
import com.brokenprotocol.firebaseauthdemo.ui.components.PrimaryButton
import com.brokenprotocol.firebaseauthdemo.ui.components.SuccessMessage

@Composable
fun EmailVerificationScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    val authState by viewModel.authState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with back button
        AuthHeader(
            title = "Email Verification",
            onBackClick = { navController.popBackStack() }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Email verification form
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Check your email verification status or resend verification email.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Pre-fill email if user is logged in
            val userEmail = currentUser?.email ?: ""
            if (userEmail.isNotEmpty()) {
                Text(
                    text = "Current user: $userEmail",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            AuthTextField(
                value = email.ifEmpty { userEmail },
                onValueChange = { 
                    email = it
                    emailError = null // Clear error when user types
                },
                label = "Email",
                isError = emailError != null,
                errorMessage = emailError
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Check verification status button
            PrimaryButton(
                text = "Check Verification Status",
                onClick = { 
                    val emailToUse = email.ifEmpty { userEmail }
                    if (emailToUse.isEmpty()) {
                        emailError = "Email is required"
                        return@PrimaryButton
                    }
                    if (!emailToUse.contains("@")) {
                        emailError = "Please enter a valid email address"
                        return@PrimaryButton
                    }
                    
                    // Get Firebase UID from current user or check verification
                    val firebaseUid = currentUser?.firebaseUid
                    if (firebaseUid != null) {
                        viewModel.checkEmailVerification(firebaseUid)
                    } else {
                        viewModel.resendVerificationEmail(emailToUse)
                    }
                },
                loading = authState is AuthState.Loading
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Resend verification email button
            PrimaryButton(
                text = "Resend Verification Email",
                onClick = { 
                    val emailToUse = email.ifEmpty { userEmail }
                    if (emailToUse.isEmpty()) {
                        emailError = "Email is required"
                        return@PrimaryButton
                    }
                    if (!emailToUse.contains("@")) {
                        emailError = "Please enter a valid email address"
                        return@PrimaryButton
                    }
                    viewModel.resendVerificationEmail(emailToUse)
                },
                loading = authState is AuthState.Loading
            )
            
            // Handle auth state
            when (authState) {
                is AuthState.Success -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    SuccessMessage(
                        message = (authState as AuthState.Success).message ?: "Operation completed successfully!"
                    )
                }
                is AuthState.Error -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    ErrorMessage(
                        message = (authState as AuthState.Error).message,
                        onDismiss = { viewModel.clearError() }
                    )
                }
                else -> {
                    // Initial state - no message needed
                }
            }
        }

        // Back to previous screen
        Spacer(modifier = Modifier.height(24.dp))

        TextButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Back")
        }
    }
} 