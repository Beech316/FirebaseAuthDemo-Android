package com.brokenprotocol.firebaseauthdemo.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.brokenprotocol.firebaseauthdemo.R
import com.brokenprotocol.firebaseauthdemo.navigation.NavRoutes
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
            title = stringResource(R.string.auth_sign_in_title),
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
                label = stringResource(R.string.label_email),
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
                label = stringResource(R.string.label_password),
                isPassword = true,
                keyboardOptions = AuthKeyboardOptions.Password,
                isError = passwordError != null,
                errorMessage = passwordError
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Forgot Password and Email Verification links
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = { navController.navigate(NavRoutes.EmailVerification.route) }
                ) {
                    Text(text = stringResource(R.string.button_verify_email))
                }
                
                TextButton(
                    onClick = { navController.navigate(NavRoutes.ForgotPassword.route) }
                ) {
                    Text(text = stringResource(R.string.button_forgot_password))
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            val errorEmailRequired = stringResource(R.string.error_email_required)
            val errorPasswordRequired = stringResource(R.string.error_password_required)

            PrimaryButton(
                text = stringResource(R.string.button_sign_in),
                onClick = { 
                    // Basic validation
                    if (email.isEmpty()) {
                        emailError = errorEmailRequired
                        return@PrimaryButton
                    }
                    if (password.isEmpty()) {
                        passwordError = errorPasswordRequired
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
                        message = stringResource(R.string.message_sign_in_successful)
                    )
                    // Navigate back after successful sign in
                    navController.popBackStack()
                }
                is AuthState.Error -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    val errorState = authState as AuthState.Error
                    ErrorMessage(
                        message = errorState.message,
                        onDismiss = { viewModel.clearError() }
                    )
                    
                    // Show additional help for email verification errors
                    if (errorState.code == 17007) {
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = { navController.navigate(NavRoutes.EmailVerification.route) }
                        ) {
                            Text(text = stringResource(R.string.button_resend_verification_email))
                        }
                    }
                }
                else -> {
                    // Initial state - no message needed
                }
            }
        }

        // Sign Up link
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.text_dont_have_account),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(4.dp))
            TextButton(onClick = { navController.navigate(NavRoutes.SignUp.route) }) {
                Text(text = stringResource(R.string.button_sign_up))
            }
        }
    }
} 