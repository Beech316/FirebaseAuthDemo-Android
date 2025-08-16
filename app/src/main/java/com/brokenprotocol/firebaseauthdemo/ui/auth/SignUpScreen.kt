package com.brokenprotocol.firebaseauthdemo.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.brokenprotocol.firebaseauthdemo.ui.components.*

@Composable
fun SignUpScreen(
    navController: NavController,
    onNavigateToSignIn: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                // Navigate to main app or show success message
            }
            is AuthState.Error -> {
                // Error is handled in the UI
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AuthHeader(
            title = "Create Account",
            onBackClick = { navController.popBackStack() }
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            keyboardOptions = AuthKeyboardOptions.Email
        )

        AuthTextField(
            value = username,
            onValueChange = { username = it },
            label = "Username",
            keyboardOptions = AuthKeyboardOptions.Text
        )

        AuthTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = "First Name",
            keyboardOptions = AuthKeyboardOptions.Text
        )

        AuthTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = "Last Name",
            keyboardOptions = AuthKeyboardOptions.Text
        )

        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            isPassword = true,
            keyboardOptions = AuthKeyboardOptions.Password
        )

        AuthTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm Password",
            isPassword = true,
            keyboardOptions = AuthKeyboardOptions.Password
        )

        when (authState) {
            is AuthState.Loading -> {
                LoadingSpinner(message = "Creating account...")
            }
            is AuthState.Error -> {
                ErrorMessage(
                    message = (authState as AuthState.Error).message,
                    onDismiss = { viewModel.clearError() }
                )
            }
            else -> {}
        }

        PrimaryButton(
            onClick = {
                if (password == confirmPassword) {
                    viewModel.signUpUser(
                        email = email,
                        password = password,
                        username = username,
                        firstName = firstName,
                        lastName = lastName
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotBlank() && password.isNotBlank() &&
                    confirmPassword.isNotBlank() && username.isNotBlank() &&
                    firstName.isNotBlank() && lastName.isNotBlank() &&
                    password == confirmPassword,
            text = "Sign Up",
            loading = false
        )

        TextButton(
            onClick = onNavigateToSignIn
        ) {
            Text("Already have an account? Sign In")
        }
    }
} 