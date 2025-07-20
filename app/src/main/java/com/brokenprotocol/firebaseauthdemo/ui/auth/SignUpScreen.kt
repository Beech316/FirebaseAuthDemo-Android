package com.brokenprotocol.firebaseauthdemo.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.brokenprotocol.firebaseauthdemo.ui.components.*

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    onNavigateToHome: () -> Unit,
    viewModel: AuthViewModel
) {
    val authState by viewModel.authState.collectAsState()
    
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onNavigateToHome()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        AuthHeader(
            title = "Create Account",
            onBackClick = { navController.popBackStack() }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var username by remember { mutableStateOf("") }
        var firstName by remember { mutableStateOf("") }
        var lastName by remember { mutableStateOf("") }
        
        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        AuthTextField(
            value = username,
            onValueChange = { username = it },
            label = "Username",
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = "First Name",
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Words
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = "Last Name",
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                capitalization = KeyboardCapitalization.Words
            ),
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            isPassword = true,
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        AuthTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm Password",
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            isPassword = true,
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        PrimaryButton(
            onClick = {
                if (password == confirmPassword) {
                    viewModel.registerUser(
                        email = email,
                        password = password,
                        username = username,
                        firstName = firstName,
                        lastName = lastName
                    )
                }
            },
            enabled = email.isNotBlank() && 
                     password.isNotBlank() && 
                     confirmPassword.isNotBlank() && 
                     password == confirmPassword &&
                    authState !is AuthState.Loading,
            text = "Sign Up"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (authState is AuthState.Loading) {
            LoadingSpinner()
        }
        
        if (authState is AuthState.Error) {
            ErrorMessage(
                message = (authState as AuthState.Error).message,
                onDismiss = { viewModel.clearError() }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(
            onClick = { navController.popBackStack() }
        ) {
            Text("Already have an account? Sign In")
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
} 