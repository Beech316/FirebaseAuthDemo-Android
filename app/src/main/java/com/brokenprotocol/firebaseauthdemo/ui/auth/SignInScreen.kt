package com.brokenprotocol.firebaseauthdemo.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.brokenprotocol.firebaseauthdemo.ui.components.AuthHeader
import com.brokenprotocol.firebaseauthdemo.ui.components.AuthKeyboardOptions
import com.brokenprotocol.firebaseauthdemo.ui.components.AuthTextField
import com.brokenprotocol.firebaseauthdemo.ui.components.ErrorMessage
import com.brokenprotocol.firebaseauthdemo.ui.components.PrimaryButton
import com.brokenprotocol.firebaseauthdemo.ui.components.SuccessMessage

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    val authState by viewModel.authState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with back button
        AuthHeader(
            title = "Sign In",
            onBackClick = { navController.popBackStack() }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Sign in form
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuthTextField(
                value = email,
                onValueChange = { 
                    email = it
                    emailError = null // Clear error when user types
                },
                label = "Email",
                keyboardOptions = AuthKeyboardOptions.Email,
                isError = emailError != null,
                errorMessage = emailError
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AuthTextField(
                value = password,
                onValueChange = { 
                    password = it
                    passwordError = null // Clear error when user types
                },
                label = "Password",
                isPassword = true,
                keyboardOptions = AuthKeyboardOptions.Password,
                isError = passwordError != null,
                errorMessage = passwordError
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            PrimaryButton(
                text = "Sign In",
                onClick = { 
                    // Basic validation
                    if (email.isEmpty()) {
                        emailError = "Email is required"
                        return@PrimaryButton
                    }
                    if (password.isEmpty()) {
                        passwordError = "Password is required"
                        return@PrimaryButton
                    }
                    viewModel.signIn(email, password)
                },
                loading = authState is AuthState.Loading
            )
            
            // Handle auth state
            when (authState) {
                is AuthState.Success -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    SuccessMessage(
                        message = "Sign in successful! Returning to main screen..."
                    )
                    // Navigate back after successful sign in
                    navController.popBackStack()
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
    }
} 