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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.brokenprotocol.firebaseauthdemo.navigation.NavRoutes
import com.brokenprotocol.firebaseauthdemo.ui.components.ErrorMessage
import com.brokenprotocol.firebaseauthdemo.ui.components.LoadingSpinner
import com.brokenprotocol.firebaseauthdemo.ui.components.PrimaryButton
import com.brokenprotocol.firebaseauthdemo.ui.components.SuccessMessage

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AuthViewModel
) {
    val authState by viewModel.authState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (currentUser != null) {
            // User is authenticated - show Sign Out
            Text(
                text = "Welcome, ${currentUser!!.email}!",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            PrimaryButton(
                text = "Sign Out",
                onClick = { viewModel.signOut() },
                loading = authState is AuthState.Loading
            )
        } else {
            // User is not authenticated - show Sign In and Sign Up
            Text(
                text = "Authentication",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            PrimaryButton(
                text = "Sign In",
                onClick = { navController.navigate(NavRoutes.SignIn.route) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(
                text = "Sign Up",
                onClick = { navController.navigate(NavRoutes.SignUp.route) }
            )
        }
        
        // Handle auth state for feedback
        when (authState) {
            is AuthState.Loading -> {
                Spacer(modifier = Modifier.height(16.dp))
                LoadingSpinner(
                    message = if (currentUser != null) "Signing out..." else "Signing in..."
                )
            }
            is AuthState.Success -> {
                Spacer(modifier = Modifier.height(16.dp))
                SuccessMessage(
                    message = "Authentication successful!"
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
} 